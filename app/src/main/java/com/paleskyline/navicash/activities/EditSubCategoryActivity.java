package com.paleskyline.navicash.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.SubCategoryFragment;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.services.ApiService;

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
        setContentView(R.layout.activity_edit_sub_category);
        apiService = new ApiService(this, getApplicationContext());
        subCategoryFragment = (SubCategoryFragment) getSupportFragmentManager().findFragmentById(R.id.edit_sub_category);
        subCategoryFragment.toggleEditing();
        subCategory = getIntent().getParcelableExtra("subCategory");
        progressDialog = new ProgressDialog(EditSubCategoryActivity.this);
        progressDialog.setMessage("Saving...");
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
        // TODO: add confirm dialog
        progressDialog.show();
        apiService.deleteSubCategory(subCategory);
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

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == ApiService.UPDATE_SUB_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_updated, Toast.LENGTH_LONG).show();
        } else if (requestType == ApiService.DELETE_SUB_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_deleted, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    public void onUnsuccessfulRequest(int requestType, int errorCode) {
        progressDialog.dismiss();
        //System.out.println(errorMessage);
    }

    @Override
    public void onUnsuccessfulDecryption() {
        progressDialog.dismiss();
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        progressDialog.dismiss();
        System.out.println("GENERAL ERROR");
    }

}
