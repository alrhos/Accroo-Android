package com.paleskyline.navicash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.services.DataProvider;

import java.util.ArrayList;

/**
 * Created by oscar on 22/07/17.
 */

public class GeneralCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GeneralCategory> dataSource;
    private Context context;
    private AdapterInteractionListener adapterInteractionListener;

    public GeneralCategoryAdapter(Context context, AdapterInteractionListener adapterInteractionListener) {
        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        dataSource = new ArrayList<>();
        refreshDataSource();
    }

    class GeneralCategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView category;
        private ImageView icon;

        public GeneralCategoryViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.category_name);
            icon = (ImageView) view.findViewById(R.id.category_icon);
        }

    }

    public interface AdapterInteractionListener {
        void onGeneralCategorySelected(GeneralCategory generalCategory);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GeneralCategoryViewHolder vh = (GeneralCategoryViewHolder) holder;
        final GeneralCategory generalCategory = dataSource.get(position);
        int iconId = context.getResources().getIdentifier("@drawable/" + generalCategory.getIconFile(), null, context.getPackageName());
        vh.icon.setImageResource(iconId);
        vh.category.setText(generalCategory.getCategoryName());
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterInteractionListener.onGeneralCategorySelected(generalCategory);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_category_list_item, parent, false);
        return new GeneralCategoryViewHolder(view);
    }

    public void refreshDataSource() {

        dataSource.clear();
        ArrayList<GeneralCategory> generalCategories = DataProvider.getGeneralCategories();

        for (GeneralCategory generalCategory : generalCategories) {
            dataSource.add(generalCategory);
        }

        notifyDataSetChanged();

    }

}
