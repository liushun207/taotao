package com.taotao.common.pojo;

import java.rmi.ServerError;

/**
 * 表示数据传输响应状态。
 */
public enum ResponseStatus
{
    //#region 枚举

    SUCCESS(1, "成功"),

    FAILURE(0, "失败"),

    INVALIDSIGN(-5, "签名无效"),

    VALIDATIONERROR(-10, "参数无效"),

    SERVERERROR(-15, "服务端发生异常"),

    SESSIONLOST(-20, "会话凭证丢失"),

    UNAUTHORIZED(-25, "操作未授权");

    //#endregion

    //#region 成员

    private int value;
    private String description;

    //#endregion

    //#region 构造函数

    /**
     * 构造函数
     *
     * @param 值
     * @param 描述
     */
    private ResponseStatus(int value, String description)
    {
        this.value = value;
        this.description = description;
    }

    //#endregion

    //#region 公共方法

    /**
     * 获取枚举值
     *
     * @return 枚举值
     */
    public int getValue()
    {
        return this.value;
    }

    /**
     * 获取枚举描述
     *
     * @return 枚举描述
     */
    public String getDescription()
    {
        return this.description;
    }

    //#endregion
}
