package com.taotao.controller;

import com.taotao.common.jedis.JedisClient;
import com.taotao.common.pojo.EUDataGridResult;
import com.taotao.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Jedis 缓存测试
 */
@Controller
public class JedisController
{
    /**
     * 注入jedis 客户端
     */
    @Autowired
    private JedisClient jedisClient;

    private String key = "test";

    /**
     * Sets jedis.
     * @return the jedis
     */
    @RequestMapping("/jedis/set")
    @ResponseBody
    public Boolean setJedis()
    {
        String str = "测试";
        jedisClient.set(key, str);
        jedisClient.hset(key + "1", 1 + "", str);
        return true;
    }

    /**
     * Gets jedis.
     * @return the jedis
     */
    @RequestMapping("/jedis/get")
    @ResponseBody
    public String getJedis()
    {
        String result = jedisClient.hget(key + "1", 1 + "");
        return result;
    }
}
