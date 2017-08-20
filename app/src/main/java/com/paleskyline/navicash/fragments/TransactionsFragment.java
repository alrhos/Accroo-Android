package com.paleskyline.navicash.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.adapters.TransactionAdapter;
import com.paleskyline.navicash.model.Transaction;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionsFragment extends Fragment implements TransactionAdapter.AdapterInteractionListener {

    private TransactionAdapter transactionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentInteractionListener fragmentListener;

    public TransactionsFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance(String param1, String param2) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: review data source handling - can probably be pushed to the adapter

//        dataSource = new ArrayList<>();
//        for (Transaction t : DataProvider.getInstance().getTransactions()) {
//            dataSource.add(t);
//        }
//        System.out.println("DATA SOURCE SIZE: " + dataSource.size());
        transactionAdapter = new TransactionAdapter(getActivity(), this);

        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_transactions, container, false);
        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.transaction_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(transactionAdapter);

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

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.transaction_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //swipeRefreshLayout.setRefreshing(false);
                fragmentListener.onTransactionSwipeRefresh();
            }
        });

        //swipeRefreshLayout.setEnabled(false);

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
        System.out.println("TRANSACTION FRAG - ON ACTIVITY CREATED");
        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        System.out.println("TRANSACTION FRAG - saving instance");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface FragmentInteractionListener {
        void onTransactionSwipeRefresh();
        void onTransactionSelected(Transaction transaction);
        FloatingActionButton getFab();
    }

    public void refreshAdapter() {
        transactionAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
    }


    @Override
    public void onTransactionSelected(Transaction transaction) {
        fragmentListener.onTransactionSelected(transaction);
    }

}
