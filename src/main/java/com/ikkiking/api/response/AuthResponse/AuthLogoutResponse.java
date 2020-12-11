package com.ikkiking.api.response.AuthResponse;

public class AuthLogoutResponse {
    private boolean result;

    public AuthLogoutResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
