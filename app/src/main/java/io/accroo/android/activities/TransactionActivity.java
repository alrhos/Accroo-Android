package io.accroo.android.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.accroo.android.R;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.other.Constants;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class TransactionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText amountField, descriptionField;
    private TextView categoryField, dateField;
    private ImageView categoryIcon;
    private Button submitButton;
    private DatePickerDialog.OnDateSetListener datePicker;
    private DateTime date;
    private Transaction newTransaction, existingTransaction;
    private int selectedSubCategoryID;
    private ProgressDialog progressDialog;
    private final int SUB_CATEGORY_REQUEST = 1;
    private boolean editing = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_transaction);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            amountField = findViewById(R.id.add_transaction_amount);
            descriptionField = findViewById(R.id.add_transaction_description);
            categoryField = findViewById(R.id.add_transaction_category);
            dateField = findViewById(R.id.add_transaction_date);
            categoryIcon = findViewById(R.id.add_transaction_category_icon);
            submitButton = findViewById(R.id.submit_transaction_button);

            progressDialog = new ProgressDialog(TransactionActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.saving));
            progressDialog.setCancelable(false);

            apiService = new ApiService(this, getApplicationContext());

            existingTransaction = getIntent().getParcelableExtra("transaction");

            if (existingTransaction != null) {
                editing = true;
                date = existingTransaction.getDate();
                setTitle(R.string.title_activity_edit_transaction);

                amountField.setText(String.valueOf(existingTransaction.getFormattedAmount()));
                dateField.setText(existingTransaction.getDate().toString(Constants.DATE_FORMAT));
                descriptionField.setText(existingTransaction.getDescription());

                String icon = ((GeneralCategory) ((SubCategory) existingTransaction.getParent()).getParent()).getIconFile();
                int iconId = getApplicationContext().getResources().getIdentifier(
                        "@drawable/" + icon, null, getApplicationContext().getPackageName());

                categoryIcon.setImageResource(iconId);
                categoryIcon.setFocusableInTouchMode(true);
                categoryIcon.requestFocus();
                String subCategoryName = ((SubCategory) existingTransaction.getParent()).getCategoryName();
                categoryField.setText(subCategoryName);
                this.selectedSubCategoryID = existingTransaction.getSubCategoryId();
            } else {
                date = new DateTime();
                updateDate();
                amountField.setFocusableInTouchMode(true);
                amountField.requestFocus();
                Utils.showSoftKeyboard(TransactionActivity.this);
            }

            datePicker = new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    date = new DateTime()
                            .withYear(year)
                            .withMonthOfYear(monthOfYear + 1)
                            .withDayOfMonth(dayOfMonth);
                    updateDate();
                }
            };

            dateField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(TransactionActivity.this, datePicker, date.getYear(),
                            date.getMonthOfYear() - 1, date.getDayOfMonth()).show();
                }
            });

            categoryField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideSoftKeyboard(TransactionActivity.this);
                    Intent intent = new Intent(getApplicationContext(), SelectSubCategoryActivity.class);
                    startActivityForResult(intent, SUB_CATEGORY_REQUEST);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
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

                    String formattedDescription = Utils.capitaliseAndTrim(descriptionField.getText().toString());

                    if (editing) {
                        existingTransaction.setAmount(Double.parseDouble(amountField.getText().toString()));
                        existingTransaction.setSubCategoryId(selectedSubCategoryID);
                        existingTransaction.setDate(date);
                        existingTransaction.setDescription(formattedDescription);
                        apiService.updateTransaction(existingTransaction);
                    } else {
                        newTransaction = new Transaction(selectedSubCategoryID,
                                date.toDateTime(),
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
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(TransactionActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_resource) {
            deleteTransaction();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        dateField.setText(date.toString(Constants.DATE_FORMAT));
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
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                Toast.makeText(getApplicationContext(), R.string.negative_amount,
                        Toast.LENGTH_SHORT).show();
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
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MaintenanceDialog.show(TransactionActivity.this);
        } else if (errorCode == ApiService.UNAUTHORIZED) {
            apiService.logout();
            relaunch();
        } else if (requestType == ApiService.DELETE_TRANSACTION && errorCode == ApiService.NOT_FOUND) {
            // The transaction has already been deleted
            Toast.makeText(getApplicationContext(), R.string.transaction_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String message;
            switch (errorCode) {
                case ApiService.CONNECTION_ERROR:
                    message = getResources().getString(R.string.connection_error);
                    break;
                case ApiService.TIMEOUT_ERROR:
                    message = getResources().getString(R.string.timeout_error);
                    break;
                case ApiService.TOO_MANY_REQUESTS:
                    message = getResources().getString(R.string.too_many_requests);
                    break;
                default:
                    message = getResources().getString(R.string.general_error);
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
