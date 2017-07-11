package com.paleskyline.navicash.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.SummaryListAdapter;
import com.paleskyline.navicash.other.DividerItemDecoration;

public class SummaryFragment extends Fragment {

    private SummaryListAdapter summaryListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnFragmentInteractionListener fragmentListener;
    private LinearLayoutManager layoutManager;

    public SummaryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        summaryListAdapter = new SummaryListAdapter(getActivity());
        System.out.println("SUMMARY FRAG - onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        System.out.println("ON CREATE SUMMARY VIEW");

        View fragmentView = inflater.inflate(R.layout.fragment_summary, container, false);

        RecyclerView rv = (RecyclerView) fragmentView.findViewById(R.id.summary_recycler_view);
        rv.addItemDecoration(new DividerItemDecoration(fragmentView.getContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(summaryListAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.summary_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        fragmentListener.onSummarySwipeRefresh();
                    }
                }
        );

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    public interface OnFragmentInteractionListener {
        void onSummarySwipeRefresh();
    }

    public void refreshAdapter() {
        summaryListAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }

}