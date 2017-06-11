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
import com.paleskyline.navicash.model.Summary;

import java.util.ArrayList;

/**
 * Created by oscar on 27/05/17.
 */

public class SummaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> items;

    private final int SUMMARY = 0;
    private final int GENERAL_CATEGORY = 1;

    private Context context;
    private LayoutInflater inflater;
    private SummaryListAdapter.ClickListener clickListener;

    public SummaryListAdapter(Context context, ArrayList<Object> items) {

        // TODO: data source can probably be initialised from within

        this.items = items;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    class SummaryViewHolder extends RecyclerView.ViewHolder {

        private TextView income, expenses, savings;

        public SummaryViewHolder(View view) {
            super(view);
            income = (TextView) view.findViewById(R.id.income_amount);
            expenses = (TextView) view.findViewById(R.id.expenses_amount);
            savings = (TextView) view.findViewById(R.id.savings_amount);
        }

    }

    class GeneralCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView category, amount;
        private LinearLayout details;
        private ImageView icon;

        private boolean expanded = false;

        public GeneralCategoryViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category_name);
            amount = (TextView) view.findViewById(R.id.category_amount);
            icon = (ImageView) view.findViewById(R.id.category_icon);
            details = (LinearLayout) view.findViewById(R.id.general_category_list_details);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("CLICKY!");
            clickListener.itemClicked(view, getAdapterPosition());
        }
    }

    public interface ClickListener {
        void itemClicked(View v, int position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Summary) {
            return SUMMARY;
        } else if (items.get(position) instanceof GeneralCategory) {
            return GENERAL_CATEGORY;
        } else {
            return -1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {

            case SUMMARY:
                SummaryViewHolder vh1 = (SummaryViewHolder) holder;
                Summary summary = (Summary) items.get(position);
                vh1.income.setText(summary.getIncome());
                vh1.expenses.setText(summary.getExpenses());
                vh1.savings.setText(summary.getSavings());
                break;

            case GENERAL_CATEGORY:
                final GeneralCategoryViewHolder vh2 = (GeneralCategoryViewHolder) holder;
                GeneralCategory gc = (GeneralCategory) items.get(position);
                vh2.category.setText(gc.getCategoryName());
                vh2.amount.setText(gc.getFormattedTransactionTotal());
                int iconId = context.getResources().getIdentifier("@drawable/" + gc.getIconFile(), null, context.getPackageName());
                vh2.icon.setImageResource(iconId);

                LinearLayout ll = (LinearLayout) vh2.itemView.findViewById(R.id.general_category_list_details);
                ll.removeAllViews();

                if (!gc.getSubCategories().isEmpty()) {
                    for (SubCategory sc : gc.getSubCategories()) {
                        View v = inflater.inflate(R.layout.sub_category_summary_item, null, false);
                        TextView tv1 = (TextView) v.findViewById(R.id.sub_category_name);
                        tv1.setText(sc.getCategoryName());
                        TextView tv2 = (TextView) v.findViewById(R.id.sub_category_amount);
                        tv2.setText(sc.getFormattedTransactionTotal());
                        ll.addView(v);
                    }
                } else {
                    View a = inflater.inflate(R.layout.sub_category_summary_item, null, false);
                    TextView tv = (TextView) a.findViewById(R.id.sub_category_name);
                    tv.setText("No sub categories exist");
                    ll.addView(a);
                }

                vh2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vh2.expanded) {
                            vh2.details.setVisibility(View.GONE);
                        } else {
                            vh2.details.setVisibility(View.VISIBLE);
                        }
                        vh2.expanded = !vh2.expanded;
                    }
                });

                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder vh = null;
        inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SUMMARY:
                View v1 = inflater.inflate(R.layout.summary_card, viewGroup, false);
                vh = new SummaryViewHolder(v1);
                break;
            case GENERAL_CATEGORY:
                View v2 = inflater.inflate(R.layout.general_category_list_item, viewGroup, false);
                vh = new GeneralCategoryViewHolder(v2);
                break;
        }

        return vh;
    }

    public void setClickListener(SummaryListAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
