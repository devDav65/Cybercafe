package com.example.monprojet.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static long calculateSessionDuration(Date start, Date end) {
        long diffInMillies = end.getTime() - start.getTime();
        return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Date getCurrentDate() {
        return new Date();
    }
}
