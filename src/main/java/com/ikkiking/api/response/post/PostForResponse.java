package com.ikkiking.api.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostForResponse {

    private Long id;
    private Long timestamp;
    private UserResponse user;
    private String title;
    private String announce;
    private Long likeCount;
    private Long dislikeCount;
    private Long commentCount;
    private Long viewCount;

}
