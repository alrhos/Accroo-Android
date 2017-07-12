package com.paleskyline.navicash.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.paleskyline.navicash.R;

import java.lang.reflect.Field;

/**
 * Created by oscar on 12/07/17.
 */

public class SelectIconAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int[] iconRows;
    private int[] iconColumns;
    private int[][] icons;
    private LayoutInflater inflater;
    private String path;
    private Field[] drawables;
    private final int cols = 5;
    private int rows;

    public SelectIconAdapter(Context context) {
        this.context = context;
        this.drawables = android.R.drawable.class.getFields();
        rows = drawables.length / cols;
        icons = new int[cols][rows];

        System.out.println("ROWS: " + rows);

        try {
            for (Field f : drawables) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        icons[j][i] = f.getInt(null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        int dummy = context.getResources().getIdentifier("@drawable/cheese", null, context.getPackageName());

        vh.icon1.setImageResource(dummy);
        vh.icon2.setImageResource(icons[1][position]);
        vh.icon3.setImageResource(icons[2][position]);
        vh.icon4.setImageResource(icons[3][position]);
        vh.icon5.setImageResource(icons[4][position]);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_selector_item, parent, false);
        return new IconViewHolder(view);
    }

}
