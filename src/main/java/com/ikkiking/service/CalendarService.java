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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalendarService {

    private final PostRepository postRepository;

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
        if (year == 0) {
            year = DateHelper.getCurrentDate().get(Calendar.YEAR);
            log.info("The year was setted as default: " + year);
        }
        final Integer searchYear = year;
        List<CalendarCustom> postsByYears = postRepository.findPostByYear(year);

        List<Integer> years = postsByYears.stream()
                .map(p -> p.getYear())
                .distinct()
                .collect(Collectors.toList());

        Map<String, Long> postsMap = postsByYears.stream()
                    .filter(f -> f.getYear().equals(Integer.valueOf(searchYear)))
                    .collect(Collectors.toMap(CalendarCustom::getDate,
                            CalendarCustom::getAmount));

        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setYears(years);
        calendarResponse.setPosts(postsMap);
        return ResponseEntity.ok(calendarResponse);
    }

}
