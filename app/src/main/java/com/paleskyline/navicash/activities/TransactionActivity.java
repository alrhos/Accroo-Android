package com.paleskyline.navicash.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.paleskyline.navicash.services.DataServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity implements DataServices.RequestOutcome {

    private EditText amountField, descriptionField;
    private TextView categoryField, dateField;
    private ImageView categoryIcon;
    private Button submitButton;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    private Transaction newTransaction, existingTransaction;
    private int selectedSubCategoryID;
    private ProgressDialog progressDialog;
    private final int SUB_CATEGORY_REQUEST = 1;
    private boolean editing = false;
    private boolean editable = true;
    private DataServices dataServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

    //    Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_toolbar);
    //    setSupportActionBar(toolbar);

        amountField = (EditText) findViewById(R.id.add_transaction_amount);
        descriptionField = (EditText) findViewById(R.id.add_transaction_description);
        categoryField = (TextView) findViewById(R.id.add_transaction_category);
        dateField = (TextView) findViewById(R.id.add_transaction_date);
        categoryIcon = (ImageView) findViewById(R.id.add_transaction_category_icon);
        submitButton = (Button) findViewById(R.id.submit_transaction_button);

        progressDialog = new ProgressDialog(TransactionActivity.this);
        progressDialog.setMessage("Submitting...");

        dataServices = new DataServices(this, getApplicationContext());
        calendar = Calendar.getInstance();

        existingTransaction = getIntent().getParcelableExtra("transaction");

        if (existingTransaction != null) {

            editing = true;

            amountField.setText(String.valueOf(existingTransaction.getAmount()));
            dateField.setText(existingTransaction.getDateString());
            descriptionField.setText(existingTransaction.getDescription());

            int iconId = getApplicationContext().getResources().getIdentifier(
                    "@drawable/" + existingTransaction.getCategoryIcon(), null,
                    getApplicationContext().getPackageName());

            categoryIcon.setImageResource(iconId);
            categoryField.setText(existingTransaction.getSubCategoryName());

            this.selectedSubCategoryID = existingTransaction.getSubCategoryID();

            submitButton.setText("SAVE");
            toggleEditing();

        } else {
            updateDate();
        }

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(TransactionActivity.this, date, calendar.get(Calendar.YEAR),
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

                progressDialog.show();

                if (editing) {
                    existingTransaction.setAmount(Double.parseDouble(amountField.getText().toString()));
                    existingTransaction.setDateString(dateField.getText().toString());
                    existingTransaction.setDescription(descriptionField.getText().toString());
                    dataServices.updateTransaction(existingTransaction);
                } else {
                    newTransaction = new Transaction(selectedSubCategoryID,
                            dateField.getText().toString(),
                            Double.parseDouble(amountField.getText().toString()),
                            descriptionField.getText().toString());
                    dataServices.createTransaction(newTransaction);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editing) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_resource:
                toggleEditing();
                return true;
            case R.id.delete_resource:
                deleteTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUB_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                SubCategory subCategory = data.getParcelableExtra("subCategory");
                this.selectedSubCategoryID = subCategory.getId();
                int iconId = getApplicationContext().getResources().getIdentifier(
                        "@drawable/" + subCategory.getCategoryIcon(), null, getApplicationContext().getPackageName());
                categoryIcon.setImageResource(iconId);
                categoryField.setText(subCategory.getCategoryName());

            }
        }
    }

    private void updateDate() {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(dateFormat, Locale.US);
        dateField.setText(df.format(calendar.getTime()));
    }

    private void toggleEditing() {
        editable = !editable;
        amountField.setEnabled(editable);
        categoryField.setEnabled(editable);
        dateField.setEnabled(editable);
        descriptionField.setEnabled(editable);
        submitButton.setEnabled(editable);
    }

    private void deleteTransaction() {
        // TODO: add confirmation dialog before deleting
        progressDialog.show();
        dataServices.deleteTransaction(existingTransaction);
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

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == DataServices.CREATE_TRANSACTION) {
            amountField.getText().clear();
            descriptionField.getText().clear();
            amountField.requestFocus();
        } else if (requestType == DataServices.UPDATE_TRANSACTION) {

        } else if (requestType == DataServices.DELETE_TRANSACTION) {
            // TODO: return to main activity
            Toast.makeText(getApplicationContext(), "Transaction deleted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onUnsuccessfulRequest(String errorMessage) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), errorMessage,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnsuccessfulDecryption() {
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        System.out.println("GENERAL ERROR");
    }

}
