package com.taotao.elasticsearch.common;

/**
 * es 批量抄作类型
 **/
public enum BulkOperation
{
    //#region 枚举

    /**
     * 索引
     */
    INDEX(1, "索引"),

    /**
     * 更新
     */
    UPSERT(2, "更新"),

    /**
     * 删除
     */
    DELETE(3, "删除");

    //#endregion

    //#region 成员

    private int value;
    private String description;

    //#endregion

    //#region 构造函数

    /**
     * 构造函数
     * @param value 值
     * @param description 描述
     */
    private BulkOperation(int value, String description)
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
