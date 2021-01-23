package com.ikkiking.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentAddResponse {

    private Long id;

    @JsonProperty("parent_id")
    private boolean result;

    private CommentAddError errors;

}
