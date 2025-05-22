package br.martim.dev.util.list;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final Locale LOCALE_US = Locale.US;

    public static String getCurrentDate() {
        return new SimpleDateFormat("MM/dd/yy", LOCALE_US).format(new Date());
    }

    public static String getDate(long millis) {
        return getDate(millis, DateFormat.BIG, true);
    }

    public static String getDate(long millis, DateFormat format, boolean withHoursAndMinutes) {
        String pattern = "MM/dd/yy";
        if (withHoursAndMinutes) {
            pattern += (format.equals(DateFormat.BIG) ? " 'at' " : " ") + "hh:mm a";
        }

        return new SimpleDateFormat(pattern, LOCALE_US).format(new Date(millis));
    }

    public static String getSimpleDate(long millis) {
        return getDate(millis, DateFormat.SMALL, true);
    }

    public static String formatDate(long time) {
        return formatDate(time, DateFormat.SMALL);
    }

    public static String formatDate(long time, DateFormat format) {
        boolean big = format.equals(DateFormat.BIG);

        long diff = System.currentTimeMillis() - time;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long years = days / 365;

        seconds %= 60;
        minutes %= 60;
        hours %= 24;
        days %= 365;

        StringBuilder result = new StringBuilder();

        if (years > 0)
            result.append(years).append(big ? " years" : "y");

        if (days > 0)
            result.append(years > 0 ? " " : "").append(days).append(big ? " days" : "d");

        if (hours > 0)
            result.append(days > 0 ? " " : "").append(hours).append(big ? " hours" : "h");

        if (minutes > 0)
            result.append(hours > 0 ? " " : "").append(minutes).append(big ? " minutes" : "m");

        if (seconds > 0)
            result.append((hours > 0 || minutes > 0) ? " " : "").append(seconds).append(big ? " seconds" : "s");

        return result.toString();
    }

    public enum DateFormat {
        BIG, SMALL
    }
}
