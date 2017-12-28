package com.paleskyline.accroo.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.model.SubCategory;
import com.paleskyline.accroo.model.Transaction;
import com.paleskyline.accroo.services.ApiService;
import com.paleskyline.accroo.services.InputService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText amountField, descriptionField;
    private TextView categoryField, dateField;
    private ImageView categoryIcon;
    private Button submitButton;
    private DatePickerDialog.OnDateSetListener datePicker;
    private Calendar calendar;
    private Transaction newTransaction, existingTransaction;
    private int selectedSubCategoryID;
    private ProgressDialog progressDialog;
    private final int SUB_CATEGORY_REQUEST = 1;
    private boolean editing = false;
    private boolean editable = true;
    private ApiService apiService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_transaction);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            amountField = findViewById(R.id.add_transaction_amount);
            descriptionField = findViewById(R.id.add_transaction_description);
            categoryField = findViewById(R.id.add_transaction_category);
            dateField = findViewById(R.id.add_transaction_date);
            categoryIcon = findViewById(R.id.add_transaction_category_icon);
            submitButton = findViewById(R.id.submit_transaction_button);

            progressDialog = new ProgressDialog(TransactionActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.submitting));
            progressDialog.setCancelable(false);

            apiService = new ApiService(this, getApplicationContext());
            calendar = Calendar.getInstance();

            existingTransaction = getIntent().getParcelableExtra("transaction");

            if (existingTransaction != null) {

                editing = true;
                calendar.setTime(existingTransaction.getDate());
                setTitle(R.string.title_activity_edit_transaction);

                amountField.setText(String.valueOf(existingTransaction.getFormattedAmount()));
                dateField.setText(dateFormat.format(existingTransaction.getDate()));
                descriptionField.setText(existingTransaction.getDescription());

                String icon = ((GeneralCategory) ((SubCategory) existingTransaction.getParent()).getParent()).getIconFile();
                int iconId = getApplicationContext().getResources().getIdentifier(
                        "@drawable/" + icon, null, getApplicationContext().getPackageName());

                categoryIcon.setImageResource(iconId);
                String subCategoryName = ((SubCategory) existingTransaction.getParent()).getCategoryName();
                categoryField.setText(subCategoryName);

                this.selectedSubCategoryID = existingTransaction.getSubCategoryID();

                submitButton.setText(getResources().getString(R.string.save));
                toggleEditing();

            } else {
                updateDate();
            }

            datePicker = new DatePickerDialog.OnDateSetListener() {
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
                    new DatePickerDialog(TransactionActivity.this, datePicker, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(dateField.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });

            categoryField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), SelectSubCategoryActivity.class);
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

                    progressDialog.show();

                    String formattedDescription = InputService.capitaliseAndTrim(descriptionField.getText().toString());

                    if (editing) {
                        existingTransaction.setAmount(Double.parseDouble(amountField.getText().toString()));
                        existingTransaction.setSubCategoryID(selectedSubCategoryID);
                        existingTransaction.setDate(calendar.getTime());
                        existingTransaction.setDescription(formattedDescription);
                        apiService.updateTransaction(existingTransaction);
                    } else {
                        newTransaction = new Transaction(selectedSubCategoryID,
                                calendar.getTime(),
                                Double.parseDouble(amountField.getText().toString()),
                                formattedDescription);
                        apiService.createTransaction(newTransaction);
                    }
                }
            });
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                String icon = ((GeneralCategory) subCategory.getParent()).getIconFile();
                int iconId = getApplicationContext().getResources().getIdentifier(
                        "@drawable/" + icon, null, getApplicationContext().getPackageName());
                categoryIcon.setImageResource(iconId);
                categoryField.setText(subCategory.getCategoryName());
            }
        }
    }

    private void updateDate() {
        String dateString = dateFormat.format(calendar.getTime());
        dateField.setText(dateString);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
        builder.setMessage(R.string.delete_transaction)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        apiService.deleteTransaction(existingTransaction);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).create().show();
    }

    private boolean isValidAmount() {
        String amountString = amountField.getText().toString();
        if (amountString.length() > 0) {
            Double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                Toast.makeText(getApplicationContext(), R.string.negative_amount, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        Toast.makeText(getApplicationContext(), R.string.enter_amount, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isCategorySelected() {
        if (selectedSubCategoryID == 0) {
            Toast.makeText(getApplicationContext(), R.string.select_sub_category, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == ApiService.CREATE_TRANSACTION) {
            amountField.getText().clear();
            descriptionField.getText().clear();
            amountField.requestFocus();
            Toast.makeText(getApplicationContext(), R.string.transaction_added, Toast.LENGTH_SHORT).show();
        } else if (requestType == ApiService.UPDATE_TRANSACTION) {
            Toast.makeText(getApplicationContext(), R.string.transaction_updated, Toast.LENGTH_SHORT).show();
            finish();
        } else if (requestType == ApiService.DELETE_TRANSACTION) {
            Toast.makeText(getApplicationContext(), R.string.transaction_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.UNAUTHORIZED) {
            Toast.makeText(getApplicationContext(), R.string.login_required, Toast.LENGTH_LONG).show();
            apiService.logout();
            relaunch();
        } else {
            String message;
            switch (errorCode) {
                case ApiService.CONNECTION_ERROR:
                    message = getResources().getString(R.string.connection_error);
                    break;
                case ApiService.TIMEOUT_ERROR:
                    message = getResources().getString(R.string.timeout_error);
                    break;
                case ApiService.INVALID_DATE_RANGE:
                    message = getResources().getString(R.string.invalid_date_range);
                    break;
                default:
                    message = getResources().getString(R.string.general_error);
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
