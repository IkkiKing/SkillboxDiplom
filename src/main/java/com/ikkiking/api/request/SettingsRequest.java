package com.ikkiking.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SettingsRequest {
    @JsonProperty("MULTIUSER_MODE")
    private boolean multiUserMode;
    @JsonProperty("POST_PREMODERATION")
    private boolean postPreModeration;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private boolean statisticIsPublic;
}
