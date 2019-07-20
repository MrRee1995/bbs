package com.ccnu.bbs.converter;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class Date2StringConverter {

    public static String convert(Date date){
        String time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        SimpleDateFormat dateFormatToday = new SimpleDateFormat("HH:mm");
        if (date.before(getBeginDayOfYesterday())){
            time = dateFormat.format(date);
        }
        else if (!date.before(getBeginDayOfYesterday()) && !date.after(getEndDayOfYesterDay())){
            time = "昨天";
        }
        else {
            time = "今天 " + dateFormatToday.format(date);
        }
        return time;
    }

    //获取当天的开始时间
    private static java.util.Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    //获取当天的结束时间
    private static java.util.Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
    //获取昨天的开始时间
    private static Date getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
    //获取昨天的结束时间
    private static Date getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
}
