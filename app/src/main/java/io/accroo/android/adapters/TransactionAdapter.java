package io.accroo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.accroo.android.R;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.RootCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.other.Constants;
import io.accroo.android.services.DataProvider;

/**
 * Created by oscar on 6/06/17.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private AdapterInteractionListener adapterInteractionListener;
    private ArrayList<Transaction> transactions;

    public TransactionAdapter(Context context, AdapterInteractionListener adapterInteractionListener) {
        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        transactions = new ArrayList<>();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView dateHeader, subCategory, amount, description;

        public TransactionViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            dateHeader = view.findViewById(R.id.date_header);
            subCategory = view.findViewById(R.id.sub_category);
            amount = view.findViewById(R.id.amount);
            description = view.findViewById(R.id.description);
        }
    }

    public interface AdapterInteractionListener {
        void onEmptyList();
        void onNonEmptyList();
        void onTransactionSelected(Transaction transaction);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        final Transaction transaction = transactions.get(position);
        if (position > 0 && transactions.get(position - 1).getDateWithoutTime().isEqual(transaction.getDateWithoutTime())) {
            holder.dateHeader.setVisibility(View.GONE);
        } else {
            String formattedDate = transaction.getDateWithoutTime().toString(Constants.DATE_FORMAT);
            holder.dateHeader.setText(formattedDate);
            holder.dateHeader.setVisibility(View.VISIBLE);
        }

        GeneralCategory generalCategory = ((GeneralCategory) ((SubCategory) transaction.getParent()).getParent());
        int iconId = context.getResources().getIdentifier("@drawable/" + generalCategory.getIconFile(), null, context.getPackageName());
        holder.icon.setImageResource(iconId);

        String subCategoryName = ((SubCategory) transaction.getParent()).getCategoryName();
        holder.subCategory.setText(subCategoryName);

        holder.description.setText(transaction.getDescription());
        holder.amount.setText(transaction.getFullyFormattedAmount());

        if (generalCategory.getRootCategory().equals(RootCategory.INCOME)) {
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if (generalCategory.getRootCategory().equals(RootCategory.EXPENSE)) {
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        holder.itemView.setOnClickListener(view -> adapterInteractionListener.onTransactionSelected(transaction));
    }

    @Override
    @NonNull
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item, parent, false);
        return new TransactionViewHolder(view);
    }

    public void refreshDataSource() {
        transactions = DataProvider.getTransactions();
        if (transactions.size() == 0) {
            adapterInteractionListener.onEmptyList();
        } else {
            adapterInteractionListener.onNonEmptyList();
        }
        notifyDataSetChanged();
    }

}
