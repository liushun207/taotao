package com.taotao.elasticsearch;

import com.taotao.elasticsearch.common.HostUtils;
import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * es批量操作服务
 **/
public class ElasticsearchBulkProcessor
{
    // region 成员

    /**
     * log4j 日志
     */
    private static Logger logger = Logger.getLogger(ElasticsearchBulkProcessor.class);

    private static BulkProcessor bulkProcessor;

    @Autowired
    private static ESConfig esConfig;

    // endregion

    // region 单例模式

    public static BulkProcessor getInstance()
    {
        if (bulkProcessor == null)
        {
            synchronized (BulkProcessor.class)
            {
                if (bulkProcessor == null)
                {
                    init();
                }
            }
        }
        return bulkProcessor;
    }

    // endregion

    // region 初始化

    private static void init()
    {
        HttpHost[] hosts = HostUtils.getHttpHost(esConfig.getHosts(), esConfig.getSchema());

        ElasticsearchClient client = ElasticsearchClient.build(hosts);
        client.init();

        BulkProcessor.Listener listener = new BulkProcessor.Listener()
        {
            @Override
            public void beforeBulk(long executionId, BulkRequest request)
            {
                int numberOfActions = request.numberOfActions();
                String message  ="---尝试处理[" + numberOfActions + "]条数据---";
                logger.info(message);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response)
            {
                int numberOfActions = request.numberOfActions();
                String message  ="---尝试处理[" + numberOfActions + "]条数据成功---";
                logger.info(message);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure)
            {
                logger.error("[es批量处理`bulkProcessor`错误信息]：" + failure.toString());
            }
        };

        // 此processer的含义为如果消息数量到达500
        // 或者消息大小到大1M 或者时间达到10s
        // 任意条件满足，客户端就会把当前的数据提交到服务端处理。效率很高。
        BulkProcessor.Builder builder = BulkProcessor.builder(client.getClient()::bulkAsync, listener);

        // 设置什么时候根据当前添加的动作数量来刷新一个新的批量请求（默认为1000，使用-1来禁用它）
        builder.setBulkActions(500);

        // 设置何时根据当前添加的动作的大小来刷新一个新的批量请求（默认为5 Mb，使用-1来禁用它）
        builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));

        // 设定允许执行的并发请求的数量（默认为1，使用0只允许执行单个请求）
        builder.setConcurrentRequests(0);

        // 设置一个刷新间隔，在间隔通过时刷新任何容积请求（默认值不设置）
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));

        bulkProcessor = builder.build();
    }

    // endregion
}
