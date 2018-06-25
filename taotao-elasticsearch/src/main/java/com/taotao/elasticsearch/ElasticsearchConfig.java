package com.taotao.elasticsearch;

import com.taotao.elasticsearch.common.HostUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 使用javaConfig方式配置 es资源
 */
@Configuration
@ComponentScan(basePackageClasses = ElasticsearchClient.class)
public class ElasticsearchConfig
{
    // region 资源配置

    /**
     * 服务地址 例如："localhost:9200, 192.168.1.200:9200"
     */
    @Value("${httpHost.hosts}")
    private String hosts;

    /**
     * schema方案
     */
    @Value("${httpHost.schema}")
    private String schema;

    /**
     * http最大连接值
     */
    @Value("${esclient.connectNum}")
    private Integer connectNum;

    /**
     * http路由值的最大连接
     */
    @Value("${esclient.connectPerRoute}")
    private Integer connectPerRoute;

    // endregion

    // region bean配置

    /**
     * es 配置
     *
     * @return host数组
     */
    @Bean
    public ESConfig esConfig()
    {
        ESConfig esConfig = new ESConfig();
        esConfig.setConnectNum(connectNum);
        esConfig.setConnectPerRoute(connectPerRoute);
        esConfig.setHosts(hosts);
        esConfig.setSchema(schema);
        return esConfig;
    }


    /**
     * host 配置
     *
     * @return host数组
     */
    @Bean
    public HttpHost[] httpHost()
    {
        return HostUtils.getHttpHost(hosts, schema);
    }

    /**
     * es客户端工厂配置.
     *
     * @return ElasticsearchClient
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public ElasticsearchClient getFactory()
    {
        return ElasticsearchClient.build(httpHost(), connectNum, connectPerRoute);
    }

    /**
     * es客户端配置.
     *
     * @return RestHighLevelClient
     */
    @Bean
    public RestHighLevelClient getRestClient()
    {
        return getFactory().getClient();
    }

    // endregion
}
