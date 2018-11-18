package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oscar on 25/03/17.
 */

public class Transaction implements Relationship, Parcelable {

    private int id;
    private int subCategoryId;
    //@Expose private Date date;
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

//    public Transaction(JSONObject json) throws JSONException, UnsupportedEncodingException {
//        decrypt(json);
//    }

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

//    public Date getDate() {
//        try {
//            String adjustedDateString = dateFormat.format(date);
//            return dateFormat.parse(adjustedDateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

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

//    @Override
//    public JSONObject encrypt() throws JSONException {
//        JSONObject transactionData = new JSONObject();
//
//        transactionData.put("date", date.getTime());
//        transactionData.put("amount", amount);
//        transactionData.put("description", description);
//
//        SecurePayload payload = CryptoManager.getInstance().encrypt(transactionData.toString());
//        JSONObject json = new JSONObject();
//
//        if (id != 0) {
//            json.put("id", id);
//        }
//
//        json.put("subCategoryId", subCategoryId);
//        json.put("data", payload.getData());
//        json.put("nonce", payload.getNonce());
//
//        return json;
//    }
//
//    @Override
//    public void decrypt(JSONObject json) throws JSONException, UnsupportedEncodingException {
//        SecurePayload payload = new SecurePayload(json.getString("data"), json.getString("nonce"));
//
//        String transactionString = CryptoManager.getInstance().decrypt(payload);
//        JSONObject transactionJson = new JSONObject(transactionString);
//
//        this.id = json.getInt("id");
//        this.subCategoryId = json.getInt("subCategoryId");
//        this.date = new Date(transactionJson.getLong("date"));
//        this.amount = transactionJson.getDouble("amount");
//        this.description = transactionJson.getString("description");
//    }

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
        this.decimalFormat = (DecimalFormat) in.readSerializable();
        this.dateFormat = (SimpleDateFormat) in.readSerializable();
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
