package com.taotao.common.pojo;

import java.io.Serializable;

/**
 * @param <T> 响应的结果对象。
 * @Author:曾智
 * @Description:用于请求之后数据结果的对象。
 * @CreatedTime 2017/7/23
 */
public class ResponseInstruction<T> implements Serializable
{
    /**
     * 响应代码。
     */
    private int code;

    /**
     * 是否成功。
     */
    private boolean success;
    /**
     * 响应消息。
     */
    private String message;
    /**
     * 是否出错。
     */
    private boolean isError;
    /**
     * 响应结果。
     */
    private T Content;

    public ResponseInstruction()
    {
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public boolean isError()
    {
        return isError;
    }

    public void setError(boolean error)
    {
        isError = error;
    }

    public Boolean validate()
    {
        return null;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public T getContent()
    {
        return Content;
    }

    public void setContent(T content)
    {
        Content = content;
    }
}
