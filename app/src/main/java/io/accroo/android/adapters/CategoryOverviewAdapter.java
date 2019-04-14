package io.accroo.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.accroo.android.R;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.services.DataProvider;

import java.util.ArrayList;

/**
 * Created by oscar on 11/06/17.
 */

public class CategoryOverviewAdapter extends RecyclerView.Adapter<CategoryOverviewAdapter.CategoryOverviewViewHolder> {

    private Context context;
    private AdapterInteractionListener adapterInteractionListener;
    private ArrayList<GeneralCategory> generalCategories;
    private LayoutInflater inflater;

    public CategoryOverviewAdapter(Context context, AdapterInteractionListener adapterInteractionListener) {
        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        generalCategories = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public interface AdapterInteractionListener {
        void onEmptyList();
        void onNonEmptyList();
        void onGeneralCategoryClicked(GeneralCategory generalCategory);
        void onSubCategoryClicked(SubCategory subCategory);
    }

    class CategoryOverviewViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView generalCategory;

        public CategoryOverviewViewHolder(View view) {
            super(view);
            categoryIcon = view.findViewById(R.id.category_icon);
            generalCategory = view.findViewById(R.id.category_name);
        }
    }

    @Override
    public int getItemCount() {
        return generalCategories.size();
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryOverviewViewHolder holder, int position) {
        final GeneralCategory gc = generalCategories.get(position);
        int iconId = context.getResources().getIdentifier("@drawable/" + gc.getIconFile(), null, context.getPackageName());
        holder.categoryIcon.setImageResource(iconId);
        holder.generalCategory.setText(gc.getCategoryName());

        LinearLayout ll = holder.itemView.findViewById(R.id.category_overview_linear_layout);
        ll.removeAllViews();

        for (final SubCategory sc : gc.getSubCategories()) {
            View v = inflater.inflate(R.layout.category_overview_subcategory_item, null, false);

            TextView subCategory = v.findViewById(R.id.category_overview_subcategory);
            subCategory.setText(sc.getCategoryName());

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterInteractionListener.onSubCategoryClicked(sc);
                }
            });

            ll.addView(v);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterInteractionListener.onGeneralCategoryClicked(gc);
            }
        });

    }

    @Override
    @NonNull
    public CategoryOverviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_overview_item, parent, false);
        return new CategoryOverviewViewHolder(view);
    }

    public void refreshDataSource() {
        generalCategories = DataProvider.getGeneralCategories();
        if (generalCategories.size() == 0) {
            adapterInteractionListener.onEmptyList();
        } else {
            adapterInteractionListener.onNonEmptyList();
            notifyDataSetChanged();
        }
    }

}
