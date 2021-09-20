package io.accroo.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.util.ArrayList;

import io.accroo.android.R;
import io.accroo.android.model.SessionData;
import io.accroo.android.services.DataProvider;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private Context context;
    private ArrayList<SessionData> sessions;
    private AdapterInteractionListener adapterInteractionListener;
    private String currentSessionId;

    public SessionAdapter(Context context, AdapterInteractionListener adapterInteractionListener,
                          String currentSessionId) {
        this.context = context;
        this.adapterInteractionListener = adapterInteractionListener;
        this.currentSessionId = currentSessionId;
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

    public interface AdapterInteractionListener {
        void onSessionSelected(SessionData sessionData);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        SessionData sessionData = sessions.get(position);
        String deviceName = sessionData.getDeviceName() != null ? sessionData.getDeviceName() : context.getString(R.string.unknown);
        holder.deviceName.setText(deviceName);
        String lastActiveLabel = null;
        if (sessionData.getId().toString().equals(currentSessionId)) {
            lastActiveLabel = context.getString(R.string.current_device);
        } else {
            DateTime currentTime = new DateTime();
            DateTime lastActive = null;
            String incrementUnit;
            if (sessionData.getDateLastRefreshed() != null) {
                lastActive = new DateTime(sessionData.getDateLastRefreshed());
            } else {
                lastActive = new DateTime(sessionData.getDateCreated());
            }

            int incrementDays = Days.daysBetween(lastActive, currentTime).getDays();
            int incrementHours = Hours.hoursBetween(lastActive, currentTime).getHours();
            int incrementMinutes = Minutes.minutesBetween(lastActive, currentTime).getMinutes();
            int incrementSeconds = Seconds.secondsBetween(lastActive, currentTime).getSeconds();

            int increment;
            if (incrementDays >= 1) {
                increment = incrementDays;
                incrementUnit = incrementDays != 1 ? context.getString(R.string.days) : context.getString(R.string.day);
            } else if (incrementHours >= 1) {
                increment = incrementHours;
                incrementUnit = incrementHours != 1 ? context.getString(R.string.hours) : context.getString(R.string.hour);
            } else if (incrementMinutes >= 1) {
                increment = incrementMinutes;
                incrementUnit = incrementMinutes != 1 ? context.getString(R.string.minutes) : context.getString(R.string.minute);
            } else {
                increment = incrementSeconds;
                incrementUnit = incrementSeconds != 1 ? context.getString(R.string.seconds) : context.getString(R.string.second);
            }

            lastActiveLabel = String.format(context.getString(R.string.last_active), increment, incrementUnit);
            //lastActiveLabel = "Last active " + increment + " " + incrementUnit + " ago";
        }
        holder.lastActive.setText(lastActiveLabel);
        holder.itemView.setOnClickListener(view -> adapterInteractionListener.onSessionSelected(sessionData));
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
