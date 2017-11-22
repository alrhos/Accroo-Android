package com.paleskyline.accroo.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paleskyline.accroo.BuildConfig;
import com.paleskyline.accroo.R;

public class AboutActivity extends AppCompatActivity {

    private TextView terms, privacyPolicy, licenses, contact, versionString;
    private static final String TERMS_OF_USE = "https://www.google.com";
    private static final String PRIVACY_POLICY = "https://www.youtube.com";
    private static final String ACCROO_SUPPORT = "support@accroo.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        terms = findViewById(R.id.terms);
        privacyPolicy = findViewById(R.id.privacy_policy);
        licenses = findViewById(R.id.licenses);
        contact = findViewById(R.id.contact);
        versionString = findViewById(R.id.version_string);

        versionString.setText(BuildConfig.VERSION_NAME);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(TERMS_OF_USE));
                startActivity(intent);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(PRIVACY_POLICY));
                startActivity(intent);
            }
        });

        licenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", ACCROO_SUPPORT, null));
                try {
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.email_chooser)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), R.string.no_email_client, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
