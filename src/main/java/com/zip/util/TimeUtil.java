package com.zip.util;

import com.zip.constant.ZipConstant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class TimeUtil {

    private TimeUtil() {}

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(ZipConstant.DATE_FORMAT);

    /**
     * 获取数月的时间信息
     * @param number 时间间隔（月）
     * @return 距当前时间number月的时间信息，格式为yyyy-MM-dd
     */
    public static String getTimeBeforeSomeMonth(int number) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, number);
        Date formNow3Month = calendar.getTime();
        return dateFormat.format(formNow3Month);
    }
}
