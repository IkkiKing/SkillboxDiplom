package com.ikkiking.api.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private long timestamp;
    private String text;
    private CommentUserResponse user;
}
