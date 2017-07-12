package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.SelectIconAdapter;

public class SelectIconActivity extends AppCompatActivity {

    private LinearLayoutManager layoutManager;
    private SelectIconAdapter iconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_icon);

        iconAdapter = new SelectIconAdapter(getApplicationContext());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.select_icon_recycler_view);


        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(iconAdapter);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

    }

}
