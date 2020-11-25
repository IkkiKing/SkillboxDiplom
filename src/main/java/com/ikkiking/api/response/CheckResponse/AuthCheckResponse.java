package com.ikkiking.api.response.CheckResponse;

public class AuthCheckResponse {
    private boolean result;
    private User user;

    public AuthCheckResponse(boolean result, User user) {
        this.result = result;
        this.user = user;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
