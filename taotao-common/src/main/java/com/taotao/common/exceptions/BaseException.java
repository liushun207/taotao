package com.taotao.common.exceptions;

/**
 * 自定义异常基类
 */
public class BaseException extends Exception
{
    // region 构造函数

    /**
     * 构造函数
     */
    public BaseException()
    {
        super();
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public BaseException(String message)
    {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message 错误消息
     * @param cause   引发错误原因
     */
    public BaseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param cause 引发错误原因
     */
    public BaseException(Throwable cause)
    {
        super(cause);
    }

    // endregion
}
