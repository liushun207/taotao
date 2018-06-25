package com.taotao.elasticsearch.common;

import org.apache.http.HttpHost;
import org.springframework.util.Assert;

/**
 * host工具.
 */
public class HostUtils
{
    /**
     * 获取hosts.
     *
     * @param host   服务地址 例如："localhost:9200, 192.168.1.200:9200"
     * @param schema 方案
     * @return hosts
     */
    public static HttpHost[] getHttpHost(String host, String schema)
    {
        String[] hostArr = host.split(",");

        int len = hostArr.length;

        HttpHost[] hosts = new HttpHost[len];

        for (int i = 0; i < len; i++)
        {
            String[] uriArr = hostArr[i].split(":");

            String hostName = uriArr[0];
            String port =  uriArr[1];

            Assert.hasText(hostName, "[Assertion failed] missing host name in 'clusterNodes'");
            Assert.hasText(port, "[Assertion failed] missing port in 'clusterNodes'");

            hosts[i] = new HttpHost(hostName, Integer.valueOf(port), schema);
        }

        return hosts;
    }
}
