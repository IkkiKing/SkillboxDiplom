package com.ikkiking.controller;

import com.ikkiking.api.request.CommentRequest;
import com.ikkiking.api.request.ModerationRequest;
import com.ikkiking.api.request.ProfileRequest;
import com.ikkiking.api.request.SettingsRequest;
import com.ikkiking.api.response.*;
import com.ikkiking.api.response.StatisticResponse.StatisticResponse;
import com.ikkiking.api.response.TagResponse.TagResponse;
import com.ikkiking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final GeneralService generalService;
    private final PostCommentsService postCommentsService;

    @Autowired
    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService, CalendarService calendarService, GeneralService generalService, PostCommentsService postCommentsService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.generalService = generalService;
        this.postCommentsService = postCommentsService;
    }

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        return settingsService.globalSettings();
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void setSettings(@RequestBody SettingsRequest settingsRequest) {
        settingsService.settings(settingsRequest);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> getTags(@RequestParam(name = "query", required = false) String query) {
        return tagService.tag(query);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> getCalendar(@RequestParam(name = "year", required = false, defaultValue = "0") int year) {
        return calendarService.calendar(year);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticResponse> getMyStatistic() {
        return generalService.myStatistic();
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticResponse> getAllStatistic() {
        return generalService.allStatistic();
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<CommentAddResponse> addComment(@RequestBody CommentRequest commentRequest) {
        return postCommentsService.comment(commentRequest);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ModerationResponse> moderate(@RequestBody ModerationRequest moderationRequest) {
        return generalService.moderate(moderationRequest);
    }

    @PostMapping("/image")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> image(@RequestParam("image") MultipartFile multipartFile) {
        return generalService.image(multipartFile);
    }


    @PostMapping(value = "/profile/my", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileResponse> profile(
            @RequestPart("photo") MultipartFile photo,
            @RequestPart(name = "name") String name,
            @RequestPart(name = "email") String email,
            @RequestPart(name = "removePhoto") String removePhoto,
            @RequestPart(name = "password", required = false) String password) {
        return generalService.profileMulti(photo, name, email, removePhoto, password);
    }

    @PostMapping(value = "/profile/my", consumes = {"application/json"})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ProfileResponse> profile(@RequestBody ProfileRequest profileRequest) {
        profileRequest.hashCode();
        return generalService.profile(profileRequest);
    }
}
