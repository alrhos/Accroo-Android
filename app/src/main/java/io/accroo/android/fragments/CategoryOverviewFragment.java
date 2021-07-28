package io.accroo.android.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.accroo.android.R;
import io.accroo.android.adapters.CategoryOverviewAdapter;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.other.DividerItemDecoration;

public class CategoryOverviewFragment extends Fragment implements CategoryOverviewAdapter.AdapterInteractionListener {

    private CategoryOverviewAdapter categoryOverviewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentInteractionListener fragmentListener;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public CategoryOverviewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryOverviewAdapter = new CategoryOverviewAdapter(requireActivity(), this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_category_overview, container, false);
        emptyView = fragmentView.findViewById(R.id.empty_view);

        recyclerView = fragmentView.findViewById(R.id.category_overview_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(fragmentView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(categoryOverviewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fragmentListener.hideFab();
                }
                else if (dy < 0) {
                    fragmentListener.showFab();
                }
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(llm);

        swipeRefreshLayout = fragmentView.findViewById(R.id.category_overview_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> fragmentListener.onCategorySwipeRefresh());

        categoryOverviewAdapter.refreshDataSource();
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            fragmentListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdapterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public interface FragmentInteractionListener {
        void onGeneralCategoryClicked(GeneralCategory generalCategory);
        void onSubCategoryClicked(SubCategory subCategory);
        void onCategorySwipeRefresh();
        void hideFab();
        void showFab();
    }

    @Override
    public void onEmptyList() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNonEmptyList() {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGeneralCategoryClicked(GeneralCategory generalCategory) {
        fragmentListener.onGeneralCategoryClicked(generalCategory);
    }

    @Override
    public void onSubCategoryClicked(SubCategory subCategory) {
        fragmentListener.onSubCategoryClicked(subCategory);
    }

    public void refreshAdapter() {
        categoryOverviewAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }

}
