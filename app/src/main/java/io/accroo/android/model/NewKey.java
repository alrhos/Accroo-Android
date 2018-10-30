package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class NewKey implements Parcelable {

    @Expose private String encryptedKey;
    @Expose private String nonce;
    @Expose private String salt;
    @Expose private int algorithm;
    @Expose private int opslimit;
    @Expose private int memlimit;

    public NewKey(String encryptedKey, String nonce, String salt, int algorithm, int opslimit, int memlimit) {
        this.encryptedKey = encryptedKey;
        this.nonce = nonce;
        this.salt = salt;
        this.algorithm = algorithm;
        this.opslimit = opslimit;
        this.memlimit = memlimit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.encryptedKey);
        dest.writeString(this.nonce);
        dest.writeString(this.salt);
        dest.writeInt(this.algorithm);
        dest.writeInt(this.opslimit);
        dest.writeInt(this.memlimit);
    }

    protected NewKey(Parcel in) {
        this.encryptedKey = in.readString();
        this.nonce = in.readString();
        this.salt = in.readString();
        this.algorithm = in.readInt();
        this.opslimit = in.readInt();
        this.memlimit = in.readInt();
    }

    public static final Parcelable.Creator<NewKey> CREATOR = new Parcelable.Creator<NewKey>() {
        @Override
        public NewKey createFromParcel(Parcel source) {
            return new NewKey(source);
        }

        @Override
        public NewKey[] newArray(int size) {
            return new NewKey[size];
        }
    };

}
