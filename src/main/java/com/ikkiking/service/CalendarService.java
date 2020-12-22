package com.ikkiking.service;

import com.ikkiking.api.response.CalendarResponse;
import com.ikkiking.model.Post;
import com.ikkiking.repository.CalendarCustom;
import com.ikkiking.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



@Service
public class CalendarService {

    @Autowired
    private PostRepository postRepository;

    public CalendarResponse getCalendar(int year){

        if (year == 0){
            year = Calendar.getInstance(TimeZone.getTimeZone("UTC")).get(Calendar.YEAR);
        }

        List<CalendarCustom> posts = postRepository.findPostDates(year);

        Set<Integer> years = new HashSet<>();
        Map<String, Long> postsMap = new HashMap<>();

        posts.forEach(t->{
            years.add(t.getYear());
            postsMap.put(t.getDate(), t.getAmount());
        });

        CalendarResponse calendarResponse = new CalendarResponse(years, postsMap);
        return calendarResponse;
    }

}
