package com.ikkiking.api.response.PostResponse;


import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private Long count;
    private List<PostForResponse> posts;
}
