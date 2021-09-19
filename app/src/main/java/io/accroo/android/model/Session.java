package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class Session extends SecurePayload {

    @Expose(serialize = false) @SerializedName("user_id") private UUID userId;
    @Expose(serialize = false) private String email;
    @Expose(serialize = false) @SerializedName("date_created") private String dateCreated;
    @Expose(serialize = false) @SerializedName("date_last_refreshed") private String dateLastRefreshed;
    @Expose(serialize = false) @SerializedName("refresh_token") private Jwt refreshToken;
    @Expose(serialize = false) @SerializedName("access_token") private Jwt accessToken;

    public Session(UUID id, String email, String data, String nonce, UUID userId, String dateCreated,
                   String dateLastRefreshed, Jwt refreshToken, Jwt accessToken) {
        super(id, data, nonce);
        this.userId = userId;
        this.email = email;
        this.dateCreated = dateCreated;
        this.dateLastRefreshed = dateLastRefreshed;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateLastRefreshed() {
        return dateLastRefreshed;
    }

    public void setDateLastRefreshed(String dateLastRefreshed) {
        this.dateLastRefreshed = dateLastRefreshed;
    }

    public Jwt getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(Jwt refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Jwt getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Jwt accessToken) {
        this.accessToken = accessToken;
    }

    public SessionData decrypt() {
        SecurePayload securePayload = new SecurePayload(getId(), getData(), getNonce());
        SessionData sessionData;
        try {
            String sessionJson = CryptoManager.getInstance().decrypt(securePayload);
            sessionData = GsonUtil.getInstance().fromJson(sessionJson, SessionData.class);
        } catch (Exception e) {
            e.printStackTrace();
            sessionData = new SessionData(null, null, null);
        }
        sessionData.setId(getId());
        sessionData.setDateCreated(this.dateCreated);
        sessionData.setDateLastRefreshed(this.dateLastRefreshed);
        return sessionData;
    }

}
