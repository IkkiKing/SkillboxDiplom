package com.ikkiking.api.response.PostResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Comment {
    private Long id;
    private long timestamp;
    private String text;
    private CommentUser user;
}
