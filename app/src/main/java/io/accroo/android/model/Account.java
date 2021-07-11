package io.accroo.android.model;

import android.util.Base64;

import com.google.gson.annotations.Expose;

import java.security.SecureRandom;

public class Account {
    @Expose private String email;
    @Expose private String verificationToken;

    public Account(String email) {
        this.email = email;
        this.verificationToken = generateVerificationToken();
    }

    public Account(String email, String verificationToken) {
        this.email = email;
        this.verificationToken = verificationToken;
    }

    private String generateVerificationToken() {
        // TODO: change to 6 digit code
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[5];
        random.nextBytes(bytes);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
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
