package io.accroo.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.accroo.android.R;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.RootCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Summary;
import io.accroo.android.services.DataProvider;

import java.util.ArrayList;

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
    private DateTime startDate, endDate;
    private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd MMM yyyy");

    public SummaryListAdapter(Context context, DateTime startDate, DateTime endDate,
                              AdapterInteractionListener adapterInteractionListener) {
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
            income = view.findViewById(R.id.income_amount);
            expenses = view.findViewById(R.id.expenses_amount);
            savings = view.findViewById(R.id.savings_amount);
            startDateView = view.findViewById(R.id.start_date);
            endDateView = view.findViewById(R.id.end_date);
        }

    }

    class GeneralCategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView category, amount;
        private LinearLayout details;
        private ImageView icon;
        private boolean expanded = false;

        public GeneralCategoryViewHolder(View view) {
            super(view);
            category = view.findViewById(R.id.category_name);
            amount = view.findViewById(R.id.category_amount);
            icon = view.findViewById(R.id.category_icon);
            details = view.findViewById(R.id.general_category_list_details);
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

                summaryViewHolder.income.setText(summary.getTotal(RootCategory.INCOME));
                summaryViewHolder.expenses.setText(summary.getTotal(RootCategory.EXPENSE));
                summaryViewHolder.savings.setText(summary.getSavings());
                summaryViewHolder.startDateView.setText(startDate.toString(dateFormat));
                summaryViewHolder.endDateView.setText(endDate.toString(dateFormat));

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

                LinearLayout ll = vh2.itemView.findViewById(R.id.general_category_list_details);
                ll.removeAllViews();

                if (!gc.getSubCategories().isEmpty()) {
                    for (SubCategory sc : gc.getSubCategories()) {
                        View v = inflater.inflate(R.layout.sub_category_summary_item, null, false);
                        TextView tv1 = v.findViewById(R.id.sub_category_name);
                        tv1.setText(sc.getCategoryName());
                        TextView tv2 = v.findViewById(R.id.sub_category_amount);
                        tv2.setText(sc.getFormattedTransactionTotal());
                        ll.addView(v);
                    }
                } else {
                    View a = inflater.inflate(R.layout.sub_category_summary_item, null, false);
                    TextView tv = a.findViewById(R.id.sub_category_name);
                    tv.setText(R.string.no_sub_categories);
                    ll.addView(a);
                }

                vh2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vh2.expanded) {
                            vh2.details.setVisibility(View.GONE);
                            vh2.category.setTextAppearance(R.style.Heading);
                            vh2.amount.setTextAppearance(R.style.Heading);
                        } else {
                            vh2.details.setVisibility(View.VISIBLE);
                            vh2.category.setTextAppearance(R.style.BoldSubHeading);
                            vh2.amount.setTextAppearance(R.style.BoldSubHeading);
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
    }

    public void updateStartDate(DateTime date) {
        startDate = date;
        summaryViewHolder.startDateView.setText(date.toString(dateFormat));
    }

    public void updateEndDate(DateTime date) {
        endDate = date;
        summaryViewHolder.endDateView.setText(date.toString(dateFormat));
    }

}
