package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

/**
 * Created by oscar on 25/03/17.
 */

public class Transaction implements Relationship, Parcelable {

    private UUID id;
    private UUID subCategoryId;
    @Expose private DateTime date;
    @Expose private double amount;
    @Expose private String description = "";
    private SubCategory parent;
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public Transaction(UUID subCategoryId, DateTime date, double amount, String description) {
        this.subCategoryId = subCategoryId;
        this.date = date;
        this.amount = amount;
        if (description != null) {
            this.description = description;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(UUID subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public DateTime getDate() {
        return date;
    }

    public DateTime getDateWithoutTime() {
        return date.withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return decimalFormat.format(amount);
    }

    public String getFullyFormattedAmount() {
        return "$" + decimalFormat.format(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setParent(Object parent) {
        this.parent = (SubCategory) parent;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    public EncryptedTransaction encrypt() {
        String transactionJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(transactionJson);
        securePayload.setId(this.id);
        return new EncryptedTransaction(securePayload.getId(), securePayload.getData(),
                securePayload.getNonce(), this.subCategoryId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        dest.writeSerializable(this.subCategoryId);
        dest.writeSerializable(date);
        dest.writeDouble(this.amount);
        dest.writeString(this.description);
        dest.writeParcelable(this.parent, flags);
    }

    protected Transaction(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.subCategoryId = (UUID) in.readSerializable();
        this.date = (DateTime) in.readSerializable();
        this.amount = in.readDouble();
        this.description = in.readString();
        this.parent = in.readParcelable(SubCategory.class.getClassLoader());
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

}
