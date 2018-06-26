package com.taotao.elasticsearch;

import com.taotao.elasticsearch.common.BulkItem;
import com.taotao.elasticsearch.common.BulkOperation;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * ES搜索引擎基类
 */
public class ElasticsearchIndex implements Closeable
{
    // region 成员

    /**
     * log4j 日志
     */
    private static Logger logger = Logger.getLogger(ElasticsearchIndex.class);

    /**
     * 分片数量
     */
    private static int NUMBER_OF_SHARDS = 5;

    /**
     * 副本数量
     */
    private static int NUMBER_OF_REPLICAS = 1;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * es客户端，根据 ElasticsearchConfig 注入
     */
    @Autowired
    private RestHighLevelClient client;

    // endregion

    // region 构造函数

    /**
     * 够着函数
     *
     * @param indexName 索引名称，全小写
     */
    public ElasticsearchIndex(String indexName)
    {
        checkIndexName(indexName);

        this.indexName = indexName.toLowerCase();
    }

    // endregion

    // region 公共方法

    // region 索引相关

    /**
     * 创建索引与 mapping 模板
     *
     * @param type    Mapping类型（文档类型）
     * @param mapping 映射
     * @return true 成功
     * @throws IOException the io exception
     */
    public boolean createIndex(String type, XContentBuilder mapping) throws IOException
    {
        checkType(type);

        if(mapping == null)
        {
            throw new ElasticsearchException("`mapping`映射不能为空");
        }

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

        return true;
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

    // endregion

    // region 文档相关 增、删、改

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
        checkType(type);
        checkId(id);
        checkSource(source);

        IndexRequest request = new IndexRequest(this.indexName, type, id);

        request.source(source, XContentType.JSON);

        IndexResponse indexResponse = client.index(request);

        checkShard(indexResponse.getShardInfo());

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
        checkType(type);
        checkId(id);

        DeleteRequest request = new DeleteRequest(this.indexName, type, id);

        DeleteResponse deleteResponse = client.delete(request);

        checkShard(deleteResponse.getShardInfo());

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
        checkType(type);
        checkId(id);
        checkSource(source);

        UpdateRequest request = new UpdateRequest(this.indexName, type, id);

        request.doc(source, XContentType.JSON);

        try
        {
            UpdateResponse updateResponse = client.update(request);

            checkShard(updateResponse.getShardInfo());
        }
        catch (ElasticsearchException e)
        {
            if (e.status() == RestStatus.NOT_FOUND)
            {
                StringBuilder builder = new StringBuilder();
                builder.append("更新的文档不存在；");
                builder.append("索引：").append(this.indexName);
                builder.append(",类型：").append(type);
                builder.append(",id=").append(id);

                throw new ElasticsearchException(builder.toString());
            }
        }

        return true;
    }

