package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by oscar on 25/03/17.
 */

public class Transaction implements Relationship, Parcelable {

    private int id;
    private int subCategoryId;
    @Expose private DateTime date;
    @Expose private double amount;
    @Expose private String description = "";
    private SubCategory parent;
    private static DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public Transaction(int subCategoryId, DateTime date, double amount, String description) {
        this.subCategoryId = subCategoryId;
        this.date = date;
        this.amount = amount;
        if (description != null) {
            this.description = description;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
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
        return new EncryptedTransaction(this.id, this.subCategoryId,
                securePayload.getData(), securePayload.getNonce());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.subCategoryId);
        dest.writeSerializable(date);
        dest.writeDouble(this.amount);
        dest.writeString(this.description);
        dest.writeParcelable(this.parent, flags);
        dest.writeSerializable(this.decimalFormat);
        dest.writeSerializable(this.dateFormat);
    }

    protected Transaction(Parcel in) {
        this.id = in.readInt();
        this.subCategoryId = in.readInt();
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", subCategoryId=" + subCategoryId +
                ", date=" + date +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", dateFormat=" + dateFormat +
                '}';
    }
}
