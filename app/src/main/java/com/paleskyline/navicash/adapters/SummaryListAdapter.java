package com.paleskyline.navicash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paleskyline.navicash.R;

/**
 * Created by oscar on 27/05/17.
 */

public class SummaryListAdapter extends RecyclerView.Adapter<SummaryListAdapter.ViewHolder> {

    private String[] mDataset;

    private Context context;
    private LayoutInflater inflater;
    private SummaryListAdapter.ClickListener clickListener;

    public SummaryListAdapter(Context context, String[] mDataset) {
        this.mDataset = mDataset;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView category, amount;
        private ImageView icon;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            category = (TextView) view.findViewById(R.id.category_name);
            amount = (TextView) view.findViewById(R.id.category_amount);
            icon = (ImageView) view.findViewById(R.id.category_icon);
        }

        @Override
        public void onClick(View view) {
            clickListener.itemClicked(view, getAdapterPosition());
        }
    }

    public interface ClickListener {
        void itemClicked(View v, int position);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    @Override
    public void onBindViewHolder(SummaryListAdapter.ViewHolder holder, int position) {
        /*
        if (position == 0) {
            holder.category.setText("Wages");
            holder.amount.setText("$3498.11");
            holder.icon.setImageResource(R.drawable.wages);
        } else if (position == 1) {
            holder.category.setText("Transport");
            holder.amount.setText("$82");
            holder.icon.setImageResource(R.drawable.transport);
        } else if (position == 2) {
            holder.category.setText("Pets");
            holder.amount.setText("$100");
            holder.icon.setImageResource(R.drawable.pets);
        } else if (position == 3) {
            holder.category.setText("Food and Drink");
            holder.amount.setText("$489.82");
            holder.icon.setImageResource(R.drawable.foodanddrink);
        } else if (position == 4) {
            holder.category.setText("Health");
            holder.amount.setText("$120");
            holder.icon.setImageResource(R.drawable.health);
        } else if (position == 5) {
            holder.category.setText("Miscellaneous Income");
            holder.amount.setText("$550");
            holder.icon.setImageResource(R.drawable.miscellaneousincome);
        } else if (position == 6) {
            holder.category.setText("Sport");
            holder.amount.setText("$72.50");
            holder.icon.setImageResource(R.drawable.sport);
        } else if (position == 7) {
            holder.category.setText("Investments");
            holder.amount.setText("$193");
            holder.icon.setImageResource(R.drawable.investments);
        } else if (position == 8) {
            holder.category.setText("Holidays");
            holder.amount.setText("$800");
            holder.icon.setImageResource(R.drawable.holidays);
        } else if (position == 9) {
            holder.category.setText("Entertainment");
            holder.amount.setText("$62.80");
            holder.icon.setImageResource(R.drawable.entertainment);
        } else if (position == 10) {
            holder.category.setText("Education");
            holder.amount.setText("$220");
            holder.icon.setImageResource(R.drawable.education);
        } else if (position == 11) {
            holder.category.setText("Miscellaneous Expenses");
            holder.amount.setText("$40");
            holder.icon.setImageResource(R.drawable.miscellaneousexpenses);
        } else if (position == 12) {
            holder.category.setText("Accomodation");
            holder.amount.setText("$1720");
            holder.icon.setImageResource(R.drawable.accomodation);
        }
        */
    }

    @Override
    public SummaryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_category_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void setClickListener(SummaryListAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
