package com.ikkiking.api.response.PostResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUserResponse {
    private Long id;
    private String name;
    private String photo;
}