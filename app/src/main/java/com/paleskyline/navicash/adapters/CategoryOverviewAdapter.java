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
import com.paleskyline.navicash.services.DataProvider;

import java.util.ArrayList;

import static com.paleskyline.navicash.services.DataProvider.getGeneralCategories;

/**
 * Created by oscar on 11/06/17.
 */

public class CategoryOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private AdapterInteractionListener adapterInteractionListener;
    private ArrayList<GeneralCategory> generalCategories;
    private LayoutInflater inflater;

    public CategoryOverviewAdapter(Context context, AdapterInteractionListener adapterInteractionListener) {
        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        this.generalCategories = getGeneralCategories();
        inflater = LayoutInflater.from(context);
    }

    public interface AdapterInteractionListener {
        void onGeneralCategoryClicked(GeneralCategory generalCategory);
        void onSubCategoryClicked(SubCategory subCategory);
    }

    class CategoryOverviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView generalCategory;

        public CategoryOverviewViewHolder(View view) {
            super(view);
            categoryIcon = (ImageView) view.findViewById(R.id.category_overview_icon);
            generalCategory = (TextView) view.findViewById(R.id.category_overview_general_category);
        }
    }

    @Override
    public int getItemCount() {
        return generalCategories.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CategoryOverviewViewHolder vh = (CategoryOverviewViewHolder) holder;
        final GeneralCategory gc = generalCategories.get(position);
        int iconId = context.getResources().getIdentifier("@drawable/" + gc.getIconFile(), null, context.getPackageName());
        vh.categoryIcon.setImageResource(iconId);
        vh.generalCategory.setText(gc.getCategoryName());

        LinearLayout ll = (LinearLayout) vh.itemView.findViewById(R.id.category_overview_linear_layout);
        ll.removeAllViews();

        for (final SubCategory sc : gc.getSubCategories()) {
            View v = inflater.inflate(R.layout.category_overview_subcategory_item, null, false);

            TextView subCategory = (TextView) v.findViewById(R.id.category_overivew_subcategory);
            subCategory.setText(sc.getCategoryName());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterInteractionListener.onSubCategoryClicked(sc);
                }
            });

            ll.addView(v);

        }

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterInteractionListener.onGeneralCategoryClicked(gc);
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_overview_item, parent, false);
        return new CategoryOverviewViewHolder(view);
    }

    public void refreshDataSource() {
        this.generalCategories = DataProvider.getGeneralCategories();
        notifyDataSetChanged();
    }

}
