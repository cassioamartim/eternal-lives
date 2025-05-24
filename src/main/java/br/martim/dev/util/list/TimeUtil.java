package br.martim.dev.util.list;

public class TimeUtil {

    public static long convertTime(long days, long hours, long minutes, long seconds) {
        long day = 86400 * days;
        long hour = 3600 * hours;
        long minute = 60 * minutes;

        long time = minute + hour + day + seconds;

        return System.currentTimeMillis() + time * 1000L;
    }

    public static long getTime(String timeFormat) {
        if (timeFormat.equals("-1")
                || timeFormat.equals("-1L") || timeFormat.equalsIgnoreCase("never") || timeFormat.equalsIgnoreCase("n"))
            return -1L;

        String[] times = timeFormat.split(",");

        int day = 0, hour = 0, minute = 0, second = 0;

        for (String time : times) {
            time = time.toLowerCase();

            if (time.contains("d")) {
                day = Integer.parseInt(time.replace("d", ""));
            }

            if (time.contains("h")) {
                hour = Integer.parseInt(time.replace("h", ""));
            }

            if (time.contains("m")) {
                minute = Integer.parseInt(time.replace("m", ""));
            }

            if (time.contains("s")) {
                second = Integer.parseInt(time.replace("s", ""));
            }
        }

        return convertTime(day, hour, minute, second);
    }
}
