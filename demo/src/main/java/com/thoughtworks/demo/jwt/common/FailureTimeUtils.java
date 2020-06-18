package com.thoughtworks.demo.jwt.common;

import java.util.Calendar;
import java.util.Date;

/**
 * 类 名: FailureTimeUtils
 * 描 述:
 * 作 者: yunfeng
 * 创 建: 2020/6/9 : 14:55
 *
 * @author: yunfeng
 */
public class FailureTimeUtils {

    /**
     * @author: yunfeng
     * @demand: 根据指定的时间规则和时间生成有效时间
     * @parameters:
     * @creationDate：
     */
    public static Date creatValidTime(FailureTime failureTime, int jwtValidTime) {
        Date date = new Date();
        if (failureTime.name().equals(FailureTime.SECOND.toString())) {
            return createBySecond(date, jwtValidTime);
        }
        if (failureTime.name().equals(FailureTime.MINUTE.toString())) {
            return createBySecond(date, jwtValidTime * 60);
        }
        if (failureTime.name().equals(FailureTime.HOUR.toString())) {
            return createBySecond(date, jwtValidTime * 60 * 60);
        }
        if (failureTime.name().equals(FailureTime.DAY.toString())) {
            return getDateAfter(date, jwtValidTime);
        }
        return null;
    }

    /**
     * 得到几天后的时间
     *
     * @param day
     * @return
     */
    public static Date getDateAfter(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 得到几天前的时间
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getDateBefore(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到多少秒之后的时间
     *
     * @param date
     * @param jwtValidTime
     * @return
     */
    public static Date createBySecond(Date date, int jwtValidTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, jwtValidTime);
        return calendar.getTime();
    }

}
