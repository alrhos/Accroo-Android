package com.paleskyline.navicash.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.GeneralCategoryFragment;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.services.ApiService;

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
        setContentView(R.layout.activity_edit_general_category);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = new ApiService(this, getApplicationContext());
        generalCategoryFragment = (GeneralCategoryFragment) getSupportFragmentManager().findFragmentById(R.id.edit_general_category);
        generalCategoryFragment.toggleEditing();
        generalCategory = getIntent().getParcelableExtra("generalCategory");
        progressDialog = new ProgressDialog(EditGeneralCategoryActivity.this);
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
    public void onUnsuccessfulRequest(int requestType, int errorCode) {
        progressDialog.dismiss();
        System.out.println(errorCode);
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
        // TODO: add confirm dialog
        progressDialog.show();
        apiService.deleteGeneralCategory(generalCategory);
    }

}
