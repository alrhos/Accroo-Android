package io.accroo.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.fragments.SubCategoryFragment;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class EditSubCategoryActivity extends AppCompatActivity implements ApiService.RequestOutcome,
        SubCategoryFragment.FragmentInteractionListener {

    private SubCategoryFragment subCategoryFragment;
    private ApiService apiService;
    private ProgressDialog progressDialog;
    private SubCategory subCategory;
    private final int GENERAL_CATEGORY_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_edit_sub_category);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            apiService = new ApiService(this, getApplicationContext());
            subCategoryFragment = (SubCategoryFragment) getSupportFragmentManager().findFragmentById(R.id.edit_sub_category);
            subCategory = getIntent().getParcelableExtra("subCategory");
            progressDialog = new ProgressDialog(EditSubCategoryActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.saving));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(EditSubCategoryActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_resource) {
            deleteSubCategory();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void createSubCategory(SubCategory subCategory) {
        // Not invoked
    }

    @Override
    public void updateSubCategory(SubCategory subCategory) {
        progressDialog.show();
        apiService.updateSubCategory(subCategory);
    }

    @Override
    public void selectGeneralCategory() {
        Intent intent = new Intent(getApplicationContext(), SelectGeneralCategoryActivity.class);
        startActivityForResult(intent, GENERAL_CATEGORY_REQUEST);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void deleteSubCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditSubCategoryActivity.this);
        builder.setMessage(R.string.delete_sub_category)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.show();
                        apiService.deleteSubCategory(subCategory);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GENERAL_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                GeneralCategory generalCategory = data.getParcelableExtra("generalCategory");
                subCategoryFragment.setGeneralCategory(generalCategory);
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
        if (requestType == ApiService.UPDATE_SUB_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_updated, Toast.LENGTH_SHORT).show();
        } else if (requestType == ApiService.DELETE_SUB_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_deleted, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MaintenanceDialog.show(this);
        } else if (errorCode == ApiService.UNAUTHORIZED) {
            apiService.logout();
            relaunch();
        } else if (requestType == ApiService.DELETE_SUB_CATEGORY && errorCode == ApiService.NOT_FOUND) {
            // The category has already been deleted
            Toast.makeText(getApplicationContext(), R.string.category_deleted, Toast.LENGTH_SHORT).show();
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
