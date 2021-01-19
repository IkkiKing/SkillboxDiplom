package com.ikkiking.controller;

import com.ikkiking.api.request.CommentRequest;
import com.ikkiking.api.response.CalendarResponse;
import com.ikkiking.api.response.CommentAddResponse;
import com.ikkiking.api.response.InitResponse;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.api.response.StatisticResponse.StatisticResponse;
import com.ikkiking.api.response.TagResponse.TagResponse;
import com.ikkiking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final GeneralService generalService;
    private final PostCommentsService postCommentsService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService, CalendarService calendarService, GeneralService generalService, PostCommentsService postCommentsService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.generalService = generalService;
        this.postCommentsService = postCommentsService;
    }

    @Autowired


    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/tag")
    public TagResponse getTags(@RequestParam(name = "query", required = false) String query) {
        return tagService.getTag(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse getCalendar(@RequestParam(name = "year", required = false, defaultValue = "0") int year) {
        return calendarService.getCalendar(year);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticResponse> getMyStatistic() {
        return generalService.getMyStatistic();
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticResponse> getAllStatistic() {
        return generalService.getAllStatistic();
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentAddResponse> addComment(@RequestBody CommentRequest commentRequest) {
        return postCommentsService.addComment(commentRequest);
    }
}
