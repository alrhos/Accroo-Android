package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import io.accroo.android.R;
import io.accroo.android.fragments.GeneralCategoryFragment;
import io.accroo.android.fragments.SubCategoryFragment;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.other.MessageDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

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
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_category);
            Toolbar toolbar = findViewById(R.id.category_toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            progressDialog = new ProgressDialog(CategoryActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.saving));

            apiService = new ApiService(this, getApplicationContext());

            CategoryActivity.PagerAdapter pagerAdapter = new CategoryActivity.PagerAdapter(getSupportFragmentManager(), CategoryActivity.this);

            ViewPager viewPager = findViewById(R.id.category_viewpager);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(2);

            final TabLayout tabLayout = findViewById(R.id.category_tab_layout);
            tabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }
            }
        }
    }

     class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = {
                getResources().getString(R.string.general_category),
                getResources().getString(R.string.sub_category)
        };
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
            TextView tv = tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
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
        Utils.hideSoftKeyboard(CategoryActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (generalCategoryFragment == null || subCategoryFragment == null) {
            relaunch();
        } else {
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
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == ApiService.CREATE_GENERAL_CATEGORY) {
            generalCategoryFragment.clearFields();
            Toast.makeText(getApplicationContext(), R.string.category_added, Toast.LENGTH_SHORT).show();
        } else if (requestType == ApiService.CREATE_SUB_CATEGORY) {
            subCategoryFragment.clearFields();
            Toast.makeText(getApplicationContext(), R.string.category_added, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MessageDialog.show(CategoryActivity.this,
                    getResources().getString(R.string.maintenance_title),
                    getResources().getString(R.string.maintenance_message));
        } else if (errorCode == ApiService.GONE) {
            MessageDialog.show(CategoryActivity.this,
                    getResources().getString(R.string.upgrade_required_title),
                    getResources().getString(R.string.upgrade_required_message));
        } else if (errorCode == ApiService.UNAUTHORIZED || errorCode == ApiService.UNPROCESSABLE_ENTITY) {
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
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

}
