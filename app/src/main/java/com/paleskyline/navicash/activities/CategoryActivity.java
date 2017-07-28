package com.paleskyline.navicash.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.GeneralCategoryFragment;
import com.paleskyline.navicash.fragments.SubCategoryFragment;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.services.ApiService;


public class CategoryActivity extends AppCompatActivity implements ApiService.RequestOutcome,
        GeneralCategoryFragment.FragmentInteractionListener, SubCategoryFragment.FragmentInteractionListener {

    private GeneralCategoryFragment generalCategoryFragment;
    private SubCategoryFragment subCategoryFragment;
    private ProgressDialog progressDialog;

    private ApiService apiService;

    private final int ICON_REQUEST = 1;
    private final int GENERAL_CATEGORY_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.category_toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(CategoryActivity.this);
        progressDialog.setMessage("Loading...");

        apiService = new ApiService(this, getApplicationContext());

        CategoryActivity.PagerAdapter pagerAdapter = new CategoryActivity.PagerAdapter(getSupportFragmentManager(), CategoryActivity.this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.category_viewpager);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.category_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // TODO: review if this is even needed

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
    }

     class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = {"General Category", "Sub Category"};
        Context context;

        private PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return generalCategoryFragment = new GeneralCategoryFragment();
                case 1:
                    return subCategoryFragment = new SubCategoryFragment();
            }

            return null;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
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
        } else if (requestCode == GENERAL_CATEGORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                GeneralCategory generalCategory = data.getParcelableExtra("generalCategory");
                subCategoryFragment.setGeneralCategory(generalCategory);
            }
        }
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == ApiService.CREATE_GENERAL_CATEGORY) {
            Toast.makeText(getApplicationContext(), "Category created", Toast.LENGTH_SHORT).show();
        } else if (requestType == ApiService.UPDATE_GENERAL_CATEGORY) {

        } else if (requestType == ApiService.DELETE_GENERAL_CATEGORY) {

        } else if (requestType == ApiService.CREATE_SUB_CATEGORY) {
            Toast.makeText(getApplicationContext(), "Category created", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUnsuccessfulRequest(int errorCode) {
        progressDialog.dismiss();
//        Toast.makeText(getApplicationContext(), errorMessage,
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnsuccessfulDecryption() {
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        System.out.println("GENERAL ERROR");
    }

    @Override
    public void onIconClicked() {
        Intent intent = new Intent(getApplicationContext(), SelectIconActivity.class);
        startActivityForResult(intent, ICON_REQUEST);
    }

    @Override
    public void createGeneralCategory(GeneralCategory generalCategory) {
        progressDialog.show();
        apiService.createGeneralCategory(generalCategory);
    }

    @Override
    public void updateGeneralCategory(GeneralCategory generalCategory) {
        // Not invoked
    }

    @Override
    public void createSubCategory(SubCategory subCategory) {
        progressDialog.show();
        apiService.createSubCategory(subCategory);
    }

    @Override
    public void updateSubCategory(SubCategory subCategory) {
        // Not invoked
    }

    @Override
    public void selectGeneralCategory() {
        Intent intent = new Intent(getApplicationContext(), SelectGeneralCategoryActivity.class);
        startActivityForResult(intent, GENERAL_CATEGORY_REQUEST);
    }

}
