package com.paleskyline.navicash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.database.DataAccess;

import java.util.ArrayList;

/**
 * Created by oscar on 12/07/17.
 */

public class IconAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private AdapterInteractionListener adapterListener;
    private String[][] iconArray;
   // private LayoutInflater inflater;
    private final int cols = 5;
    private int rows;

    private ArrayList<String> icons;

    public IconAdapter(Context context , AdapterInteractionListener adapterListener) {

        this.context = context;
        this.adapterListener = adapterListener;
        this.icons = DataAccess.getInstance(context).getIcons();
        rows = icons.size() / cols;
        // TODO - make sure icon count is divisible by 5
        iconArray = new String[cols][rows];

        int curr = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                iconArray[j][i] = icons.get(curr);
                curr++;
            }
        }

    }

    class IconViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon1, icon2, icon3, icon4, icon5;

        public IconViewHolder(View view) {
            super(view);
            icon1 = (ImageView) view.findViewById(R.id.icon1);
            icon2 = (ImageView) view.findViewById(R.id.icon2);
            icon3 = (ImageView) view.findViewById(R.id.icon3);
            icon4 = (ImageView) view.findViewById(R.id.icon4);
            icon5 = (ImageView) view.findViewById(R.id.icon5);
        }

    }

    @Override
    public int getItemCount() {
        return rows;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        IconViewHolder vh = (IconViewHolder) holder;

        // TODO - review this to see if there's a cleaner way to to this.

        final int iconId1 = context.getResources().getIdentifier("@drawable/" + iconArray[0][position], null, context.getPackageName());
        final int iconId2 = context.getResources().getIdentifier("@drawable/" + iconArray[1][position], null, context.getPackageName());
        final int iconId3 = context.getResources().getIdentifier("@drawable/" + iconArray[2][position], null, context.getPackageName());
        final int iconId4 = context.getResources().getIdentifier("@drawable/" + iconArray[3][position], null, context.getPackageName());
        final int iconId5 = context.getResources().getIdentifier("@drawable/" + iconArray[4][position], null, context.getPackageName());

        vh.icon1.setImageResource(iconId1);
        vh.icon2.setImageResource(iconId2);
        vh.icon3.setImageResource(iconId3);
        vh.icon4.setImageResource(iconId4);
        vh.icon5.setImageResource(iconId5);

        vh.icon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(iconId1);
                adapterListener.onIconSelected(iconId1);
            }
        });

        vh.icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(iconId2);
                adapterListener.onIconSelected(iconId2);
            }
        });

        vh.icon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(iconId3);
                adapterListener.onIconSelected(iconId3);
            }
        });

        vh.icon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(iconId4);
                adapterListener.onIconSelected(iconId4);
            }
        });

        vh.icon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(iconId5);
                adapterListener.onIconSelected(iconId5);
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_selector_item, parent, false);
        return new IconViewHolder(view);
    }

    public interface AdapterInteractionListener {
        void onIconSelected(int iconID);
    }

}