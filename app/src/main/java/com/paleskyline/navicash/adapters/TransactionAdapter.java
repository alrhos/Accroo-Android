package com.paleskyline.navicash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paleskyline.navicash.R;

import java.util.ArrayList;


/**
 * Created by oscar on 6/06/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Object> items;
    private LayoutInflater inflater;

    public TransactionAdapter(Context context, ArrayList<Object> items) {
        this.items = items;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView dateHeader;

        public TransactionViewHolder(View view) {
            super(view);
            dateHeader = (TextView) view.findViewById(R.id.date_header_value);
        }

    }

    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent, false);
        return new TransactionViewHolder(view);
    }
}
