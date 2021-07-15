package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class SessionData {

    private UUID id, userId;
    @Expose private String deviceName;
    @Expose private String deviceOs;
    private String dateCreated, dateLastRefreshed;

//    public SessionData(String deviceName, String deviceOs) {
//        this.deviceName = deviceName;
//        this.deviceOs = deviceOs;
//    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceOs() {
        return deviceOs;
    }

    public void setDeviceOs(String deviceOs) {
        this.deviceOs = deviceOs;
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

    public SecurePayload encrypt() {
        String sessionDataJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(sessionDataJson);
        securePayload.setId(this.id);
        return securePayload;
    }

}
