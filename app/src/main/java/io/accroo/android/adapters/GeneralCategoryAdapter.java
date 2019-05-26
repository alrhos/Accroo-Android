package io.accroo.android.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.accroo.android.R;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.services.DataProvider;

import java.util.ArrayList;

/**
 * Created by oscar on 22/07/17.
 */

public class GeneralCategoryAdapter extends RecyclerView.Adapter<GeneralCategoryAdapter.GeneralCategoryViewHolder> {

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
            category = view.findViewById(R.id.category_name);
            icon = view.findViewById(R.id.category_icon);
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
    public void onBindViewHolder(@NonNull GeneralCategoryViewHolder holder, int position) {
        final GeneralCategory generalCategory = dataSource.get(position);
        int iconId = context.getResources().getIdentifier("@drawable/" +
                generalCategory.getIconFile(), null, context.getPackageName());
        holder.icon.setImageResource(iconId);
        holder.category.setText(generalCategory.getCategoryName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterInteractionListener.onGeneralCategorySelected(generalCategory);
            }
        });
    }

    @Override
    @NonNull
    public GeneralCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.general_category_list_item,
                parent, false);
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
