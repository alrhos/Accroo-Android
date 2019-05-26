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
import io.accroo.android.adapters.TransactionAdapter;
import io.accroo.android.model.Transaction;

public class TransactionsFragment extends Fragment implements TransactionAdapter.AdapterInteractionListener {

    private TransactionAdapter transactionAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private FragmentInteractionListener fragmentListener;

    public TransactionsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionAdapter = new TransactionAdapter(requireActivity(), this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_transactions, container, false);
        emptyView = fragmentView.findViewById(R.id.empty_view);

        recyclerView = fragmentView.findViewById(R.id.transaction_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(transactionAdapter);

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

        swipeRefreshLayout = fragmentView.findViewById(R.id.transaction_swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fragmentListener.onTransactionSwipeRefresh();
            }
        });

        transactionAdapter.refreshDataSource();

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
        void onTransactionSwipeRefresh();
        void onTransactionSelected(Transaction transaction);
        void hideFab();
        void showFab();
    }

    public void refreshAdapter() {
        transactionAdapter.refreshDataSource();
    }

    public void setRefreshStatus(boolean status) {
        swipeRefreshLayout.setRefreshing(status);
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
    public void onTransactionSelected(Transaction transaction) {
        fragmentListener.onTransactionSelected(transaction);
    }

}
