package com.taotao.common.pojo;

import java.io.Serializable;

/**
 * @Author:曾智
 * @Description:表示一个请求命令。
 * @CreatedTime 2017/7/23
 */
public class RequestInstruction<T extends DataObject> implements Serializable
{
    /**
     * 获取或设置当前请求签名。
     */
    private String sign;

    /**
     * 获取或设置合作者ID。
     */
    private String partnerId;

    /**
     * 获取或设置当前请求时间戳。
     */
    private long timestamp;

    /**
     * 获取或设置安全凭证号。
     */
    private String credential;

    public String getCredential()
    {
        return credential;
    }

    public void setCredential(String credential)
    {
        this.credential = credential;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getSign()
    {
        return sign;
    }

    public void setSign(String sign)
    {
        this.sign = sign;
    }
}
