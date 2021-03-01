package com.ikkiking.base.exception;

import com.ikkiking.api.response.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadException extends RuntimeException {
    private ImageResponse imageResponse;
}
