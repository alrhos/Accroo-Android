package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.accroo.android.R;
import io.accroo.android.fragments.CategoryOverviewFragment;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;

public class SelectSubCategoryActivity extends AppCompatActivity implements CategoryOverviewFragment.FragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_select_sub_category);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            findViewById(R.id.category_overview_swipe_refresh).setEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        Toast.makeText(getApplicationContext(), R.string.select_sub_category, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategorySwipeRefresh() {
        // No invoked
    }

    @Override
    public void onSubCategoryClicked(SubCategory subCategory) {
        Intent data = new Intent();
        data.putExtra("subCategory", subCategory);
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void hideFab() {
        // Not invoked
    }

    @Override
    public void showFab() {
        // Not invoked
    }

}
