package io.accroo.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.services.ApiService;

public class ForgotPasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText email;
    private Button sendLink;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.reset_email);
        sendLink = findViewById(R.id.send_link);

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        apiService = new ApiService(this, getApplicationContext());

        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().length() > 0) {
                    progressDialog.show();
                    apiService.forgotPassword(email.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
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
        email.setText("");
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage(R.string.reset_link_sent)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        String message;
        switch (errorCode) {
            case ApiService.CONNECTION_ERROR:
                message = getResources().getString(R.string.connection_error);
                break;
            case ApiService.TIMEOUT_ERROR:
                message = getResources().getString(R.string.timeout_error);
                break;
            default:
                message = getResources().getString(R.string.general_error);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
