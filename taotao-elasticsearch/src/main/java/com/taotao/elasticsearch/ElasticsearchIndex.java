package com.taotao.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * ES搜索引擎基类
 */
public class ElasticsearchIndex implements Closeable
{
    // region 成员

    /**
     * 方案
     */
    private static String ES_SCHEME = "http";

    /**
     * 分片数量
     */
    private static int NUMBER_OF_SHARDS = 5;

    /**
     * 副本数量
     */
    private static int NUMBER_OF_REPLICAS = 1;

    /**
     * 服务地址 例如："localhost:9200, 192.168.1.200:9200"
     */
    private String host;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * es客户端
     */
    private RestHighLevelClient client;

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

        initClient();
    }

    // endregion

    // region 公共方法

    /**
     * 创建索引与 mapping 模板
     *
     * @param type    Mapping类型（文档类型）
     * @param mapping mapping
     * @return the create index response
     * @throws IOException the io exception
     */
    public CreateIndexResponse createIndex(String type, XContentBuilder mapping) throws IOException
    {
        // 创建索引
        CreateIndexRequest request = new CreateIndexRequest(this.indexName);

        // 设置索引分片、副本
        request.settings(Settings.builder()
                .put("index.number_of_shards", NUMBER_OF_SHARDS)
                .put("index.number_of_replicas", NUMBER_OF_REPLICAS)
        );

        // 设置类型映射
        request.mapping(type, mapping);

        // 创建
        CreateIndexResponse response = client.indices().create(request);

        return response;
    }

    /**
     * 删除索引
     *
     * @throws IOException the io exception
     */
    public void deleteIndex() throws IOException
    {
        try
        {
            DeleteIndexRequest request = new DeleteIndexRequest(this.indexName);
            client.indices().delete(request);
        }
        catch (ElasticsearchException exception)
        {
            // 索引没有找到，表示删除成功
            if (exception.status() == RestStatus.NOT_FOUND)
            {
                return;
            }

            throw exception;
        }
    }

    /**
     * 索引是否存在
     *
     * @return true 存在
     * @throws IOException the io exception
     */
    public boolean indexExists() throws IOException
    {
        GetRequest request = new GetRequest(this.indexName);
        boolean response = client.exists(request);
        return response;
    }

    // region 增、删、改

    /**
     * 新增一个文档.
     *
     * @param type   文档类型
     * @param id     数据主键Id
     * @param source 数据(json格式)
     * @return true 成功
     * @throws IOException the io exception
     */
    public boolean indexDocument(String type, String id, String source) throws IOException
    {
        IndexRequest request = new IndexRequest(this.indexName, type, id);

        request.source(source, XContentType.JSON);

        IndexResponse indexResponse = client.index(request);

        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();

        // 处理成功分片数量少于总分片数量的情况
        if (shardInfo.getTotal() != shardInfo.getSuccessful())
        {
            throw new IOException("成功分片数量少于总分片数量");
        }

        if (shardInfo.getFailed() > 0)
        {
            // 处理潜在的失败
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures())
            {
                String reason = failure.reason();
                throw new IOException(reason);
            }
        }

        return true;
    }

    /**
     * 删除一个文档.
     *
     * @param type 文档类型
     * @param id   数据主键Id
     * @return true 成功
     * @throws IOException the io exception
     */
    public boolean deleteDocument(String type, String id) throws IOException
    {
        DeleteRequest request = new DeleteRequest(this.indexName, type, id);

        DeleteResponse deleteResponse = client.delete(request);

        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();

        // 处理成功分片数量少于总分片数量的情况
        if (shardInfo.getTotal() != shardInfo.getSuccessful())
        {
            throw new IOException("成功分片数量少于总分片数量");
        }

        if (shardInfo.getFailed() > 0)
        {
            // 处理潜在的失败
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures())
            {
                String reason = failure.reason();
                throw new IOException(reason);
            }
        }

        return true;
    }

    /**
     * 更新一个文档.
     *
     * @param type   文档类型
     * @param id     数据主键Id
     * @param source 数据(json格式)
     * @return true 成功
     * @throws IOException the io exception
     */
    public boolean updageDocument(String type, String id, String source) throws IOException
    {
        UpdateRequest request = new UpdateRequest(this.indexName, type, id);

        request.doc(source, XContentType.JSON);

        UpdateResponse updateResponse = client.update(request);

        return true;
    }

    // endregion

    // region 查询

    /**
     * 获取一个文档.
     *
     * @param type 文档类型
     * @param id   数据主键Id
     * @return string 返回json数据
     * @throws IOException the io exception
     */
    public String getDocument(String type, String id) throws IOException
    {
        GetRequest request = new GetRequest(this.indexName, type, id);

        try
        {
            GetResponse getResponse = client.get(request);

            if (getResponse.isExists())
            {
                String source = getResponse.getSourceAsString();
                return source;
            }
            else
            {
                return null;
            }
        }
        catch (ElasticsearchException e)
        {
            // 未发现
            if (e.status() == RestStatus.NOT_FOUND)
            {
                return null;
            }

            throw e;
        }
    }

    // endregion

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
            try
            {
                client.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // endregion

    // region 私有方法

    /**
     * 初始化client
     */
    private void initClient()
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

            hosts[i] = new HttpHost(hostName, Integer.valueOf(port), ES_SCHEME);
        }

        RestClientBuilder builder = RestClient.builder(hosts);

        client = new RestHighLevelClient(builder);
    }

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
    public RestHighLevelClient getClient()
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
