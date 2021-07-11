package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Jwt {

    @Expose(serialize = false) private String token;
    @Expose(serialize = false) @SerializedName("expires_at") private String expiresAt;

    public Jwt(String token, String expiresAt) {
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

    @Override
    public String toString() {
        return "Jwt{" +
                "token='" + token + '\'' +
                ", expiresAt='" + expiresAt + '\'' +
                '}';
    }

}
