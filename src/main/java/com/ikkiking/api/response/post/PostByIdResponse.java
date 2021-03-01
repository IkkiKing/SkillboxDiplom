package com.ikkiking.api.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostByIdResponse {
    private Long id;
    private long timestamp;
    private boolean active;
    private UserResponse user;
    private String title;
    private String text;
    private Long likeCount;
    private Long dislikeCount;
    private Long viewCount;
    private List<CommentResponse> comments;
    private Set<String> tags;
}
