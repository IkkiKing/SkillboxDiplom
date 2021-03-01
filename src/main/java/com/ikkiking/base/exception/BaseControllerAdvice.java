package com.ikkiking.base.exception;

import com.ikkiking.api.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
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

    @ExceptionHandler(ImageUploadException.class)
    public Object imageUploadException(ImageUploadException ex){
        return new ResponseEntity<>(ex.getImageResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentException.class)
    public Object commentException(CommentException ex){
        CommentAddResponse commentAddResponse = new CommentAddResponse();
        commentAddResponse.setErrors(new CommentAddError(ex.getMessage()));
        return new ResponseEntity<>(commentAddResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public Object authenticationException(){
        return ResponseEntity.ok(new LoginResponse());
    }

    @ExceptionHandler(RegistrationException.class)
    public Object registrationException(RegistrationException ex){
        log.warn("Unsuccessfull registerRequest");
        return ResponseEntity.ok(new RegisterResponse(false, ex.getRegisterErrorResponse()));
    }

    @ExceptionHandler(PasswordRestoreException.class)
    public Object registrationException(PasswordRestoreException ex){
        log.warn("Unsuccessfull password restore");
        return ResponseEntity.ok(new PasswordResponse(false, ex.getPasswordErrorResponse()));
    }

    @ExceptionHandler(ProfileException.class)
    public Object profileException(ProfileException ex){
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setErrors(ex.getProfileErrorResponse());
        return ResponseEntity.ok(profileResponse);
    }

    @ExceptionHandler(VoteException.class)
    public Object voteException(){
        return ResponseEntity.ok(new VoteResponse());
    }

    private Object response(HttpStatus status, RuntimeException ex){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, httpHeaders, status);

    }

}
