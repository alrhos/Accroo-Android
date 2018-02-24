package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.fragments.CategoryOverviewFragment;
import io.accroo.android.fragments.SummaryFragment;
import io.accroo.android.fragments.TransactionsFragment;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.services.ApiService;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements SummaryFragment.FragmentInteractionListener,
        TransactionsFragment.FragmentInteractionListener, CategoryOverviewFragment.FragmentInteractionListener,
     ApiService.RequestOutcome {

    private SummaryFragment summaryFragment;
    private TransactionsFragment transactionsFragment;
    private CategoryOverviewFragment categoryOverviewFragment;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private Date startDate, endDate;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (!LaunchActivity.initialized) {
                relaunch();
            } else {
                setContentView(R.layout.activity_main);
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.signing_out));
                progressDialog.setCancelable(false);

                startDate = new Date(getIntent().getLongExtra("startDate", -1));
                endDate = new Date (getIntent().getLongExtra("endDate", -1));

                Toolbar toolbar = findViewById(R.id.main_toolbar);
                setSupportActionBar(toolbar);

                PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);

                ViewPager viewPager = findViewById(R.id.main_viewpager);
                viewPager.setAdapter(pagerAdapter);

                final TabLayout tabLayout = findViewById(R.id.main_tab_layout);
                tabLayout.setupWithViewPager(viewPager);

                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    tab.setCustomView(pagerAdapter.getTabView(i));
                }

                fab = findViewById(R.id.fab);

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
            }
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
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        relaunch();
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_email:
                startActivity(new Intent(getApplicationContext(), ChangeEmailActivity.class));
                return true;
            case R.id.change_password:
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
                return true;
            case R.id.sign_out:
                apiService.logout();
                relaunch();
                return true;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

     class PagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = {
                getResources().getString(R.string.summary),
                getResources().getString(R.string.transactions),
                getResources().getString(R.string.categories)};
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
                    return summaryFragment = SummaryFragment.newInstance(startDate.getTime(), endDate.getTime());
                case 1:
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
            TextView tv = tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }

    }

    @Override
    public void hideFab() {
        fab.hide();
    }

    @Override
    public void showFab() {
        fab.show();
    }

    @Override
    public void onSummarySwipeRefresh() {
        summaryFragment.setRefreshStatus(true);
        apiService.getDefaultData(startDate, endDate);
    }

    @Override
    public void onStartDateUpdated(Date date) {
        startDate = date;
        onSummarySwipeRefresh();
    }

    @Override
    public void onEndDateUpdated(Date date) {
        endDate = date;
        onSummarySwipeRefresh();
    }

    @Override
    public void onTransactionSwipeRefresh() {
        transactionsFragment.setRefreshStatus(true);
        apiService.getDefaultData(startDate, endDate);
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
        apiService.getDefaultData(startDate, endDate);
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
    public void onSuccess(int requestType) {
        hideRefreshing();
        if (requestType == ApiService.GET_DEFAULT_DATA) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (summaryFragment != null) {
                        summaryFragment.refreshAdapter();
                    }
                    if (transactionsFragment != null) {
                        transactionsFragment.refreshAdapter();
                    }
                    if (categoryOverviewFragment != null) {
                        categoryOverviewFragment.refreshAdapter();
                    }
                }
            }, 250);
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        hideRefreshing();
        if (errorCode == ApiService.UNAUTHORIZED) {
            Toast.makeText(getApplicationContext(), R.string.login_required, Toast.LENGTH_LONG).show();
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
                case ApiService.INVALID_DATE_RANGE:
                    message = getResources().getString(R.string.invalid_date_range);
                    break;
                default:
                    message = getResources().getString(R.string.general_error);
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {
        hideRefreshing();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}