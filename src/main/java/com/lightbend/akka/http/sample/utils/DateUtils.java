package com.lightbend.akka.http.sample.utils;

public class DateUtils {

    public static final String DATE_TIME = "dd/MM/yyyy HH:mm";
    public static final String DATE_TIME_SECONDS = "dd/MM/yyyy HH:mm:ss";

    public static String getDateTimePattern(final String text) {
        return text.length() == DATE_TIME.length() ? DATE_TIME : DATE_TIME_SECONDS;
    }
}
