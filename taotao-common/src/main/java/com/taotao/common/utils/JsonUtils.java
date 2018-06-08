package com.taotao.common.utils;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 淘淘商城自定义响应结构
 */
public class JsonUtils
{
    // region fasterxml.jackson

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * fasterxml.jackson 将对象转换成json字符串。
     * <p>Title: objectToJson</p>
     * <p>Description: </p>
     *
     * @param data
     * @return
     */
    public static String objectToJson(Object data)
    {
        try
        {
            String string = MAPPER.writeValueAsString(data);
            return string;
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * fasterxml.jackson 将对象转换成json字符串。
     * <p>Title: objectToBytes</p>
     * <p>Description: </p>
     *
     * @param data
     * @return
     */
    public static byte[] objectToBytes(Object data)
    {
        try
        {
            byte[] string = MAPPER.writeValueAsBytes(data);
            return string;
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * fasterxml.jackson 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType)
    {
        try
        {
            T t = MAPPER.readValue(jsonData, beanType);
            return t;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * fasterxml.jackson 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType)
    {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try
        {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    // endregion

    // region alibaba.fastjson

    /**
     * 将对象转换成json字符串。
     *
     * @param data 数据
     * @return json字符串
     */
    public static String serialize(Object data)
    {
        String result = JSON.toJSONString(data);
        return result;
    }

    /**
     * 将json结果集转化为对象
     *
     * @param <T>      泛型
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return 泛型对象
     */
    public static <T> T deserialize(String jsonData, Class<T> beanType)
    {
        T result = JSON.parseObject(jsonData, beanType);
        return result;
    }

    /**
     * 将json数据转换成对象list
     *
     * @param <T>      泛型
     * @param jsonData json数据
     * @param beanType 对象中的object类型
     * @return 泛型对象list
     */
    public static <T> List<T> deserializeArray(String jsonData, Class<T> beanType)
    {
        List<T> list = JSON.parseArray(jsonData, beanType);
        return list;
    }

    // endregion
}
