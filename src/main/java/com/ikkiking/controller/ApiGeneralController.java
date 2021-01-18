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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final StatisticService statisticService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService, CalendarService calendarService, StatisticService statisticService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.statisticService = statisticService;
    }

    @GetMapping("/init")
    public InitResponse init(){
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public TagResponse getTags(@RequestParam(name = "query", required = false) String query){
        return tagService.getTag(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam(name = "year", required = false, defaultValue = "0") int year){
        return calendarService.getCalendar(year);
    }

    @GetMapping("/statistics/my")
    public MyStatisticResponse getMyStatistic(){
        return statisticService.getMyStatistic();
    }

    @GetMapping("/statistics/all")
    public AllStatisticResponse getAllStatistic(){
        return statisticService.getAllStatistic();
    }

    /*@PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public */
}
