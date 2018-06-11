package com.taotao.elasticsearch;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ES搜索引擎基类
 */
public class ElasticsearchIndex implements Closeable
{
    // region 成员

    /**
     * 服务地址 例如："localhost, 192.168.1.200"
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
     * @param host      例如："localhost,192.168.1.200"
     * @param indexName 索引名称，全小写
     */
    public ElasticsearchIndex(String host, String indexName)
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
        this.indexName = indexName.toLowerCase();

        Settings settings = Settings.builder()
                // 索引名称
                .put("cluster.name", indexName)
                // 集群节点自动探查, 去嗅探整个集群的状态，把集群中其它机器的ip地址加到客户端中
                .put("client.transport.sniff", true)
                .build();

        // .put("client.transport.ignore_cluster_name ", true); 忽略链接节点
        // .put("client.transport.ping_timeout", 5); 设置`ping`响应时间，默认5秒
        // .put("client.transport.nodes_sampler_interval", true); 对列出和连接的节点进行`ping`的频率，默认5秒

        client = new PreBuiltTransportClient(settings);
    }

    // endregion

    // region 公共方法

    /**
     * 为集群添加新的节点
     *
     * @param ip ip
     */
    public synchronized void addNode(String ip)
    {
        try
        {
            client.addTransportAddress(new TransportAddress(InetAddress.getByName(ip), 9300));
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 创建索引与 mapping 模板
     *
     * @param type    Mapping类型（文档类型）
     * @param mapping mapping
     */
    public void createMapping(String type, XContentBuilder mapping)
    {
        // 创建索引
        CreateIndexRequestBuilder cib = client.admin().indices().prepareCreate(this.indexName);
        cib.addMapping(type, mapping);
        cib.execute().actionGet();
    }

    // endregion

    // region 资源释放

    /**
     * 关闭客户端连接.
     * 使用try(....)时，自动调用
     */
    @Override
    public void close() throws IOException
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

    // region mapping例子

    //XContentBuilder mapping = XContentFactory.jsonBuilder()
    //        .startObject()
    //        // 设置自定义字段
    //        .startObject("properties")
    //
    //        // 字段id
    //        .startObject("id")
    //        // 设置数据类型
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("classs")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("courseClass")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("courseClassExam")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("examnum")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("ok")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("room")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("score")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("student")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("updatetime")
    //        .field("type","integer")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("desc")
    //        .field("type","text")
    //        // 设置分词
    //        .field("analyzer","ik_smart")
    //        .endObject()
    //
    //        .startObject("name")
    //        .field("type","string")
    //        .field("index","not_analyzed")
    //        .endObject()
    //
    //        .startObject("inputtime")
    //        .field("type","date")  //设置Date类型
    //        .field("format","yyyy-MM-dd HH:mm:ss") //设置Date的格式
    //        .endObject()
    //        .endObject()
    //        .endObject();

    // endregion
}
