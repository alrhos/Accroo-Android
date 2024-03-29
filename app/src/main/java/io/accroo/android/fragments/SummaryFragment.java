package io.accroo.android.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.joda.time.DateTime;

import io.accroo.android.R;
import io.accroo.android.adapters.SummaryListAdapter;

public class SummaryFragment extends Fragment implements SummaryListAdapter.AdapterInteractionListener {

    private SummaryListAdapter summaryListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FragmentInteractionListener fragmentListener;
    private LinearLayoutManager layoutManager;
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private DateTime startDate, endDate;
    private DatePickerDialog.OnDateSetListener startDatePicker;
    private DatePickerDialog.OnDateSetListener endDatePicker;

    public SummaryFragment() {}

    public static SummaryFragment newInstance(long startDate, long endDate) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putLong(START_DATE, startDate);
        args.putLong(END_DATE, endDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.startDate = new DateTime(getArguments().getLong(START_DATE));
            this.endDate = new DateTime(getArguments().getLong(END_DATE));
        }
        summaryListAdapter = new SummaryListAdapter(requireActivity(), startDate, endDate, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_summary, container, false);

        recyclerView = fragmentView.findViewById(R.id.summary_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(summaryListAdapter);
        layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);

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

        swipeRefreshLayout = fragmentView.findViewById(R.id.summary_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(() -> fragmentListener.onSummarySwipeRefresh());

        startDatePicker = (view, year, monthOfYear, dayOfMonth) -> {
            startDate = new DateTime(year, monthOfYear + 1, dayOfMonth,
                    0, 0);
            summaryListAdapter.updateStartDate(startDate);
            fragmentListener.onStartDateUpdated(startDate);
        };

        endDatePicker = (view, year, monthOfYear, dayOfMonth) -> {
            endDate = new DateTime(year, monthOfYear + 1, dayOfMonth,
                    23, 59, 59, 999);
            summaryListAdapter.updateEndDate(endDate);
            fragmentListener.onEndDateUpdated(endDate);
        };

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

    public interface FragmentInteractionListener {
        void onSummarySwipeRefresh();
        void onStartDateUpdated(DateTime date);
        void onEndDateUpdated(DateTime date);
        void hideFab();
        void showFab();
    }

    public void refreshAdapter() {
        summaryListAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }

    @Override
    public void onStartDateClicked() {
        new DatePickerDialog(requireActivity(), startDatePicker, startDate.getYear(),
                startDate.getMonthOfYear() - 1, startDate.getDayOfMonth()).show();
    }

    @Override
    public void onEndDateClicked() {
        new DatePickerDialog(requireActivity(), endDatePicker, endDate.getYear(),
                endDate.getMonthOfYear() - 1, endDate.getDayOfMonth()).show();
    }

}