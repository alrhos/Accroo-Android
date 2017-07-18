package com.paleskyline.navicash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.services.DataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;


/**
 * Created by oscar on 6/06/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private AdapterInteractionListener adapterInteractionListener;
    private ArrayList<Transaction> transactions;
    private LinkedHashMap<Date, ArrayList<Transaction>> groupedTransactions;
    private LayoutInflater inflater;
    private SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy");

    public TransactionAdapter(Context context, AdapterInteractionListener adapterInteractionListener) {

        // TODO: data source can probably be initialised from within

        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        inflater = LayoutInflater.from(context);
        transactions = new ArrayList<>();
        groupedTransactions = new LinkedHashMap<>();
        refreshDataSource();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView dateHeader;

        public TransactionViewHolder(View view) {
            super(view);
            dateHeader = (TextView) view.findViewById(R.id.date_header_value);
        }
    }

    public interface AdapterInteractionListener {
        void onTransactionSelected(Transaction transaction);
    }

    @Override
    public int getItemCount() {
        return groupedTransactions.keySet().size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TransactionViewHolder vh = (TransactionViewHolder) holder;
        Date date = new ArrayList<>(groupedTransactions.keySet()).get(position);
        String formattedDate = df.format(date);
        vh.dateHeader.setText(formattedDate);

        LinearLayout ll = (LinearLayout) vh.itemView.findViewById(R.id.transaction_list_layout);
        ll.removeAllViews();

        for (final Transaction transaction : groupedTransactions.get(date)) {

            View v = inflater.inflate(R.layout.transaction_list_details, null, false);

            ImageView iv = (ImageView) v.findViewById(R.id.transaction_icon);

            ((GeneralCategory) ((SubCategory) transaction.getParent()).getParent()).getIconFile();

            String icon = ((GeneralCategory) ((SubCategory) transaction.getParent()).getParent()).getIconFile();
            int iconId = context.getResources().getIdentifier("@drawable/" + icon, null, context.getPackageName());
            iv.setImageResource(iconId);

            TextView category = (TextView) v.findViewById(R.id.transaction_category_name);
            String subCategoryName = ((SubCategory) transaction.getParent()).getCategoryName();
            category.setText(subCategoryName);

            TextView description = (TextView) v.findViewById(R.id.transaction_category_description);
            description.setText(transaction.getDescription());

            TextView amount = (TextView) v.findViewById(R.id.transaction_category_amount);
            amount.setText(transaction.getFullyFormattedAmount());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println(((GeneralCategory) ((SubCategory) transaction.getParent()).getParent()).getIconFile());
                    adapterInteractionListener.onTransactionSelected(transaction);
                }
            });

            ll.addView(v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent, false);
        return new TransactionViewHolder(view);
    }

    public void refreshDataSource() {

        groupedTransactions.clear();
        transactions = DataProvider.getTransactions();

        for (Transaction transaction : transactions) {
            if (!groupedTransactions.containsKey(transaction.getDate())) {
                groupedTransactions.put(transaction.getDate(), null);
            }
        }

        for (Date key : groupedTransactions.keySet()) {
            ArrayList<Transaction> matches = new ArrayList<>();
            for (Transaction transaction : transactions) {
                if (key.equals(transaction.getDate())) {
                    matches.add(transaction);
                }
            }
            groupedTransactions.put(key, matches);
        }

        notifyDataSetChanged();

    }

}
