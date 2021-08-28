package io.accroo.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.opencsv.CSVWriter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.accroo.android.R;
import io.accroo.android.fragments.CategoryOverviewFragment;
import io.accroo.android.fragments.SummaryFragment;
import io.accroo.android.fragments.TransactionsFragment;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.TransactionComparator;
import io.accroo.android.other.Constants;
import io.accroo.android.other.MessageDialog;
import io.accroo.android.services.ApiService;
import io.accroo.android.services.DataProvider;

public class MainActivity extends AppCompatActivity implements SummaryFragment.FragmentInteractionListener,
        TransactionsFragment.FragmentInteractionListener, CategoryOverviewFragment.FragmentInteractionListener,
     ApiService.RequestOutcome {

    private SummaryFragment summaryFragment;
    private TransactionsFragment transactionsFragment;
    private CategoryOverviewFragment categoryOverviewFragment;
    private ApiService apiService;
    private DateTime startDate, endDate;
    private FloatingActionButton fab;
    private ProgressDialog progressDialog;
    private final int[] fabColorArray = {
        R.color.colorAccent,
        R.color.colorAccent,
        R.color.colorAccentSecondary
    };
    private static final int CREATE_FILE = 1;
    private static final String EXPORT_FILE_NAME = "Accroo data.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (!LaunchActivity.initialized) {
                relaunch();
            } else {
                setContentView(R.layout.activity_main);

                startDate = new DateTime(getIntent().getLongExtra("startDate", -1));
                endDate = new DateTime(getIntent().getLongExtra("endDate", -1));

                Toolbar toolbar = findViewById(R.id.main_toolbar);
                setSupportActionBar(toolbar);

                PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), MainActivity.this);

                final ViewPager viewPager = findViewById(R.id.main_viewpager);
                viewPager.setAdapter(pagerAdapter);

                final TabLayout tabLayout = findViewById(R.id.main_tab_layout);
                tabLayout.setupWithViewPager(viewPager);

                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        tab.setCustomView(pagerAdapter.getTabView(i));
                    }
                }

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                        animateFab(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {}

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {}
                });

                fab = findViewById(R.id.fab);

                fab.setOnClickListener(view -> {
                    int selectedTab = tabLayout.getSelectedTabPosition();
                    if (selectedTab == 0 || selectedTab == 1) {
                        startActivity(new Intent(getApplicationContext(), TransactionActivity.class));
                    } else if (selectedTab == 2) {
                        startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                    }
                });

                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.signing_out));
                progressDialog.setCancelable(false);

                apiService = new ApiService(this, getApplicationContext());
            }
    }

    protected void animateFab(final int position) {
        fab.clearAnimation();
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setBackgroundTintList(getResources().getColorStateList(fabColorArray[position],
                        getApplicationContext().getTheme()));
                ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        fab.startAnimation(shrink);
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
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            case R.id.export_data:
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/csv");
                intent.putExtra(Intent.EXTRA_TITLE, EXPORT_FILE_NAME);
                startActivityForResult(intent, CREATE_FILE);
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.sign_out:
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        apiService.invalidateSession();
                    }
                }, 1500);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    new DataExportTask(uri).execute();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.data_export_error, Toast.LENGTH_LONG).show();
            }
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
                    return summaryFragment = SummaryFragment.newInstance(startDate.getMillis(),
                            endDate.getMillis());
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
        apiService.loadDefaultData(startDate, endDate);
    }

    @Override
    public void onStartDateUpdated(DateTime date) {
        startDate = date;
        onSummarySwipeRefresh();
    }

    @Override
    public void onEndDateUpdated(DateTime date) {
        endDate = date;
        onSummarySwipeRefresh();
    }

    @Override
    public void onTransactionSwipeRefresh() {
        transactionsFragment.setRefreshStatus(true);
        apiService.loadDefaultData(startDate, endDate);
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
        apiService.loadDefaultData(startDate, endDate);
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
        if (requestType == ApiService.LOAD_DEFAULT_DATA) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (summaryFragment != null) {
                    summaryFragment.refreshAdapter();
                }
                if (transactionsFragment != null) {
                    transactionsFragment.refreshAdapter();
                }
                if (categoryOverviewFragment != null) {
                    categoryOverviewFragment.refreshAdapter();
                }
            }, 250);
        } else if (requestType == ApiService.INVALIDATE_SESSION) {
            apiService.logout();
            progressDialog.dismiss();
            relaunch();
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        hideRefreshing();
        if (requestType == ApiService.INVALIDATE_SESSION) {
            apiService.logout();
            progressDialog.dismiss();
            relaunch();
        } else if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MessageDialog.show(MainActivity.this,
                    getResources().getString(R.string.maintenance_title),
                    getResources().getString(R.string.maintenance_message));
        } else if (errorCode == ApiService.GONE) {
            MessageDialog.show(MainActivity.this,
                    getResources().getString(R.string.upgrade_required_title),
                    getResources().getString(R.string.upgrade_required_message));
        } else if (errorCode == ApiService.UNAUTHORIZED || errorCode == ApiService.NOT_FOUND ||
                errorCode == ApiService.UNPROCESSABLE_ENTITY) {
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
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError() {
        hideRefreshing();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

    class DataExportTask extends AsyncTask<Void, Void, Boolean> {

        private Uri exportFileUri;

        private DataExportTask(Uri exportFileUri) {
            this.exportFileUri = exportFileUri;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(exportFileUri, "w");
                if (parcelFileDescriptor != null) {
                    FileWriter fileWriter = new FileWriter(parcelFileDescriptor.getFileDescriptor());
                    CSVWriter csvWriter = new CSVWriter(fileWriter);
                    String[] headers = {
                            getResources().getString(R.string.type),
                            getResources().getString(R.string.general_category),
                            getResources().getString(R.string.sub_category),
                            getResources().getString(R.string.date),
                            getResources().getString(R.string.amount),
                            getResources().getString(R.string.description_csv)
                    };
                    csvWriter.writeNext(headers);
                    GeneralCategory generalCategory;
                    SubCategory subCategory;
                    ArrayList<Transaction> transactions = DataProvider.getTransactions();
                    Collections.sort(transactions, new TransactionComparator());
                    for (Transaction transaction : transactions) {
                        subCategory = (SubCategory) transaction.getParent();
                        generalCategory = (GeneralCategory) subCategory.getParent();
                        String[] row = {
                                generalCategory.getRootCategory(),
                                generalCategory.getCategoryName(),
                                subCategory.getCategoryName(),
                                transaction.getDateWithoutTime().toString(Constants.DATE_FORMAT),
                                transaction.getFormattedAmount(),
                                transaction.getDescription()
                        };
                        csvWriter.writeNext(row);
                    }
                    csvWriter.close();
                    fileWriter.close();
                    parcelFileDescriptor.close();
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(getApplicationContext(), R.string.data_exported, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.data_export_error, Toast.LENGTH_LONG).show();
            }
        }
    }

}