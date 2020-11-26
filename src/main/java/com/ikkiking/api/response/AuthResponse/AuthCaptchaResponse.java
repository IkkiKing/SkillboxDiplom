package com.ikkiking.api.response.AuthResponse;

public class AuthCaptchaResponse {
    private String secret;
    private String image;

    public AuthCaptchaResponse(String secret, String image) {
        this.secret = secret;
        this.image = image;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
