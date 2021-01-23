package com.ikkiking.api.response;

import lombok.Data;

@Data
public class ImageResponse {
    private boolean result;
    private ImageErrorResponse errors;
}
