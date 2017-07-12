package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.IconAdapter;

public class SelectIconActivity extends AppCompatActivity implements IconAdapter.AdapterInteractionListener {

    private LinearLayoutManager layoutManager;
    private IconAdapter iconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_icon);

        iconAdapter = new IconAdapter(getApplicationContext(), this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.select_icon_recycler_view);


        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(iconAdapter);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

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
