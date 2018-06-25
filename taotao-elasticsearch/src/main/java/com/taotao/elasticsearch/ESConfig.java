package com.taotao.elasticsearch;

/**
 * es 配置
 **/
public class ESConfig
{
    // region 资源配置

    /**
     * 服务地址 例如："localhost:9200, 192.168.1.200:9200"
     */
    private String hosts;

    /**
     * schema方案
     */
    private String schema;

    /**
     * http最大连接值
     */
    private Integer connectNum;

    /**
     * http路由值的最大连接
     */
    private Integer connectPerRoute;

    // endregion

    // region getter/setter

    public String getHosts()
    {
        return hosts;
    }

    public void setHosts(String hosts)
    {
        this.hosts = hosts;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public Integer getConnectNum()
    {
        return connectNum;
    }

    public void setConnectNum(Integer connectNum)
    {
        this.connectNum = connectNum;
    }

    public Integer getConnectPerRoute()
    {
        return connectPerRoute;
    }

    public void setConnectPerRoute(Integer connectPerRoute)
    {
        this.connectPerRoute = connectPerRoute;
    }


    // endregion
}
