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
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.Summary;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;
import com.paleskyline.navicash.other.DividerItemDecoration;
import com.paleskyline.navicash.services.DataProvider;

import org.json.JSONObject;

import java.util.ArrayList;

public class SummaryFragment extends Fragment implements SummaryListAdapter.ClickListener {

    private ArrayList<Object> dataSource;
    private SummaryListAdapter summaryListAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public SummaryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new ArrayList<>();
        dataSource.add(new Summary());
        for (GeneralCategory gc : DataProvider.getInstance().getGeneralCategories()) {
            dataSource.add(gc);
        }
        summaryListAdapter = new SummaryListAdapter(getActivity(), dataSource);
        summaryListAdapter.setClickListener(this);
        System.out.println("Fragment - onCreated");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_summary, container, false);

        RecyclerView rv = (RecyclerView) fragmentView.findViewById(R.id.summary_recycler_view);
        rv.addItemDecoration(new DividerItemDecoration(fragmentView.getContext()));
        rv.setHasFixedSize(true);
        rv.setAdapter(summaryListAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.summary_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        System.out.println("REFRESHING!!");
                     //   insertRandomTransactions();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        System.out.println("Fragment - onCreateView");

        return fragmentView;
    }

    @Override
    public void itemClicked(View view, int position) {
        System.out.println("CLICKED ITEM " + position);

    }

    private void insertRandomTransactions() {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getContext(), this, dataReceiver) {
            @Override
            protected void onSuccess() {
                System.out.println("TRANSACTIONS INSERTED");
            }

            @Override
            protected void onFailure(String errorMessage) {
                System.out.println(errorMessage);
            }
        };

        Transaction t = new Transaction(213, "01/02/2017", 9.70, "");
        Transaction t2 = new Transaction(215, "01/03/2017", 20.00, "");
        Transaction t3 = new Transaction(215, "07/03/2017", 19.50, "");
        Transaction t4 = new Transaction(222, "15/02/2017", 100, "Test description");
        Transaction t5 = new Transaction(229, "28/02/2017", 77.25, "");
        Transaction t6 = new Transaction(236, "02/04/2017", 82.10, "");
        Transaction t7 = new Transaction(233, "01/02/2017", 15.60, "");
        Transaction t8 = new Transaction(219, "17/03/2017", 3.70, "");
        Transaction t9 = new Transaction(220, "03/06/2017", 30.00, "");
        Transaction t10 = new Transaction(237, "07/07/2017", 15, "Second");

        try {
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.put(t.encrypt());
//            jsonArray.put(t2.encrypt());
//            jsonArray.put(t3.encrypt());
//            jsonArray.put(t4.encrypt());
//            jsonArray.put(t5.encrypt());
//            jsonArray.put(t6.encrypt());
//            jsonArray.put(t7.encrypt());
//            jsonArray.put(t8.encrypt());
//            jsonArray.put(t9.encrypt());
//            jsonArray.put(t10.encrypt());

            JSONObject jsonObject = t10.encrypt();

//            System.out.println(t2.encrypt().toString());
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("transactions", jsonArray);

            coordinator.addRequests(RestMethods.post(0, RestMethods.TRANSACTION,
                    coordinator, jsonObject, RestRequest.TOKEN, getContext()));

            coordinator.start();

        } catch (Exception e) {
            // TODO: error handling
            e.printStackTrace();
        }

    }

//    private void loadData() {
//        final JSONObject[] dataReceiver = new JSONObject[3];
//        RequestCoordinator coordinator = new RequestCoordinator(getContext(), this, dataReceiver) {
//
//            @Override
//            protected void onSuccess() {
//                System.out.println("LOAD DATA SUCCESS");
//                System.out.println(dataReceiver[0].toString());
//                System.out.println(dataReceiver[1].toString());
//                System.out.println(dataReceiver[2].toString());
//                //swipeRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            protected void onFailure(JSONObject json) {
//                System.out.println("ERROR");
//                System.out.println(json.toString());
//                //swipeRefreshLayout.setRefreshing(false);
//            }
//        };
//
//        //swipeRefreshLayout.setRefreshing(true);
//
//        coordinator.addRequests(
//                RestMethods.get(0, RestMethods.GENERAL_CATEGORY, null, coordinator, RestRequest.TOKEN),
//                RestMethods.get(1, RestMethods.SUB_CATEGORY, null, coordinator, RestRequest.TOKEN),
//                RestMethods.get(2, RestMethods.TRANSACTION_PARAM, "1", coordinator, RestRequest.TOKEN));
//
//        coordinator.start();
//    }

}