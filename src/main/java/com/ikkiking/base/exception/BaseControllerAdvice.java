package com.ikkiking.base.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class BaseControllerAdvice {

    @ExceptionHandler({PostNotFoundException.class})
    public Object postNotFoundException(PostNotFoundException ex){
        return response(HttpStatus.NOT_FOUND, ex);
    }
    @ExceptionHandler({RegistrationClosedException.class})
    public Object registrationClosedException(RegistrationClosedException ex){
        return response(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(SettingNotFoundException.class)
    public Object settingNotFoundException(SettingNotFoundException ex){
        return response(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(StatisticAccessException.class)
    public Object statisticAccessException(StatisticAccessException ex){
        return response(HttpStatus.UNAUTHORIZED, ex);
    }

    private Object response(HttpStatus status, RuntimeException ex){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, httpHeaders, status);

    }

}
