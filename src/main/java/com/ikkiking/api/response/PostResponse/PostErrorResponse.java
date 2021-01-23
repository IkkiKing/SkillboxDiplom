package com.ikkiking.api.response.PostResponse;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PostErrorResponse {
    private String title;
    private String text;
    private String image;

    public PostErrorResponse(){}

    public PostErrorResponse(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public PostErrorResponse(String image) {
        this.image = image;
    }
}
