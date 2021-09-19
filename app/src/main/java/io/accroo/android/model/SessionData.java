package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class SessionData {

    private UUID id;
    @Expose private String deviceBrand;
    @Expose private String deviceModel;
    @Expose private String deviceName;
    private String dateCreated, dateLastRefreshed;

    public SessionData(String deviceBrand, String deviceModel, String deviceName) {
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.deviceName = deviceName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    @Override
    public String toString() {
        return "SessionData{" +
                "id=" + id +
                ", deviceBrand='" + deviceBrand + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", dateLastRefreshed='" + dateLastRefreshed + '\'' +
                '}';
    }

}
