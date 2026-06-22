package com.example.accountbook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());

    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    public static String getCurrentTime() {
        return timeFormat.format(new Date());
    }

    public static String formatDisplayDate(String date) {
        try {
            Date d = dateFormat.parse(date);
            return displayDateFormat.format(d);
        } catch (Exception e) {
            return date;
        }
    }

    public static String getCurrentDateTime() {
        return getCurrentDate() + " " + getCurrentTime();
    }
}