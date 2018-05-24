package com.taotao.common.pojo;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * 表示一个请求命令。
 *
 * @param <T> 请求参数
 */
public class RequestInstruction<T extends DataObject> implements Serializable
{
    //#region 成员

    /**
     * 签名。
     */
    private String sign;

    /**
     * 合作者ID。
     */
    private String partnerId;

    /**
     * 时间戳。
     */
    private Long timestamp;

    /**
     * 安全凭证号。
     */
    private String credential;

    /**
     * 请求参数。
     */
    private T params;

    //#endregion

    //#region 构造函数

    /**
     * 构造函数
     */
    public RequestInstruction()
    {

    }

    /**
     * 构造函数
     *
     * @param sign      签名。
     * @param partnerId 合作者ID。
     * @param timestamp 时间戳。
     */
    public RequestInstruction(String sign, String partnerId, Long timestamp)
    {
        this(sign, partnerId, null, timestamp);
    }

    /**
     * 构造函数
     *
     * @param sign       签名。
     * @param partnerId  合作者ID。
     * @param credential 安全凭证号。
     * @param timestamp  时间戳。
     */
    public RequestInstruction(String sign, String partnerId, String credential, Long timestamp)
    {
        this.sign = sign;
        this.partnerId = partnerId;
        this.credential = credential;
        this.timestamp = timestamp;
    }

    //#endregion

    //#region getter/setter

    /**
     * Gets sign.
     *
     * @return the sign
     */
    public String getSign()
    {
        return sign;
    }

    /**
     * Sets sign.
     *
     * @param sign the sign
     */
    public void setSign(String sign)
    {
        this.sign = sign;
    }

    /**
     * Gets partner id.
     *
     * @return the partner id
     */
    public String getPartnerId()
    {
        return partnerId;
    }

    /**
     * Sets partner id.
     *
     * @param partnerId the partner id
     */
    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp()
    {
        return timestamp;
    }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    /**
     * Gets credential.
     *
     * @return the credential
     */
    public String getCredential()
    {
        return credential;
    }

    /**
     * Sets credential.
     *
     * @param credential the credential
     */
    public void setCredential(String credential)
    {
        this.credential = credential;
    }

    /**
     * Gets params.
     *
     * @return the params
     */
    public T getParams()
    {
        return params;
    }

    /**
     * Sets params.
     *
     * @param params the params
     */
    public void setParams(T params)
    {
        this.params = params;
    }

    //#endregion
}
