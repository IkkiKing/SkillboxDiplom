package com.ikkiking.base;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

    private static final TimeZone timeZone = TimeZone.getTimeZone("Europe/Moscow");

    public static Calendar getCurrentDate(){
        return Calendar.getInstance(timeZone);
    }


    //Получаем правильную дату для поста из таймстампа
    public static Date getRightDateFromTimeStamp(Long timeStamp) {

        Calendar currentTime = Calendar.getInstance(timeZone);
        Calendar newTime    = Calendar.getInstance(timeZone);

        newTime.setTimeInMillis(timeStamp);

        //Если указанное время меньше текущего, зададим текущее
        if (newTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
            return currentTime.getTime();
        }else{
            return newTime.getTime();
        }
    }
}
