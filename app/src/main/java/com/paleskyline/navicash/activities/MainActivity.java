package com.paleskyline.navicash.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.fragments.CategoryOverviewFragment;
import com.paleskyline.navicash.fragments.SummaryFragment;
import com.paleskyline.navicash.fragments.TransactionsFragment;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.services.ApiService;

public class MainActivity extends AppCompatActivity implements SummaryFragment.FragmentInteractionListener,
        TransactionsFragment.FragmentInteractionListener, CategoryOverviewFragment.FragmentInteractionListener,
     ApiService.RequestOutcome {

    private SummaryFragment summaryFragment;
    private TransactionsFragment transactionsFragment;
    private CategoryOverviewFragment categoryOverviewFragment;

    private ApiService apiService;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // TODO: review if this is even needed

        // Iterate over all tabs and set the custom view
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedTab = tabLayout.getSelectedTabPosition();
                if (selectedTab == 0 || selectedTab == 1) {
                    Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                    startActivity(intent);
                } else if (selectedTab == 2) {
                    Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                    startActivity(intent);
                }
            }
        });

        apiService = new ApiService(this, getApplicationContext());

        System.out.println("ON CREATE CALLED");

//        RootCategory[] data = DataProvider.getInstance().getRootCategories();
//
//        for (int i = 0; i < data.length; i++) {
//            for (GeneralCategory gc : data[i].getGeneralCategories()) {
//                System.out.println(gc.toString());
//                System.out.println(gc.getFormattedTransactionTotal());
//                for (SubCategory sc : gc.getSubCategories()) {
//                    System.out.println(sc.toString());
//                    for (Transaction t : sc.getTransactions()) {
//                        System.out.println(t.toString());
//                    }
//                }
//            }
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("ON MAIN ACTIVITY RESUME");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("MAIN ACTIVITY STOPPED");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (summaryFragment != null) {
            summaryFragment.refreshAdapter();
        }
        if (transactionsFragment != null) {
            transactionsFragment.refreshAdapter();
        }
        if (categoryOverviewFragment != null) {
            categoryOverviewFragment.refreshAdapter();
            System.out.println("CATEGORY OVERVIEW ADAPTER REFRESHED");
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


//        System.out.println("MAIN ACTIVITY - on restore instance state");
////        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
////        startActivity(intent);
//        Parcelable[] savedData = savedInstanceState.getParcelableArray("workingData");
//        RootCategory[] data = new RootCategory[savedData.length];
//        System.arraycopy(savedData, 0, data, 0, savedData.length);
//        DataProvider.getInstance().setRootCategories(data);
//
//        for (int i = 0; i < data.length; i++) {
//            for (GeneralCategory gc : data[i].getGeneralCategories()) {
//                System.out.println(gc.toString());
//                System.out.println(gc.getFormattedTransactionTotal());
//                for (SubCategory sc : gc.getSubCategories()) {
//                    System.out.println(sc.toString());
//                    for (Transaction t : sc.getTransactions()) {
//                        System.out.println(t.toString());
//                    }
//                }
//            }
//        }
//
//        summaryFragment = (SummaryFragment) getSupportFragmentManager().getFragment(savedInstanceState, "summaryFragment");
//        transactionsFragment = (TransactionsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "transactionFragment");
//        categoryOverviewFragment = (CategoryOverviewFragment) getSupportFragmentManager().getFragment(savedInstanceState, "categoryFragment");

        //summaryFragment.refreshAdapter();



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putParcelableArray("workingData", DataProvider.getInstance().getRootCategories());
//        if (summaryFragment != null) {
//            getSupportFragmentManager().putFragment(savedInstanceState, "summaryFragment", summaryFragment);
//        }
//        if (transactionsFragment != null) {
//            getSupportFragmentManager().putFragment(savedInstanceState, "transactionFragment", transactionsFragment);
//        }
//        if (categoryOverviewFragment != null) {
//            getSupportFragmentManager().putFragment(savedInstanceState, "categoryFragment", categoryOverviewFragment);
//        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

     class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = {"Summary", "Transactions", "Categories"};
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
                    //return new SummaryFragment();
                    return summaryFragment = new SummaryFragment();
                case 1:
                   // return new Fragment();
                    //return new TransactionsFragment();
                    return transactionsFragment = new TransactionsFragment();
                case 2:
                    return categoryOverviewFragment = new CategoryOverviewFragment();
            }

            return null;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }

    }

    @Override
    public void onSummarySwipeRefresh() {
        summaryFragment.setRefreshStatus(true);
        apiService.getDefaultData("");
    }

    @Override
    public void onTransactionSwipeRefresh() {
        transactionsFragment.setRefreshStatus(true);
        apiService.getDefaultData("");
    }

    @Override
    public void onTransactionSelected(Transaction transaction) {
        Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
        intent.putExtra("transaction", transaction);
        startActivity(intent);
    }

    @Override
    public void onCategorySwipeRefresh() {
        categoryOverviewFragment.setRefreshStatus(true);
        apiService.getDefaultData("");
    }

    @Override
    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        Intent intent = new Intent(getApplicationContext(), EditGeneralCategoryActivity.class);
        intent.putExtra("generalCategory", generalCategory);
        startActivity(intent);
    }

    @Override
    public void onSubCategoryClicked(SubCategory subCategory) {
        Intent intent = new Intent(getApplicationContext(), EditSubCategoryActivity.class);
        intent.putExtra("subCategory", subCategory);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        if (summaryFragment != null) {
            summaryFragment.refreshAdapter();
            summaryFragment.setRefreshStatus(false);
        }
        if (transactionsFragment != null) {
            transactionsFragment.refreshAdapter();
            transactionsFragment.setRefreshStatus(false);
        }
        if (categoryOverviewFragment != null) {
            categoryOverviewFragment.refreshAdapter();
            categoryOverviewFragment.setRefreshStatus(false);
        }
    }

    private void hideRefreshing() {
        if (summaryFragment != null) {
            summaryFragment.setRefreshStatus(false);
        }
        if (transactionsFragment != null) {
            transactionsFragment.setRefreshStatus(false);
        }
        if (categoryOverviewFragment != null) {
            categoryOverviewFragment.setRefreshStatus(false);
        }
    }

    @Override
    public void onUnsuccessfulRequest(String errorMessage) {
        hideRefreshing();
    }

    @Override
    public void onUnsuccessfulDecryption() {
        hideRefreshing();
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        hideRefreshing();
        System.out.println("GENERAL ERROR");
    }

}