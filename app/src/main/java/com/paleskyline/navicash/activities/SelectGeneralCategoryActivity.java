package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.GeneralCategoryAdapter;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.other.DividerItemDecoration;

public class SelectGeneralCategoryActivity extends AppCompatActivity implements GeneralCategoryAdapter.AdapterInteractionListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private GeneralCategoryAdapter generalCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_select_general_category);
            generalCategoryAdapter = new GeneralCategoryAdapter(getApplicationContext(), this);
            recyclerView = (RecyclerView) findViewById(R.id.general_category_recycler_view);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(generalCategoryAdapter);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
        }


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

    }

    @Override
    public void onGeneralCategorySelected(GeneralCategory generalCategory) {
        Intent data = new Intent();
        data.putExtra("generalCategory", generalCategory);
        setResult(RESULT_OK, data);
        finish();
    }

}
