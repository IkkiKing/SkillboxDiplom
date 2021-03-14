package com.ikkiking.base.exception;

import com.ikkiking.api.response.CommentAddError;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CommentException extends RuntimeException {
    private final CommentAddError commentAddError;
}
