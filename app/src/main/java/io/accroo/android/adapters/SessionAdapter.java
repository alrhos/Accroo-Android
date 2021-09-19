package io.accroo.android.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.accroo.android.R;
import io.accroo.android.model.SessionData;
import io.accroo.android.services.DataProvider;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private ArrayList<SessionData> sessions;

    public SessionAdapter() {
        sessions = new ArrayList<>();
    }

    class SessionViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName, lastActive;

        public SessionViewHolder(View view) {
            super(view);
            deviceName = view.findViewById(R.id.device_name);
            lastActive = view.findViewById(R.id.last_active);
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionData sessionData = sessions.get(position);
        holder.deviceName.setText(sessionData.getDeviceName());
        holder.lastActive.setText(sessionData.getDateLastRefreshed());
    }

    @Override
    @NonNull
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_list_item, parent, false);
        return new SessionViewHolder(view);
    }

    public void refreshDataSource() {
        sessions = DataProvider.getSessions();
        notifyDataSetChanged();
    }

}
