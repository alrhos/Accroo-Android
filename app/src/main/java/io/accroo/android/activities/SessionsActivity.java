package io.accroo.android.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.accroo.android.R;
import io.accroo.android.adapters.SessionAdapter;
import io.accroo.android.model.SessionData;
import io.accroo.android.other.MessageDialog;
import io.accroo.android.services.ApiService;
import io.accroo.android.services.CredentialService;


public class SessionsActivity extends AppCompatActivity implements SessionAdapter.AdapterInteractionListener, ApiService.RequestOutcome {

    private SessionAdapter sessionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private String currentSessionId;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

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

        swipeRefreshLayout = findViewById(R.id.session_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(this::refreshSessions);

        try {
            currentSessionId = CredentialService.getInstance(getApplicationContext()).getEntry(CredentialService.SESSION_ID_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sessionAdapter = new SessionAdapter(getApplicationContext(), this, currentSessionId);
        RecyclerView recyclerView = findViewById(R.id.session_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(sessionAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        builder = new AlertDialog.Builder(SessionsActivity.this);

        progressDialog = new ProgressDialog(SessionsActivity.this);
        progressDialog.setCancelable(false);

        apiService = new ApiService(this, getApplicationContext());
        refreshSessions();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSessionSelected(@NonNull SessionData session) {
        if (session.getId().toString().equals(this.currentSessionId)) {
            builder.setMessage(R.string.sign_out_of_device)
                    .setPositiveButton(R.string.sign_out, (dialog, which) -> {
                        progressDialog.setMessage(getResources().getString(R.string.signing_out));
                        progressDialog.show();
                        apiService.invalidateCurrentSession();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {}).create().show();
        } else {
            builder.setMessage(R.string.revoke_session)
                    .setPositiveButton(R.string.revoke, (dialog, which) -> {
                        progressDialog.setMessage(getResources().getString(R.string.revoking_session));
                        progressDialog.show();
                        apiService.invalidateSession(session);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {}).create().show();
        }
    }

    private void refreshSessions() {
        swipeRefreshLayout.setRefreshing(true);
        apiService.getSessions();
    }

    @Override
    public void onSuccess(int requestType) {
        swipeRefreshLayout.setRefreshing(false);
        progressDialog.dismiss();
        if (requestType == ApiService.GET_SESSIONS) {
            sessionAdapter.refreshDataSource();
        } else if (requestType == ApiService.INVALIDATE_CURRENT_SESSION) {
            apiService.logout();
            relaunch();
        } else if (requestType == ApiService.INVALIDATE_SESSION) {
            sessionAdapter.refreshDataSource();
            Toast.makeText(getApplicationContext(), getString(R.string.session_revoked), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        swipeRefreshLayout.setRefreshing(false);
        progressDialog.dismiss();
        if (requestType == ApiService.INVALIDATE_CURRENT_SESSION) {
            apiService.logout();
            relaunch();
        } else if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MessageDialog.show(SessionsActivity.this,
                    getResources().getString(R.string.maintenance_title),
                    getResources().getString(R.string.maintenance_message));
        } else if (errorCode == ApiService.GONE) {
            MessageDialog.show(SessionsActivity.this,
                    getResources().getString(R.string.upgrade_required_title),
                    getResources().getString(R.string.upgrade_required_message));
        } else if (errorCode == ApiService.UNAUTHORIZED || errorCode == ApiService.UNPROCESSABLE_ENTITY) {
            apiService.logout();
            relaunch();
        } else if (requestType == ApiService.INVALIDATE_SESSION && errorCode == ApiService.NOT_FOUND) {
            Toast.makeText(getApplicationContext(), getString(R.string.session_revoked), Toast.LENGTH_SHORT).show();
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
        swipeRefreshLayout.setRefreshing(false);
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}