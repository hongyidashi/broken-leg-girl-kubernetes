package com.blg.api.utils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * 描述：计算年龄工具类
 * 作者: panhongtong
 * 创建时间: 2020-05-31 21:56
 **/
public class AgeUtils {

    /**
     * 获取年龄
     */
    public static int getAgeByBirth(Date birthDay) {
        int age;
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            //出生日期晚于当前时间，无法计算
            throw new IllegalArgumentException("该日期早于当前时间，这是不现实的，给爷好好检查一下");
        }
        // 当前年份
        int yearNow = cal.get(Calendar.YEAR);
        // 当前月份
        int monthNow = cal.get(Calendar.MONTH);
        // 当前日期
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        // 计算整岁数
        age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //当前日期在生日之前，年龄减一
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                //当前月份在生日之前，年龄减一
                age--;
            }
        }
        return age;
    }

    /**
     * 获取年龄
     */
    public static int getAgeByBirth(LocalDate birthDay) {
        // 1、判断传入时间是否早于当前时间
        LocalDate now = LocalDate.now();
        if (birthDay.isAfter(now)) {
            throw new IllegalArgumentException("该日期早于当前时间，这是不现实的，给爷好好检查一下");
        }
        // 2、获取当前年份、月份、日期
        int yearNow = now.getYear();
        int monthNow = now.getMonthValue();
        int dayOfMonthNow = now.getDayOfMonth();
        // 3、计算整岁数
        int age = yearNow - birthDay.getYear();
        if (monthNow <= birthDay.getMonthValue()) {
            if (monthNow == birthDay.getMonthValue()) {
                if (dayOfMonthNow < birthDay.getDayOfMonth()) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

}
