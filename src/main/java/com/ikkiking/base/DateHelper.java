package com.ikkiking.base;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("UTC");

    /**
     * Возвращает объект календарь с устанновленым часовым поясом.
     * */
    public static Calendar getCurrentDate() {
        return Calendar.getInstance(TIME_ZONE);
    }

    /**
     * Формирует дату из переданного timestamp.
     * */
    public static Date getRightDateFromTimeStamp(Long timeStamp) {

        Calendar currentTime = Calendar.getInstance(TIME_ZONE);
        Calendar newTime = Calendar.getInstance(TIME_ZONE);

        newTime.setTimeInMillis(timeStamp);

        //Если указанное время меньше текущего, зададим текущее
        if (newTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
            return currentTime.getTime();
        } else {
            return newTime.getTime();
        }
    }
}
