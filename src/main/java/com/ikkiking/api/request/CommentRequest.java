package com.ikkiking.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentRequest {
    //TEST
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("post_id")
    private Long postId;
    private String text;

}
