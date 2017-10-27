package com.paleskyline.accroo.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.fragments.GeneralCategoryFragment;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.services.ApiService;

public class EditGeneralCategoryActivity extends AppCompatActivity implements ApiService.RequestOutcome,
        GeneralCategoryFragment.FragmentInteractionListener {

    private ProgressDialog progressDialog;
    private ApiService apiService;

    private GeneralCategoryFragment generalCategoryFragment;

    private GeneralCategory generalCategory;

    private final int ICON_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_edit_general_category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            apiService = new ApiService(this, getApplicationContext());
            generalCategoryFragment = (GeneralCategoryFragment) getSupportFragmentManager().findFragmentById(R.id.edit_general_category);
            generalCategoryFragment.toggleEditing();
            generalCategory = getIntent().getParcelableExtra("generalCategory");
            progressDialog = new ProgressDialog(EditGeneralCategoryActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Saving...");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_resource:
                generalCategoryFragment.toggleEditing();
                return true;
            case R.id.delete_resource:
                deleteGeneralCategory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ICON_REQUEST) {
            if (resultCode == RESULT_OK) {
                int iconID = data.getIntExtra("iconID", 0);
                String iconName = data.getStringExtra("iconName");
                generalCategoryFragment.updateIcon(iconID, iconName);
            }
        }
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == ApiService.UPDATE_GENERAL_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_updated, Toast.LENGTH_LONG).show();
        } else if (requestType == ApiService.DELETE_GENERAL_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_deleted, Toast.LENGTH_LONG).show();
        }
        finish();
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
                default:
                    message = getResources().getString(R.string.general_error);
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        System.out.println("GENERAL ERROR");
    }

    @Override
    public void onIconClicked() {
        Intent intent = new Intent(getApplicationContext(), SelectIconActivity.class);
        startActivityForResult(intent, ICON_REQUEST);
    }

    @Override
    public void createGeneralCategory(GeneralCategory generalCategory) {
        // Not invoked
    }

    @Override
    public void updateGeneralCategory(GeneralCategory generalCategory) {
        progressDialog.show();
        System.out.println(generalCategory.toString());
        apiService.updateGeneralCategory(generalCategory);
    }

    private void deleteGeneralCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditGeneralCategoryActivity.this);
        builder.setMessage(R.string.delete_general_category)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        apiService.deleteGeneralCategory(generalCategory);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).create().show();
    }

}
