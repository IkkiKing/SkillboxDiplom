package com.ikkiking.api.response.AuthResponse;

public class AuthCheckResponse {
    private boolean result;
    private AuthUser user;

    public AuthCheckResponse(boolean result, AuthUser user) {
        this.result = result;
        this.user = user;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public AuthUser getUser() {
        return user;
    }

    public void setUser(AuthUser user) {
        this.user = user;
    }
}
