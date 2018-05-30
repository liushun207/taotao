package com.taotao.common.pojo;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

/**
 * 表示数据传输响应命令。
 *
 * @param <T> 响应的结果对象。
 */
public class ResponseInstruction<T> implements Serializable
{
    //#region 成员

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示消息
     */
    public String message;

    /**
     * 服务器时间
     */
    public Date serverTime;

    /**
     * 响应内容
     */
    public T content;

    //#endregion

    //#region 构造函数

    /**
     * 构造函数
     */
    public ResponseInstruction()
    {
        this(ResponseStatus.SUCCESS, null);
    }

    /**
     * 构造函数
     *
     * @param content 响应内容
     */
    public ResponseInstruction(T content)
    {
        this(ResponseStatus.SUCCESS, content);
    }

    /**
     * 构造函数
     *
     * @param status 响应状态
     * @param content 响应内容
     */
    public ResponseInstruction(ResponseStatus status, T content)
    {
        this.code = status.getValue();
        this.content = content;
        this.serverTime = new Date();
    }

    //#endregion

    //#region getter/setter

    /**
     * Gets 状态码.
     *
     * @return 状态码
     */
    public Integer getCode()
    {
        return code;
    }

    /**
     * Sets 状态码.
     *
     * @param code 状态码
     */
    public void setCode(Integer code)
    {
        this.code = code;
    }

    /**
     * Gets 提示消息.
     *
     * @return 提示消息
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Sets 提示消息.
     *
     * @param message 提示消息
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * Gets 服务器时间
     *
     * @return 服务器时间
     */
    public Date getServerTime()
    {
        return serverTime;
    }

    /**
     * Sets 服务器时间
     *
     * @param serverTime 服务器时间
     */
    public void setServerTime(Date serverTime)
    {
        this.serverTime = serverTime;
    }

    /**
     * Gets 响应内容
     *
     * @return 响应内容
     */
    public T getContent()
    {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content 响应内容
     */
    public void setContent(T content)
    {
        this.content = content;
    }

    //#endregion
}
