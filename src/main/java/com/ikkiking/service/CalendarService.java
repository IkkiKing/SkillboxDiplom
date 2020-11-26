package com.ikkiking.service;

import com.ikkiking.api.response.CalendarResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CalendarService {

    public CalendarResponse getCalendar(int year){

        if (year == 0){
            year = Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR);
        }

        List<Integer> years = new ArrayList<>();
        Map<String, Integer> posts = new HashMap<>();

        years.add(2017);
        years.add(2018);
        years.add(2019);
        years.add(2020);

        posts.put("2019-12-17", 56);
        posts.put("2019-12-14", 11);
        posts.put("2019-06-17", 1);
        posts.put("2020-03-12", 6);

        CalendarResponse calendarResponse = new CalendarResponse(years, posts);

        return calendarResponse;
    }

}
