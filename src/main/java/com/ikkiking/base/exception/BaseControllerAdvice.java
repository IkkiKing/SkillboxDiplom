package com.ikkiking.base.exception;

import com.ikkiking.api.response.PostResponse.PostByIdResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseControllerAdvice {

    @ExceptionHandler(PostNotFoundException.class)
    public Object postNotFoundException(){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
