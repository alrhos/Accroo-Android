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
import com.paleskyline.accroo.fragments.SubCategoryFragment;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.model.SubCategory;
import com.paleskyline.accroo.services.ApiService;

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            apiService = new ApiService(this, getApplicationContext());
            subCategoryFragment = (SubCategoryFragment) getSupportFragmentManager().findFragmentById(R.id.edit_sub_category);
            subCategoryFragment.toggleEditing();
            subCategory = getIntent().getParcelableExtra("subCategory");
            progressDialog = new ProgressDialog(EditSubCategoryActivity.this);
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
                subCategoryFragment.toggleEditing();
                return true;
            case R.id.delete_resource:
                deleteSubCategory();
                return true;
            default:
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
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
