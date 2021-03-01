package com.ikkiking.api.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUserResponse {
    private Long id;
    private String name;
    private String photo;
}
