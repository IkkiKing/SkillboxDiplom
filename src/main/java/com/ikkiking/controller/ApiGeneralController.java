package com.ikkiking.controller;

import com.ikkiking.api.response.CalendarResponse;
import com.ikkiking.api.response.InitResponse;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.api.response.StatisticResponse.AllStatisticResponse;
import com.ikkiking.api.response.StatisticResponse.MyStatisticResponse;
import com.ikkiking.api.response.TagResponse.Tag;
import com.ikkiking.api.response.TagResponse.TagResponse;
import com.ikkiking.service.CalendarService;
import com.ikkiking.service.SettingsService;
import com.ikkiking.service.StatisticService;
import com.ikkiking.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final StatisticService statisticService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService, CalendarService calendarService, StatisticService statisticService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.statisticService = statisticService;
    }

    @GetMapping("/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    private SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    private TagResponse getTags(@RequestParam(name = "query", required = false) List<Tag> tags){
        return tagService.getTagService(tags);
    }

    @GetMapping("/calendar")
    private CalendarResponse getCalendar(@RequestParam(name = "year", required = false, defaultValue = "0") int year){
        return calendarService.getCalendar(year);
    }

    @GetMapping("/statistics/my")
    private MyStatisticResponse getMyStatistic(){
        return statisticService.getMyStatistic();
    }

    @GetMapping("/statistics/all")
    private AllStatisticResponse getAllStatistic(){
        return statisticService.getAllStatistic();
    }
}
