package com.ikkiking.controller;

import com.ikkiking.api.response.InitResponse;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.api.response.TagResponse.Tag;
import com.ikkiking.api.response.TagResponse.TagResponse;
import com.ikkiking.service.SettingsService;
import com.ikkiking.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagService tagService;

    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.tagService = tagService;
    }

    @GetMapping("/api/init")
    private InitResponse init(){
        return initResponse;
    }

    @GetMapping("/api/settings")
    private SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }

    @GetMapping("/api/tag")
    private TagResponse getTags(@RequestParam(required = false, name = "query") List<Tag> tags){
        return tagService.getTagService(tags);
    }
}
