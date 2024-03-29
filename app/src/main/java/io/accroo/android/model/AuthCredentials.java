package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.SecureRandom;
import java.text.DecimalFormat;

public class AuthCredentials {
    @Expose private String email;
    @Expose @SerializedName("verification_token") private String verificationToken;

    public AuthCredentials(String email) {
        this.email = email;
        this.verificationToken = generateVerificationToken();
    }

    public AuthCredentials(String email, String verificationToken) {
        this.email = email;
        this.verificationToken = verificationToken;
    }

    private String generateVerificationToken() {
        return new DecimalFormat("000000").format(new SecureRandom().nextInt(999999));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }



}
