package com.paleskyline.navicash.activities;

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

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.GeneralCategoryFragment;
import com.paleskyline.navicash.fragments.SubCategoryFragment;


public class CategoryActivity extends AppCompatActivity implements GeneralCategoryFragment.FragmentInteractionListener {

    private GeneralCategoryFragment generalCategoryFragment;
    private SubCategoryFragment subCategoryFragment;

    private final int ICON_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.category_toolbar);

        setSupportActionBar(toolbar);

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
    public void onIconClicked() {
        Intent intent = new Intent(getApplicationContext(), SelectIconActivity.class);
        startActivityForResult(intent, ICON_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ICON_REQUEST) {
            if (resultCode == RESULT_OK) {
                int iconID = data.getIntExtra("iconID", 0);
                String iconName = data.getStringExtra("iconName");
            }
        }
    }

}
