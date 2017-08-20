package com.paleskyline.navicash.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.SummaryListAdapter;
import com.paleskyline.navicash.other.DividerItemDecoration;

import java.util.Calendar;
import java.util.Date;

public class SummaryFragment extends Fragment implements SummaryListAdapter.AdapterInteractionListener {

    private SummaryListAdapter summaryListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FragmentInteractionListener fragmentListener;
    private LinearLayoutManager layoutManager;
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private Calendar calendar;
    private Date startDate, endDate;
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
            this.startDate = new Date(getArguments().getLong(START_DATE));
            this.endDate = new Date(getArguments().getLong(END_DATE));
        }
        summaryListAdapter = new SummaryListAdapter(getActivity(), startDate, endDate, this);
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View fragmentView = inflater.inflate(R.layout.fragment_summary, container, false);

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.summary_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(fragmentView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(summaryListAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fragmentListener.getFab().hide();
                }
                else if (dy < 0) {
                    fragmentListener.getFab().show();
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.summary_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fragmentListener.onSummarySwipeRefresh();
                    }
                }
        );

        startDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDate = calendar.getTime();
                summaryListAdapter.updateStartDate(startDate);
                fragmentListener.onStartDateUpdated(startDate);
            }
        };

        endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDate = calendar.getTime();
                summaryListAdapter.updateEndDate(endDate);
                fragmentListener.onEndDateUpdated(endDate);
            }
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

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        System.out.println("SUMMARY FRAG - ON ACTIVITY CREATED");
//        if (savedInstanceState != null) {
//            layoutManagerState = savedInstanceState.getParcelable("layoutManagerState");
//            //layoutManager.onRestoreInstanceState(layoutManagerState);
//            System.out.println("SUMMARY FRAG - there's some saved instance state");
//
//        }
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        layoutManagerState = layoutManager.onSaveInstanceState();
//        savedInstanceState.putParcelable("layoutManagerState", layoutManager.onSaveInstanceState());
//        System.out.println("SUMMARY FRAG - saving instance");
//    }

    public interface FragmentInteractionListener {
        void onSummarySwipeRefresh();
        void onStartDateUpdated(Date date);
        void onEndDateUpdated(Date date);
        FloatingActionButton getFab();
    }

    public void refreshAdapter() {
        summaryListAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }

    @Override
    public void onStartDateClicked() {
        calendar.setTime(startDate);
        new DatePickerDialog(getActivity(), startDatePicker, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onEndDateClicked() {
        calendar.setTime(endDate);
        new DatePickerDialog(getActivity(), endDatePicker, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onGeneralCategoryClicked() {
        Transition transition = new ChangeBounds();
        transition.setDuration(200);
        TransitionManager.beginDelayedTransition(recyclerView, transition);
    }

}