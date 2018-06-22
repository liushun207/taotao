package com.taotao.elasticsearch.common;

/**
 * 表示索引文档信息
 */
public class BulkItem
{
    // region 成员

    /**
     * 文档.
     */
    private String source;

    /**
     * The Id.
     */
    private String Id;

    /**
     * 文档类型.
     */
    private String type;

    /**
     * 索引操作.
     */
    private BulkOperation Operation;

    // endregion

    // region getter/setter

    /**
     * Gets 文档.
     *
     * @return the source
     */
    public String getSource()
    {
        return source;
    }

    /**
     * Sets 文档.
     *
     * @param source the source
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId()
    {
        return Id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id)
    {
        Id = id;
    }

    /**
     * Gets 索引操作.
     *
     * @return the operation
     */
    public BulkOperation getOperation()
    {
        return Operation;
    }

    /**
     * Sets 索引操作.
     *
     * @param operation the operation
     */
    public void setOperation(BulkOperation operation)
    {
        Operation = operation;
    }

    /**
     * Gets 文档类型.
     *
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets 文档类型.
     *
     * @param type the type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    // endregion
}
