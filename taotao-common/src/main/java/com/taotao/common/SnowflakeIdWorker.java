package com.taotao.common;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
public class SnowflakeIdWorker
{

    // ==============================Fields===========================================
    /**
     * 开始时间截 (2015-01-01)
     */
    private final long twepoch = 1420041600000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private final long datacenterIdBits = 5L;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    private long datacenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdWorker(long workerId, long datacenterId)
    {
        if (workerId > maxWorkerId || workerId < 0)
        {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0)
        {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId()
    {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp)
        {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp)
        {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0)
            {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else
        {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp)
    {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp)
        {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen()
    {
        return System.currentTimeMillis();
    }

    // region 測試

    public static void testProductIdByMoreThread(int dataCenterId, int workerId, int n) throws InterruptedException
    {
        List<Thread> tlist = new ArrayList<>();
        Set<Long> setAll = new HashSet<>();
        CountDownLatch cdLatch = new CountDownLatch(10);
        long start = System.currentTimeMillis();
        int threadNo = dataCenterId;
        Map<String, SnowflakeIdWorker> idFactories = new HashMap<>();
        for (int i = 0; i < 10; i++)
        {
            //用线程名称做map key.
            idFactories.put("snowflake" + i, new SnowflakeIdWorker(workerId, threadNo++));
        }
        for (int i = 0; i < 10; i++)
        {
            Thread temp = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Set<Long> setId = new HashSet<>();
                    SnowflakeIdWorker idWorker = idFactories.get(Thread.currentThread().getName());
                    for (int j = 0; j < n; j++)
                    {
                        setId.add(idWorker.nextId());
                    }
                    synchronized (setAll)
                    {
                        setAll.addAll(setId);
                        //log.info("{}生产了{}个id,并成功加入到setAll中.", Thread.currentThread().getName(), n);
                    }
                    cdLatch.countDown();
                }
            }, "snowflake" + i);
            tlist.add(temp);
        }
        for (int j = 0; j < 10; j++)
        {
            tlist.get(j).start();
        }
        cdLatch.await();

        long end1 = System.currentTimeMillis() - start;

        //log.info("共耗时:{}毫秒,预期应该生产{}个id, 实际合并总计生成ID个数:{}", end1, 10 * n, setAll.size());

    }

    public static void testProductId(int dataCenterId, int workerId, int n)
    {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(workerId, dataCenterId);
        SnowflakeIdWorker idWorker2 = new SnowflakeIdWorker(workerId + 1, dataCenterId);
        Set<Long> setOne = new HashSet<>();
        Set<Long> setTow = new HashSet<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++)
        {
            setOne.add(idWorker.nextId());//加入set
        }
        long end1 = System.currentTimeMillis() - start;
        //log.info("第一批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}", n, setOne.size(), end1);

        for (int i = 0; i < n; i++)
        {
            setTow.add(idWorker2.nextId());//加入set
        }
        long end2 = System.currentTimeMillis() - start;
        //log.info("第二批ID预计生成{}个,实际生成{}个<<<<*>>>>共耗时:{}", n, setTow.size(), end2);

        setOne.addAll(setTow);
        //log.info("合并总计生成ID个数:{}", setOne.size());
    }

    public static void testPerSecondProductIdNums()
    {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 2);
        long start = System.currentTimeMillis();
        int count = 0;
        for (int i = 0; System.currentTimeMillis() - start < 1000; i++, count = i)
        {
            /**  测试方法一: 此用法纯粹的生产ID,每秒生产ID个数为300w+ */
            idWorker.nextId();
            /**  测试方法二: 在log中打印,同时获取ID,此用法生产ID的能力受限于log.error()的吞吐能力.
             * 每秒徘徊在10万左右. */
            //log.error("{}",idWorker.nextId());
        }
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
        System.out.println(count);
    }

    public static void main(String[] args)
    {
        /** case1: 测试每秒生产id个数?
         *   结论: 每秒生产id个数300w+ */
        //testPerSecondProductIdNums();

        /** case2: 单线程-测试多个生产者同时生产N个id,验证id是否有重复?
         *   结论: 验证通过,没有重复. */
        //testProductId(1,2,10000);//验证通过!
        //testProductId(1,2,20000);//验证通过!

        /** case3: 多线程-测试多个生产者同时生产N个id, 全部id在全局范围内是否会重复?
         *   结论: 验证通过,没有重复. */
        try
        {
            //单机测试此场景,性能损失至少折半!
            testProductIdByMoreThread(1, 2, 100000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    // endregion
}
