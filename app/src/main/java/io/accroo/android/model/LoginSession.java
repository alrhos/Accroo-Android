package io.accroo.android.model;

import com.google.gson.annotations.Expose;

public class LoginSession {
    @Expose private int userId;
    @Expose private RefreshToken refreshToken;
    @Expose private AccessToken accessToken;

    public LoginSession(int userId, RefreshToken refreshToken, AccessToken accessToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

}
