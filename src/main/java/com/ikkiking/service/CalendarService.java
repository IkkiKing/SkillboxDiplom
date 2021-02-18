package com.ikkiking.service;

import com.ikkiking.api.response.CalendarResponse;
import com.ikkiking.base.DateHelper;
import com.ikkiking.repository.CalendarCustom;
import com.ikkiking.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CalendarService {

    private PostRepository postRepository;

    @Autowired
    public CalendarService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Метод формирующий Календарь.
     * @param year год за который требуется сформировать кол-во постов по каждой дате
     *             может быть пустым, в этом случае ставим текущий
     *
     * @return Календарь с кол-вом публикаций по датам
     * */
    public ResponseEntity<CalendarResponse> calendar(int year) {
        CalendarResponse calendarResponse = new CalendarResponse();
        if (year == 0) {
            log.info("The year was setted as default");
            year = DateHelper.getCurrentDate().get(Calendar.YEAR);
        }

        List<CalendarCustom> postsByYears = postRepository.findPostByYear(year);
        List<Integer> years = postRepository.findYears();

        Map<String, Long> postsMap = new HashMap<>();
        if (postsByYears != null) {
            postsMap = postsByYears.stream()
                    .collect(Collectors.toMap(CalendarCustom::getDate, CalendarCustom::getAmount));
        }

        calendarResponse.setYears(years);
        calendarResponse.setPosts(postsMap);
        return ResponseEntity.ok(calendarResponse);
    }

}
