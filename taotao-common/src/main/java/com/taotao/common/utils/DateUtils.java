package com.taotao.common.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * 日期工具类
 * @Description:日期工具类
 **/
public class DateUtils
{
    /**
     * 获取当前时间
     *
     * @return 默认时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getTodayByFormat()
    {
        return getTodayByFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前时间
     *
     * @param timeFormat 默认时间格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getTodayByFormat(String timeFormat)
    {
        if(timeFormat == null || timeFormat.isEmpty())
        {
            timeFormat = "yyyy-MM-dd HH:mm:ss";
        }

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
    }

    /**
     * 字符串转换时间
     *
     * @param time 时间字符串 默认时间格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDateTime toLocalDateTime(String time)
    {
        return LocalDateTime.parse(time);
    }

    /**
     * 字符串转换时间
     *
     * @param time       时间字符串 默认时间格式：yyyy-MM-dd HH:mm:ss
     * @param timeFormat 默认时间格式：yyyy-MM-dd HH:mm:ss
     * @return local date time
     */
    public static LocalDateTime toLocalDateTime(String time, String timeFormat)
    {
        if(timeFormat == null || timeFormat.isEmpty())
        {
            timeFormat = "yyyy-MM-dd HH:mm:ss";
        }

        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(timeFormat));
    }

    /**
     * 获取本月第一天
     *
     * @return the local date
     */
    public static LocalDate firstDayOfThisMonth()
    {
        return  LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最后第一天
     *
     * @return the local date
     */
    public static LocalDate lastDayOfThisMonth()
    {
        return  LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static void main(String[] args) throws Exception
    {
        try
        {

        }
        catch (Exception e)
        {
            throw new Exception();
        }
        // System.out.println("sss");
    }
}
