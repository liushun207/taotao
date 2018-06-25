package com.taotao.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

/**
 * es客户端
 */
public class ElasticsearchClient
{
    // region 静态成员 取 RestClientBuilder 中的默认值

    /**
     * 连接超时时间.
     */
    public static int CONNECT_TIMEOUT_MILLIS = 1000;

    /**
     * Socket超时时间.
     */
    public static int SOCKET_TIMEOUT_MILLIS = 30000;

    /**
     * 连接请求超时时间.
     */
    public static int CONNECTION_REQUEST_TIMEOUT_MILLIS = 500;

    /**
     * http路由值的最大连接。
     */
    public static int MAX_CONN_PER_ROUTE = 10;

    /**
     * http最大连接值
     */
    public static int MAX_CONN_TOTAL = 30;

    // endregion

    // region 私有成员

    private static HttpHost[] HTTP_HOSTS;
    private RestClientBuilder builder;
    private RestHighLevelClient restHighLevelClient;

    private static ElasticsearchClient elasticsearchClient = new ElasticsearchClient();

    // endregion

    // region 构造函数

    private ElasticsearchClient()
    {
    }

    // endregion

    // region Build

    /**
     * Build elasticsearch client.
     *
     * @param httpHosts          服务地址
     * @return the elasticsearch client
     */
    public static ElasticsearchClient build(HttpHost[] httpHosts)
    {
        HTTP_HOSTS = httpHosts;
        return  elasticsearchClient;
    }

    /**
     * Build elasticsearch client.
     *
     * @param httpHosts          服务地址
     * @param maxConnectNum      http最大连接值
     * @param maxConnectPerRoute http路由值的最大连接
     * @return the elasticsearch client
     */
    public static ElasticsearchClient build(HttpHost[] httpHosts, Integer maxConnectNum, Integer maxConnectPerRoute)
    {
        HTTP_HOSTS = httpHosts;
        MAX_CONN_TOTAL = maxConnectNum;
        MAX_CONN_PER_ROUTE = maxConnectPerRoute;
        return  elasticsearchClient;
    }

    /**
     * Build elasticsearch client.
     *
     * @param httpHosts             服务地址
     * @param connectTimeOut        连接超时时间
     * @param socketTimeOut         Socket超时时间
     * @param connectionRequestTime 连接请求超时时间
     * @param maxConnectNum         http最大连接值
     * @param maxConnectPerRoute    http路由值的最大连接
     * @return the elasticsearch client
     */
    public static ElasticsearchClient build(HttpHost[] httpHosts, Integer connectTimeOut, Integer socketTimeOut, Integer connectionRequestTime,Integer maxConnectNum, Integer maxConnectPerRoute)
    {
        HTTP_HOSTS = httpHosts;
        CONNECT_TIMEOUT_MILLIS = connectTimeOut;
        SOCKET_TIMEOUT_MILLIS = socketTimeOut;
        CONNECTION_REQUEST_TIMEOUT_MILLIS = connectionRequestTime;
        MAX_CONN_TOTAL = maxConnectNum;
        MAX_CONN_PER_ROUTE = maxConnectPerRoute;
        return elasticsearchClient;
    }

    // endregion

    // region 初始化连接及设置

    /**
     * 初始化
     */
    public void init()
    {
        builder = RestClient.builder(HTTP_HOSTS);

        // 设置配置
        setConnectTimeOutConfig();
        setMutiConnectConfig();

        restHighLevelClient = new RestHighLevelClient(builder);
    }

    /**
     * 配置连接时间延时
     */
    public void setConnectTimeOutConfig()
    {
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback()
        {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder)
            {
                requestConfigBuilder.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
                requestConfigBuilder.setSocketTimeout(SOCKET_TIMEOUT_MILLIS);
                requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLIS);
                return requestConfigBuilder;
            }
        });
    }

    /**
     * 使用异步httpclient时设置并发连接数
     */
    public void setMutiConnectConfig()
    {
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback()
        {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
            {
                httpClientBuilder.setMaxConnTotal(MAX_CONN_TOTAL);
                httpClientBuilder.setMaxConnPerRoute(MAX_CONN_PER_ROUTE);
                return httpClientBuilder;
            }
        });
    }

    // endregion

    // region 其它

    /**
     * 获取 rest high api client
     * @return
     */
    public RestHighLevelClient getClient()
    {
        return restHighLevelClient;
    }

    /**
     * 关闭连接
     */
    public void close()
    {
        if (restHighLevelClient != null)
        {
            try
            {
                restHighLevelClient.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // endregion
}
