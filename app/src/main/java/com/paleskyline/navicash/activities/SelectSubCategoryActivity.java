package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.CategoryOverviewFragment;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;

public class SelectSubCategoryActivity extends AppCompatActivity implements CategoryOverviewFragment.FragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_select_sub_category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        CategoryOverviewFragment categoryOverviewFragment = new CategoryOverviewFragment();
//        fragmentTransaction.add(R.id.select_category_fragment, categoryOverviewFragment);
//        fragmentTransaction.commit();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        Toast.makeText(getApplicationContext(), "Select a sub category", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategorySwipeRefresh() {
        // TODO: call get all categories data or consider disable swipe to refresh here
    }

    @Override
    public void onSubCategoryClicked(SubCategory subCategory) {
        Intent data = new Intent();
        data.putExtra("subCategory", subCategory);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public FloatingActionButton getFab() {
        // Not invoked
        return null;
    }

}
