package com.paleskyline.navicash.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.paleskyline.navicash.model.RootCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.services.DataProvider;
import com.paleskyline.navicash.services.DataServices;

public class MainActivity extends AppCompatActivity implements SummaryFragment.OnFragmentInteractionListener,
        TransactionsFragment.OnFragmentInteractionListener, CategoryOverviewFragment.OnFragmentInteractionListener,
     DataServices.RequestOutcome {

    private SummaryFragment summaryFragment;
    private TransactionsFragment transactionsFragment;
    private CategoryOverviewFragment categoryOverviewFragment;

    private DataServices dataServices;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
                Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
                startActivity(intent);
            }
        });

        dataServices = new DataServices(this, getApplicationContext());

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
        summaryFragment.refreshAdapter();
        transactionsFragment.refreshAdapter();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("MAIN ACTIVITY - on restore instance state");
//        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
//        startActivity(intent);
        Parcelable[] savedData = savedInstanceState.getParcelableArray("workingData");
        RootCategory[] data = new RootCategory[savedData.length];
        System.arraycopy(savedData, 0, data, 0, savedData.length);
        DataProvider.getInstance().setRootCategories(data);

        for (int i = 0; i < data.length; i++) {
            for (GeneralCategory gc : data[i].getGeneralCategories()) {
                System.out.println(gc.toString());
                System.out.println(gc.getFormattedTransactionTotal());
                for (SubCategory sc : gc.getSubCategories()) {
                    System.out.println(sc.toString());
                    for (Transaction t : sc.getTransactions()) {
                        System.out.println(t.toString());
                    }
                }
            }
        }

        summaryFragment = (SummaryFragment) getSupportFragmentManager().getFragment(savedInstanceState, "summaryFragment");
        transactionsFragment = (TransactionsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "transactionFragment");
        categoryOverviewFragment = (CategoryOverviewFragment) getSupportFragmentManager().getFragment(savedInstanceState, "categoryFragment");

        //summaryFragment.refreshAdapter();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArray("workingData", DataProvider.getInstance().getRootCategories());
        if (summaryFragment != null) {
            getSupportFragmentManager().putFragment(savedInstanceState, "summaryFragment", summaryFragment);
        }
        if (transactionsFragment != null) {
            getSupportFragmentManager().putFragment(savedInstanceState, "transactionFragment", transactionsFragment);
        }
        if (categoryOverviewFragment != null) {
            getSupportFragmentManager().putFragment(savedInstanceState, "categoryFragment", categoryOverviewFragment);
        }
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

    public class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = {"Summary", "Transactions", "Categories"};
        Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
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
        dataServices.getDefaultData("");
    }

    @Override
    public void onTransactionSwipeRefresh() {
        transactionsFragment.setRefreshStatus(true);
        dataServices.getDefaultData("");
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
        dataServices.getDefaultData("");
    }

    @Override
    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        System.out.println(generalCategory.toString());
    }

    @Override
    public void onSubCategoryClicked(SubCategory subCategory) {
        System.out.println(subCategory.toString());
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

    @Override
    public void onUnsuccessfulRequest(String errorMessage) {
        System.out.println(errorMessage);
    }

    @Override
    public void onUnsuccessfulDecryption() {
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        System.out.println("GENERAL ERROR");
    }

//    @Override
//    public void onSuccessfulDecryption() {
//        if (summaryFragment != null) {
//            summaryFragment.refreshAdapter();
//            summaryFragment.setRefreshStatus(false);
//        }
//        if (transactionsFragment != null) {
//            transactionsFragment.refreshAdapter();
//            transactionsFragment.setRefreshStatus(false);
//        }
//        if (categoryOverviewFragment != null) {
//            categoryOverviewFragment.refreshAdapter();
//            categoryOverviewFragment.setRefreshStatus(false);
//        }
//    }
//
//    @Override
//    public void onUnsuccessfulDecryption() {
//        transactionsFragment.setRefreshStatus(false);
//    }

//    private void loadUserData() {
//
//        final JSONObject[] dataReceiver = new JSONObject[2];
//        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
//                this, dataReceiver) {
//
//            @Override
//            protected void onSuccess() {
//                new DecryptData(MainActivity.this).execute(dataReceiver);
//            }
//
//            @Override
//            protected void onFailure(String errorMessage) {
//                System.out.println(errorMessage);
//                // TODO - exception handling
//            }
//        };
//
//        // TODO: get system date, lookup id in local db and add to transaction request.
//
//        try {
//
//            coordinator.addRequests(
//                    RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.GET,
//                            RequestBuilder.CATEGORY, null, null, getApplicationContext()),
//                    RequestBuilder.accessTokenAuth(1, coordinator, Request.Method.GET,
//                            RequestBuilder.TRANSACTION, "?transactionid=1", null, getApplicationContext()));
//
////            coordinator.addRequests(
////                    RequestBuilder.get(0, RequestBuilder.CATEGORY, null, coordinator, RestRequest.ACCESS_TOKEN, getApplicationContext()),
////                    RequestBuilder.get(1, RequestBuilder.TRANSACTION_GET, "1", coordinator, RestRequest.ACCESS_TOKEN, getApplicationContext()));
//
//            coordinator.start();
//
//        } catch (Exception e) {
//            // TODO: exception handling
//            e.printStackTrace();
//        }
//    }

}