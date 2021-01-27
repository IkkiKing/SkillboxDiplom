package com.ikkiking.service;

import com.ikkiking.api.response.CalendarResponse;
import com.ikkiking.base.DateHelper;
import com.ikkiking.model.Post;
import com.ikkiking.repository.CalendarCustom;
import com.ikkiking.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class CalendarService {

    private PostRepository postRepository;

    @Autowired
    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity<CalendarResponse> getCalendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        if (year == 0) {
            year = DateHelper.getCurrentDate().get(Calendar.YEAR);
        }

        List<CalendarCustom> posts = postRepository.findPostDates(year);

        Set<Integer> years = new HashSet<>();
        Map<String, Long> postsMap = new HashMap<>();

        if (posts != null) {
            posts.forEach(t -> {
                years.add(t.getYear());
                postsMap.put(t.getDate(), t.getAmount());
            });
        }
        calendarResponse.setYears(years);
        calendarResponse.setPosts(postsMap);
        return ResponseEntity.ok(calendarResponse);
    }

}
