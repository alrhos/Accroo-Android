package com.paleskyline.accroo.adapters;

import android.content.Context;
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
import com.paleskyline.accroo.model.Summary;
import com.paleskyline.accroo.services.DataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by oscar on 27/05/17.
 */

public class SummaryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> dataSource;
    private final int SUMMARY = 0;
    private final int GENERAL_CATEGORY = 1;
    private Context context;
    private LayoutInflater inflater;
    private SummaryViewHolder summaryViewHolder;
    private AdapterInteractionListener adapterInteractionListener;
    private Date startDate, endDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    public SummaryListAdapter(Context context, Date startDate, Date endDate, AdapterInteractionListener adapterInteractionListener) {
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
        this.adapterInteractionListener = adapterInteractionListener;
        inflater = LayoutInflater.from(context);
        dataSource = new ArrayList<>();
        refreshDataSource();
    }

    public interface AdapterInteractionListener {
        void onStartDateClicked();
        void onEndDateClicked();
    }

    class SummaryViewHolder extends RecyclerView.ViewHolder {

        private TextView income, expenses, savings, startDateView, endDateView;

        public SummaryViewHolder(View view) {
            super(view);
            income = (TextView) view.findViewById(R.id.income_amount);
            expenses = (TextView) view.findViewById(R.id.expenses_amount);
            savings = (TextView) view.findViewById(R.id.savings_amount);
            startDateView = (TextView) view.findViewById(R.id.start_date);
            endDateView = (TextView) view.findViewById(R.id.end_date);
        }

    }

    class GeneralCategoryViewHolder extends RecyclerView.ViewHolder {

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
        }

    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataSource.get(position) instanceof Summary) {
            return SUMMARY;
        } else if (dataSource.get(position) instanceof GeneralCategory) {
            return GENERAL_CATEGORY;
        } else {
            return -1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {

            case SUMMARY:

                summaryViewHolder = (SummaryViewHolder) holder;
                Summary summary = (Summary) dataSource.get(position);

                summaryViewHolder.income.setText(summary.getTotal(Summary.INCOME));
                summaryViewHolder.expenses.setText(summary.getTotal(Summary.EXPENSES));
                summaryViewHolder.savings.setText(summary.getSavings());
                summaryViewHolder.startDateView.setText(dateFormat.format(startDate.getTime()));
                summaryViewHolder.endDateView.setText(dateFormat.format(endDate.getTime()));

                summaryViewHolder.startDateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapterInteractionListener.onStartDateClicked();
                    }
                });

                summaryViewHolder.endDateView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapterInteractionListener.onEndDateClicked();
                    }
                });


                break;

            case GENERAL_CATEGORY:
                final GeneralCategoryViewHolder vh2 = (GeneralCategoryViewHolder) holder;
                GeneralCategory gc = (GeneralCategory) dataSource.get(position);
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
                    tv.setText(R.string.no_sub_categories);
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

    public void refreshDataSource() {
        dataSource.clear();
        dataSource.add(new Summary(DataProvider.getRootCategories()));
        ArrayList<GeneralCategory> generalCategories = DataProvider.getGeneralCategories();
        dataSource.addAll(generalCategories);
        notifyDataSetChanged();
//        for (GeneralCategory generalCategory : generalCategories) {
//            dataSource.add(generalCategory);
//        }
    }

    public void updateStartDate(Date date) {
        startDate = date;
        summaryViewHolder.startDateView.setText(dateFormat.format(date.getTime()));
    }

    public void updateEndDate(Date date) {
        endDate = date;
        summaryViewHolder.endDateView.setText(dateFormat.format(date.getTime()));
    }

}
