package com.ikkiking.base.exception;

import lombok.Data;

@Data
public class CommentException extends RuntimeException {
    public CommentException(String message) {
        super(message);
    }
}
