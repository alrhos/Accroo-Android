package com.paleskyline.navicash.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;
import com.paleskyline.navicash.services.DataProvider;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText amountField, descriptionField;
    private TextView categoryField, dateField;
    private ImageView categoryIcon;
    private Button submitButton;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    private Transaction transaction;
    private SubCategory selectedSubCategory;
    private ProgressDialog progressDialog;
    private final int SUB_CATEGORY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        amountField = (EditText) findViewById(R.id.add_transaction_amount);
        descriptionField = (EditText) findViewById(R.id.add_transaction_description);
        categoryField = (TextView) findViewById(R.id.add_transaction_category);
        dateField = (TextView) findViewById(R.id.add_transaction_date);
        categoryIcon = (ImageView) findViewById(R.id.add_transaction_category_icon);
        submitButton = (Button) findViewById(R.id.submit_transaction_button);

        progressDialog = new ProgressDialog(AddTransactionActivity.this);
        progressDialog.setMessage("Submitting...");

        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        updateDate();

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddTransactionActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(dateField.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });

        categoryField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectCategoryActivity.class);
                startActivityForResult(intent, SUB_CATEGORY_REQUEST);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isValidAmount()) {
                    return;
                }
                if (!isCategorySelected()) {
                    return;
                }
                if (!isDescriptionValid()) {
                    return;
                }

                final JSONObject[] dataReceiver = new JSONObject[1];
                RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                        this, dataReceiver) {

                    // TODO: review onSuccess and onFailure logic and UX

                    @Override
                    protected void onSuccess() {
                        progressDialog.dismiss();
                        amountField.getText().clear();
                        descriptionField.getText().clear();
                        amountField.requestFocus();

                        Toast.makeText(getApplicationContext(), "Transaction submitted",
                                Toast.LENGTH_SHORT).show();

                        // TODO: add condition to only add the transaction if its date is within the currently selected date range

                        DataProvider.getInstance().addTransaction(transaction);
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                };

                try {

                    transaction = new Transaction(selectedSubCategory.getId(),
                            dateField.getText().toString(),
                            Double.parseDouble(amountField.getText().toString()),
                            descriptionField.getText().toString());

                    JSONObject json = transaction.encrypt();

                    coordinator.addRequests(RestMethods.post(0, RestMethods.TRANSACTION, coordinator,
                            json, RestRequest.TOKEN, getApplicationContext()));

                    progressDialog.show();
                    coordinator.start();

                } catch (Exception e) {
                    // TODO: exception handling
                    e.printStackTrace();
                }



            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUB_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                SubCategory sc = data.getParcelableExtra("subCategory");
                this.selectedSubCategory = sc;
                int iconId = getApplicationContext().getResources().getIdentifier(
                        "@drawable/" + sc.getCategoryIcon(), null, getApplicationContext().getPackageName());
                categoryIcon.setImageResource(iconId);
                categoryField.setText(sc.getCategoryName());

            }
        }
    }

    private void updateDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.US);
        dateField.setText(df.format(calendar.getTime()));
    }

    // TODO: implement amount regex and toast

    private boolean isValidAmount() {
        return true;
    }

    // TODO: implement category selected check and toast

    private boolean isCategorySelected() {
        return true;
    }

    // TODO: implement description regex and toast

    private boolean isDescriptionValid() {
        return true;
    }

}
