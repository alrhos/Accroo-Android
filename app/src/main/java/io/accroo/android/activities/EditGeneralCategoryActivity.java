package io.accroo.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.accroo.android.R;
import io.accroo.android.fragments.GeneralCategoryFragment;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.other.MessageDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

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
            relaunch();
        } else {
            setContentView(R.layout.activity_edit_general_category);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            apiService = new ApiService(this, getApplicationContext());
            generalCategoryFragment = (GeneralCategoryFragment) getSupportFragmentManager().findFragmentById(R.id.edit_general_category);
            generalCategory = getIntent().getParcelableExtra("generalCategory");
            progressDialog = new ProgressDialog(EditGeneralCategoryActivity.this);
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
        Utils.hideSoftKeyboard(EditGeneralCategoryActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_resource) {
            deleteGeneralCategory();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            Toast.makeText(getApplicationContext(), R.string.category_updated, Toast.LENGTH_SHORT).show();
        } else if (requestType == ApiService.DELETE_GENERAL_CATEGORY) {
            Toast.makeText(getApplicationContext(), R.string.category_deleted, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MessageDialog.show(EditGeneralCategoryActivity.this,
                    getResources().getString(R.string.maintenance_title),
                    getResources().getString(R.string.maintenance_message));
        } else if (errorCode == ApiService.GONE) {
            MessageDialog.show(EditGeneralCategoryActivity.this,
                    getResources().getString(R.string.upgrade_required_title),
                    getResources().getString(R.string.upgrade_required_message));
        } else if (errorCode == ApiService.UNAUTHORIZED || errorCode == ApiService.UNPROCESSABLE_ENTITY) {
            apiService.logout();
            relaunch();
        } else if (requestType == ApiService.DELETE_GENERAL_CATEGORY && errorCode == ApiService.NOT_FOUND) {
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

    @Override
    public void onIconClicked() {
        Intent intent = new Intent(getApplicationContext(), SelectIconActivity.class);
        startActivityForResult(intent, ICON_REQUEST);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void createGeneralCategory(GeneralCategory generalCategory) {
        // Not invoked
    }

    @Override
    public void updateGeneralCategory(GeneralCategory generalCategory) {
        progressDialog.show();
        apiService.updateGeneralCategory(generalCategory);
    }

    private void deleteGeneralCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditGeneralCategoryActivity.this);
        builder.setMessage(R.string.delete_general_category)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    progressDialog.show();
                    apiService.deleteGeneralCategory(generalCategory);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {}).create().show();
    }

}
