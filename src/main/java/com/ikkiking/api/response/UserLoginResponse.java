package com.ikkiking.api.response;

import lombok.Data;

@Data
public class UserLoginResponse {
    private Long id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
