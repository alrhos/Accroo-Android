package com.paleskyline.accroo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.model.SubCategory;
import com.paleskyline.accroo.model.Transaction;
import com.paleskyline.accroo.services.DataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;


/**
 * Created by oscar on 6/06/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private AdapterInteractionListener adapterInteractionListener;
    private ArrayList<Transaction> transactions;
    private LinkedHashMap<Date, ArrayList<Transaction>> groupedTransactions;
    private LayoutInflater inflater;
    // TODO: change this to make it locale specific
    //private SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy");
    private SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.getDefault());

    public TransactionAdapter(Context context, AdapterInteractionListener adapterInteractionListener) {
        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        inflater = LayoutInflater.from(context);
        transactions = new ArrayList<>();
        groupedTransactions = new LinkedHashMap<>();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView dateHeader;

        public TransactionViewHolder(View view) {
            super(view);
            dateHeader = view.findViewById(R.id.date_header_value);
        }
    }

    public interface AdapterInteractionListener {
        void onEmptyList();
        void onNonEmptyList();
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

        LinearLayout ll = vh.itemView.findViewById(R.id.transaction_list_layout);
        ll.removeAllViews();

        for (final Transaction transaction : groupedTransactions.get(date)) {

            View v = inflater.inflate(R.layout.transaction_list_details, null, false);
            ImageView iv = v.findViewById(R.id.transaction_icon);

            GeneralCategory generalCategory = ((GeneralCategory) ((SubCategory) transaction.getParent()).getParent());

            int iconId = context.getResources().getIdentifier("@drawable/" + generalCategory.getIconFile(), null, context.getPackageName());
            iv.setImageResource(iconId);

            TextView category = v.findViewById(R.id.transaction_category_name);
            String subCategoryName = ((SubCategory) transaction.getParent()).getCategoryName();
            category.setText(subCategoryName);

            TextView description = v.findViewById(R.id.transaction_category_description);
            description.setText(transaction.getDescription());

            TextView amount = v.findViewById(R.id.transaction_category_amount);
            amount.setText(transaction.getFullyFormattedAmount());

            if (generalCategory.getRootCategory().equals("Income")) {
                amount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            } else {
                amount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

        if (transactions.size() == 0) {
            adapterInteractionListener.onEmptyList();
        } else {
            adapterInteractionListener.onNonEmptyList();
            for (Transaction transaction : transactions) {
                if (!groupedTransactions.containsKey(transaction.getAdjustedDate())) {
                    groupedTransactions.put(transaction.getAdjustedDate(), null);
                }
            }

            for (Date key : groupedTransactions.keySet()) {
                ArrayList<Transaction> matches = new ArrayList<>();
                for (Transaction transaction : transactions) {
                    if (key.equals(transaction.getAdjustedDate())) {
                        matches.add(transaction);
                    }
                }
                groupedTransactions.put(key, matches);
            }

            notifyDataSetChanged();
        }
    }

}
