package com.paleskyline.accroo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.adapters.IconAdapter;

public class SelectIconActivity extends AppCompatActivity implements IconAdapter.AdapterInteractionListener {

    private LinearLayoutManager layoutManager;
    private IconAdapter iconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_select_icon);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            iconAdapter = new IconAdapter(getApplicationContext(), this);

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.select_icon_recycler_view);


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
    public void onIconSelected(int iconID) {
        Intent data = new Intent();
        data.putExtra("iconID", iconID);
        String iconName = getResources().getResourceEntryName(iconID);
        data.putExtra("iconName", iconName);
        setResult(RESULT_OK, data);
        finish();
    }

}
