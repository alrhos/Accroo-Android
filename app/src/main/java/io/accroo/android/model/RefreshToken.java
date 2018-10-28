package io.accroo.android.model;

import com.google.gson.annotations.Expose;

public class RefreshToken {
    @Expose private String token;
    @Expose private String expiresAt;

    public RefreshToken(String token, String expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}
