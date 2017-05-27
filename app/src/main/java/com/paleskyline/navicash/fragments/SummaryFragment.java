package com.paleskyline.navicash.fragments;

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
import com.paleskyline.navicash.other.SimpleDividerItemDecoration;

public class SummaryFragment extends Fragment implements SummaryListAdapter.ClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.addItemDecoration(new SimpleDividerItemDecoration(rootView.getContext()));
        rv.setHasFixedSize(true);
        SummaryListAdapter summaryListAdapter = new SummaryListAdapter(getContext(), new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"});
        summaryListAdapter.setClickListener(this);

        rv.setAdapter(summaryListAdapter);
        //MyAdapter adapter = new MyAdapter(new String[]{"test one", "test two", "test three", "test four", "test five" , "test six" , "test seven", "test eight" , "test nine"});
        //rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        System.out.println("REFRESHING!!");
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        return rootView;
    }

    @Override
    public void itemClicked(View view, int position) {
        System.out.println("CLICKED ITEM " + position);
    }

}