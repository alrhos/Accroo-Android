package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.CategoryOverviewFragment;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;

public class SelectCategoryActivity extends AppCompatActivity implements CategoryOverviewFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        CategoryOverviewFragment categoryOverviewFragment = new CategoryOverviewFragment();
//        fragmentTransaction.add(R.id.select_category_fragment, categoryOverviewFragment);
//        fragmentTransaction.commit();

    }

    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        // Not needed
    }

    public void onSubCategoryClicked(SubCategory subCategory) {
        Intent data = new Intent();
        data.putExtra("subCategory", subCategory);
        setResult(RESULT_OK, data);
        finish();
    }

}
