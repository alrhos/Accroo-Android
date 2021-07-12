package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class Session extends SecurePayload {

    @Expose(serialize = false) @SerializedName("user_id") private UUID userId;
    @Expose(serialize = false) @SerializedName("date_created") private DateTime dateCreated;
    @Expose(serialize = false) @SerializedName("date_last_refreshed") private DateTime dateLastRefreshed;
    @Expose(serialize = false) @SerializedName("refresh_token") private Jwt refreshToken;
    @Expose(serialize = false) @SerializedName("access_token") private Jwt accessToken;

    public Session(UUID id, String data, String nonce, UUID userId, DateTime dateCreated,
                   DateTime dateLastRefreshed, Jwt refreshToken, Jwt accessToken) {
        super(id, data, nonce);
        this.userId = userId;
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

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public DateTime getDateLastRefreshed() {
        return dateLastRefreshed;
    }

    public void setDateLastRefreshed(DateTime dateLastRefreshed) {
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

    public SessionData decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(getId(), getData(), getNonce());
        String sessionJson = CryptoManager.getInstance().decrypt(securePayload);
        SessionData sessionData = GsonUtil.getInstance().fromJson(sessionJson, SessionData.class);
        sessionData.setId(getId());
        sessionData.setDateCreated(this.dateCreated);
        sessionData.setDateLastRefreshed(this.dateLastRefreshed);
        return sessionData;
    }

}