    /**
     * 批量处理
     *
     * @param items 文档列表
     * @return the bulk response
     */
    public BulkResponse bulk(List<BulkItem> items) throws IOException
    {
        if(items == null || items.isEmpty())
        {
            throw new ElasticsearchException("批量处理列表没有元素！");
        }

        BulkRequest request = new BulkRequest();

        // region 批量处理

        for (BulkItem bulkItem : items)
        {
            String id = bulkItem.getId();

            if(id != null && !id.isEmpty())
            {
                BulkOperation operation = bulkItem.getOperation();
                String source = bulkItem.getSource();
                String type = bulkItem.getType();

                switch(operation)
                {
                    case INDEX:
                    {
                        if(source == null || source.isEmpty())
                        {
                            continue;
                        }

                        IndexRequest indexRequest = new IndexRequest(this.indexName, type, id);

                        indexRequest.source(source, XContentType.JSON);

                        request.add(indexRequest);

                        break;
                    }
                    case UPSERT:
                    {
                        if(source == null || source.isEmpty())
                        {
                            continue;
                        }

                        UpdateRequest updateRequest = new UpdateRequest(this.indexName, type, id);

                        updateRequest.doc(source, XContentType.JSON);

                        request.add(updateRequest);

                        break;
                    }
                    case DELETE:
                    {
                        DeleteRequest deleteRequest = new DeleteRequest(this.indexName, type, id);

                        request.add(deleteRequest);

                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
            }
        }

        // endregion

        BulkResponse bulkResponse = client.bulk(request);

        for (BulkItemResponse bulkItemResponse : bulkResponse)
        {
            if (bulkItemResponse.isFailed())
            {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                logger.error("[es批量处理`bluk`错误信息]:" + failure.toString());
            }
        }

        return bulkResponse;
    }

    /**
     * 批量处理
     *
     * @param items 文档列表
     * @return the bulk response
     */
    public void bulkProcessor(List<BulkItem> items) throws IOException
    {
        if(items == null || items.isEmpty())
        {
            throw new ElasticsearchException("批量处理列表没有元素！");
        }

        BulkProcessor processor = ElasticsearchBulkProcessor.getInstance();

        // region 批量处理

        for (BulkItem bulkItem : items)
        {
            String id = bulkItem.getId();

            if(id != null && !id.isEmpty())
            {
                BulkOperation operation = bulkItem.getOperation();
                String source = bulkItem.getSource();
                String type = bulkItem.getType();

                switch(operation)
                {
                    case INDEX:
                    {
                        if(source == null || source.isEmpty())
                        {
                            continue;
                        }

                        IndexRequest indexRequest = new IndexRequest(this.indexName, type, id);

                        indexRequest.source(source, XContentType.JSON);

                        processor.add(indexRequest);

                        break;
                    }
                    case UPSERT:
                    {
                        if(source == null || source.isEmpty())
                        {
                            continue;
                        }

                        UpdateRequest updateRequest = new UpdateRequest(this.indexName, type, id);

                        updateRequest.doc(source, XContentType.JSON);

                        processor.add(updateRequest);

                        break;
                    }
                    case DELETE:
                    {
                        DeleteRequest deleteRequest = new DeleteRequest(this.indexName, type, id);

                        processor.add(deleteRequest);

                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
            }
        }

        // endregion
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
        checkType(type);
        checkId(id);

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

    // region 其它

    /**
     * 调用 ES 获取 IK 分词后结果
     *
     * @param keyword
     * @return
     */
    public List<String> getIkAnalyzeSearchTerms(String keyword)
    {


        return  null;
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
     * 检查字段
     * @param indexName 索引名称
     */
    private void checkIndexName(String indexName)
    {
        if(indexName == null || indexName.isEmpty())
        {
            throw new ElasticsearchException("搜索服务索引名称`indexName`不能为空");
        }
    }

    /**
     * 检查字段
     * @param id 文档Id
     */
    private void checkId(String id)
    {
        if(id == null || id.isEmpty())
        {
            throw new ElasticsearchException("文档Id不能为空");
        }
    }

    /**
     * 检查字段
     * @param type 文档类型
     */
    private void checkType(String type)
    {
        if(type == null || type.isEmpty())
        {
            throw new ElasticsearchException("文档类型不能为空");
        }
    }

    /**
     * 检查字段
     * @param source 文档数据
     */
    private void checkSource(String source)
    {
        if(source == null || source.isEmpty())
        {
            throw new ElasticsearchException("文档数据不能为空");
        }
    }

    /**
     * 检查碎片故障
     * @param shardInfo
     */
    private void checkShard(ReplicationResponse.ShardInfo shardInfo)
    {
        // 处理成功分片数量少于总分片数量的情况
        if (shardInfo.getTotal() != shardInfo.getSuccessful())
        {
            throw new ElasticsearchException("成功分片数量少于总分片数量");
        }

        if (shardInfo.getFailed() > 0)
        {
            // 处理潜在的失败
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures())
            {
                String reason = failure.reason();
                throw new ElasticsearchException(reason);
            }
        }
    }

    // endregion

    // region getter/setter

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
