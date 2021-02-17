package com.ikkiking.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingsResponse {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticsIsPublic;
}
