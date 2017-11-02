package com.paleskyline.accroo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.adapters.GeneralCategoryAdapter;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.other.DividerItemDecoration;

public class SelectGeneralCategoryActivity extends AppCompatActivity implements GeneralCategoryAdapter.AdapterInteractionListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private LinearLayoutManager layoutManager;
    private GeneralCategoryAdapter generalCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_select_general_category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            generalCategoryAdapter = new GeneralCategoryAdapter(getApplicationContext(), this);

            recyclerView = (RecyclerView) findViewById(R.id.general_category_recycler_view);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(generalCategoryAdapter);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);

            emptyView = (TextView) findViewById(R.id.empty_view);

            if (generalCategoryAdapter.getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onGeneralCategorySelected(GeneralCategory generalCategory) {
        Intent data = new Intent();
        data.putExtra("generalCategory", generalCategory);
        setResult(RESULT_OK, data);
        finish();
    }

}
