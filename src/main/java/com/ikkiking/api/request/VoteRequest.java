package com.ikkiking.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VoteRequest {
    @JsonProperty("post_id")
    private Long postId;
}
