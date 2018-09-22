package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.accroo.android.R;
import io.accroo.android.adapters.GeneralCategoryAdapter;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.other.DividerItemDecoration;

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
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            generalCategoryAdapter = new GeneralCategoryAdapter(getApplicationContext(), this);

            recyclerView = findViewById(R.id.general_category_recycler_view);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(generalCategoryAdapter);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);

            emptyView = findViewById(R.id.empty_view);

            if (generalCategoryAdapter.getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
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
    public void onGeneralCategorySelected(GeneralCategory generalCategory) {
        Intent data = new Intent();
        data.putExtra("generalCategory", generalCategory);
        setResult(RESULT_OK, data);
        finish();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

}
