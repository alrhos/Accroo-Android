package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.accroo.android.R;
import io.accroo.android.adapters.IconAdapter;

public class SelectIconActivity extends AppCompatActivity implements IconAdapter.AdapterInteractionListener {

    private LinearLayoutManager layoutManager;
    private IconAdapter iconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_select_icon);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            iconAdapter = new IconAdapter(getApplicationContext(), this);

            RecyclerView recyclerView = findViewById(R.id.select_icon_recycler_view);
            recyclerView.setHasFixedSize(false);
            recyclerView.setAdapter(iconAdapter);

            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onIconSelected(int iconID) {
        Intent data = new Intent();
        data.putExtra("iconID", iconID);
        String iconName = getResources().getResourceEntryName(iconID);
        data.putExtra("iconName", iconName);
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
