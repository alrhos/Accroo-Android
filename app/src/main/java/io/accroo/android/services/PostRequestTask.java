package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

import io.accroo.android.model.EncryptedGeneralCategory;
import io.accroo.android.model.EncryptedSubCategory;
import io.accroo.android.model.EncryptedTransaction;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.Jwt;
import io.accroo.android.model.Key;
import io.accroo.android.model.Session;
import io.accroo.android.model.SessionData;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.other.GsonUtil;

/**
 * Created by oscar on 4/07/17.
 */

public class PostRequestTask extends AsyncTask<String[], Boolean, Boolean> {

    private PostRequestOutcome postRequestOutcome;
    private Context context;
    private DateTime startDate, endDate;
    private Session session;
    private SessionData sessionData;
    private DateTime refreshTokenExpiry, accessTokenExpiry;
    private EncryptedTransaction encryptedTransaction;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private EncryptedGeneralCategory encryptedGeneralCategory;
    private SubCategory subCategory;
    private EncryptedSubCategory encryptedSubCategory;
    private int requestType;
    private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
    private ArrayList<SubCategory> subCategories = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private HashMap<String, Object> requestVariables;

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome,
                           Context context, HashMap<String, Object> requestVariables) {
        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
        this.requestVariables = requestVariables;
    }

    public interface PostRequestOutcome {
        void onPostRequestTaskSuccess(int requestType);
        void onPostRequestTaskFailure();
    }

    @Override
    protected Boolean doInBackground(String[]... dataReceiver) {
        try {
            switch (requestType) {

                case ApiService.GET_VISITOR_TOKEN:

                    Jwt accessToken = GsonUtil.getInstance().fromJson(dataReceiver[0][0], Jwt.class);
                    DateTime tokenExpiry = new DateTime(accessToken.getExpiresAt());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, accessToken.getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, tokenExpiry.toString());

                    return true;

                case ApiService.CREATE_SESSION:

                    session = GsonUtil.getInstance().fromJson(dataReceiver[0][0], Session.class);

                    refreshTokenExpiry = new DateTime(session.getRefreshToken().getExpiresAt());
                    accessTokenExpiry = new DateTime(session.getAccessToken().getExpiresAt());

                    CredentialService.getInstance(context).saveEntry(CredentialService.SESSION_ID_KEY, session.getId().toString());
                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, session.getEmail());
                    CredentialService.getInstance(context).saveEntry(CredentialService.USER_ID_KEY, session.getUserId().toString());
                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_KEY, session.getRefreshToken().getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_EXPIRY_KEY, refreshTokenExpiry.toString());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, session.getAccessToken().getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, accessTokenExpiry.toString());

                    return true;

                case ApiService.REAUTHENTICATE_SESSION:

                    session = GsonUtil.getInstance().fromJson(dataReceiver[0][0], Session.class);

                    refreshTokenExpiry = new DateTime(session.getRefreshToken().getExpiresAt());
                    accessTokenExpiry = new DateTime(session.getAccessToken().getExpiresAt());

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, session.getEmail());
                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_KEY, session.getRefreshToken().getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_EXPIRY_KEY, refreshTokenExpiry.toString());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, session.getAccessToken().getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, accessTokenExpiry.toString());

                    return true;

                case ApiService.INVALIDATE_SESSION:

                    sessionData = (SessionData) requestVariables.get("sessionData");
                    DataProvider.deleteSession(sessionData);

                    return true;

                case ApiService.GET_SESSIONS:

                    ArrayList<Session> sessions = GsonUtil.getInstance().listFromJson(dataReceiver[0][0], Session.class);
                    ArrayList<SessionData> sessionData = new ArrayList<>();

                    for (Session session : sessions) {
                        sessionData.add(session.decrypt());
                    }

                    DataProvider.setSessions(sessionData);
                    return true;

                case ApiService.LOAD_DEFAULT_DATA:

                    startDate = (DateTime) requestVariables.get("startDate");
                    endDate = (DateTime) requestVariables.get("endDate");

                    ArrayList<EncryptedGeneralCategory> encryptedGeneralCategories = GsonUtil.getInstance()
                            .listFromJson(dataReceiver[0][1], EncryptedGeneralCategory.class);

                    for (EncryptedGeneralCategory encryptedGeneralCategory : encryptedGeneralCategories) {
                        generalCategories.add(encryptedGeneralCategory.decrypt());
                    }

                    ArrayList<EncryptedSubCategory> encryptedSubCategories = GsonUtil.getInstance()
                            .listFromJson(dataReceiver[0][2], EncryptedSubCategory.class);

                    for (EncryptedSubCategory encryptedSubCategory : encryptedSubCategories) {
                        subCategories.add(encryptedSubCategory.decrypt());
                    }

                    ArrayList<EncryptedTransaction> encryptedTransactions = GsonUtil.getInstance()
                            .listFromJson(dataReceiver[0][3], EncryptedTransaction.class);

                    for (EncryptedTransaction encryptedTransaction : encryptedTransactions) {
                        Transaction transaction = encryptedTransaction.decrypt();
                        if (!transaction.getDate().isBefore(startDate) && !transaction.getDate()
                                .isAfter(endDate)) {
                            transactions.add(transaction);
                        }
                    }

                    DataProvider.loadData(generalCategories, subCategories, transactions);

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    encryptedTransaction = (EncryptedTransaction) GsonUtil.getInstance()
                            .objectFromJson(dataReceiver[0][0], EncryptedTransaction.class);
                    transaction = encryptedTransaction.decrypt();
                    DataProvider.addTransaction(transaction);
                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    encryptedTransaction = (EncryptedTransaction) GsonUtil.getInstance()
                            .objectFromJson(dataReceiver[0][0], EncryptedTransaction.class);
                    transaction = encryptedTransaction.decrypt();
                    DataProvider.updateTransaction(transaction);
                    return true;

                case ApiService.DELETE_TRANSACTION:

                    transaction = (Transaction) requestVariables.get("transaction");
                    DataProvider.deleteTransaction(transaction);
                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    encryptedGeneralCategory = (EncryptedGeneralCategory) GsonUtil.getInstance()
                            .objectFromJson(dataReceiver[0][0], EncryptedGeneralCategory.class);
                    generalCategory = encryptedGeneralCategory.decrypt();
                    DataProvider.addGeneralCategory(generalCategory);

                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    encryptedGeneralCategory = (EncryptedGeneralCategory) GsonUtil.getInstance()
                            .objectFromJson(dataReceiver[0][0], EncryptedGeneralCategory.class);
                    generalCategory = encryptedGeneralCategory.decrypt();
                    DataProvider.updateGeneralCategory(generalCategory);

                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    DataProvider.deleteGeneralCategory(generalCategory);
                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    encryptedSubCategory = (EncryptedSubCategory) GsonUtil.getInstance()
                            .objectFromJson(dataReceiver[0][0], EncryptedSubCategory.class);
                    subCategory = encryptedSubCategory.decrypt();
                    DataProvider.addSubCategory(subCategory);

                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    encryptedSubCategory = (EncryptedSubCategory) GsonUtil.getInstance()
                            .objectFromJson(dataReceiver[0][0], EncryptedSubCategory.class);
                    subCategory = encryptedSubCategory.decrypt();
                    DataProvider.updateSubCategory(subCategory);
                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    DataProvider.deleteSubCategory(subCategory);
                    return true;

                case ApiService.UPDATE_EMAIL:

                    String newEmail = (String) requestVariables.get("newEmail");
                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, newEmail);
                    return true;

                case ApiService.GET_KEY:

                    Key key = (Key) GsonUtil.getInstance().objectFromJson(dataReceiver[0][0], Key.class);
                    DataProvider.setKey(key);
                    return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            postRequestOutcome.onPostRequestTaskSuccess(requestType);
        } else {
            postRequestOutcome.onPostRequestTaskFailure();
        }
    }

}
