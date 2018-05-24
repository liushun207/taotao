package com.taotao.common.pojo;

import org.joda.time.DateTime;

/**
 * 表示内部接口返回实体对象
 *
 * @param <T> 响应对象
 */
public class ApiResponse<T>
{
    //#region 成员

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示消息
     */
    public String message;

    /**
     * 服务器时间
     */
    public DateTime serverTime;

    /**
     * 响应内容
     */
    public T content;

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
    public DateTime getServerTime()
    {
        return serverTime;
    }

    /**
     * Sets 服务器时间
     *
     * @param serverTime 服务器时间
     */
    public void setServerTime(DateTime serverTime)
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
