package com.paleskyline.navicash.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.model.SubCategory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText amountField, descriptionField;
    private TextView categoryField, dateField;
    private ImageView categoryIcon;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SUB_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                SubCategory sc = data.getParcelableExtra("subCategory");
                int iconId = getApplicationContext().getResources().getIdentifier("@drawable/" + sc.getCategoryIcon(), null, getApplicationContext().getPackageName());
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
}
