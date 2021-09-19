package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.accroo.android.R;
import io.accroo.android.adapters.SessionAdapter;
import io.accroo.android.services.ApiService;


public class SessionsActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private SessionAdapter sessionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_sessions);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        sessionAdapter = new SessionAdapter();
        recyclerView = findViewById(R.id.session_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(sessionAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        swipeRefreshLayout = findViewById(R.id.session_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        apiService = new ApiService(this, getApplicationContext());

        swipeRefreshLayout.setRefreshing(true);
        apiService.getSessions();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSuccess(int requestType) {
        swipeRefreshLayout.setRefreshing(false);
        sessionAdapter.refreshDataSource();
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}