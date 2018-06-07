package com.taotao.elasticsearch;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

/**
 * ES搜索引擎基类
 */
public class ElasticsearchIndex
{
    // region 成员

    /**
     * 服务地址 例如："http://localhost:9527/, http://192.168.1.200:9527/"
     */
    private String host;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * es客户端
     */
    private TransportClient client;

    // endregion

    // region 构造函数

    /**
     * 够着函数
     *
     * @param host      例如："http://localhost:9527/, http://192.168.1.200:9527/"
     * @param indexName 索引名称，全小写
     */
    public ElasticsearchIndex(String host, String indexName) throws Exception
    {
        if(host == null || host.isEmpty())
        {
            throw new ElasticsearchException("搜索服务`host`不能为空");
        }

        if(indexName == null || indexName.isEmpty())
        {
            throw new ElasticsearchException("搜索服务索引名称`indexName`不能为空");
        }

        this.host = host;
        this.indexName = indexName;

        Settings settings = Settings.builder().put("cluster.name", indexName).build();

        // .put("client.transport.sniff", true).build(); 使用嗅探
        // .put("client.transport.ignore_cluster_name ", true).build(); 忽略链接节点
        // .put("client.transport.ping_timeout", 5).build(); 设置`ping`响应时间，默认5秒
        // .put("client.transport.nodes_sampler_interval", true).build(); 对列出和连接的节点进行`ping`的频率，默认5秒

        client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));

    }

    // endregion

    // region 公共方法


    /**
     * 关闭客户端连接.
     */
    public void Close()
    {
        if(client != null)
        {
            client.close();
        }
    }

    // endregion

    // region 私有方法



    // endregion

    // region getter/setter

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost()
    {
        return host;
    }

    /**
     * Gets index name.
     *
     * @return the index name
     */
    public String getIndexName()
    {
        return indexName;
    }

    /**
     * Sets index name.
     *
     * @param indexName the index name
     */
    protected void setIndexName(String indexName)
    {
        this.indexName = indexName;
    }

    /**
     * Gets client.
     *
     * @return the client
     */
    public TransportClient getClient()
    {
        return client;
    }

    // endregion
}
