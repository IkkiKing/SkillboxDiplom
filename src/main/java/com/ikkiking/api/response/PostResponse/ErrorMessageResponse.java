package com.ikkiking.api.response.PostResponse;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ErrorMessageResponse {
    private String title;
    private String text;
    private String image;

    public ErrorMessageResponse(){}

    public ErrorMessageResponse(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public ErrorMessageResponse(String image) {
        this.image = image;
    }
}
