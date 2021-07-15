package com.example.finalapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.R;
import com.example.finalapp.models.Event;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.Viewholder> {
    private Context context;
    private List<Event> events;
    public static final String TAG = "EventAdapter";

    public EventAdapter(Context context, List<Event> events){
        this.context = context;
        this.events = events;
    }

    @NonNull
    @NotNull
    @Override
    public EventAdapter.Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EventAdapter.Viewholder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{
        private TextView tvStart;
        private TextView tvEnd;
        private TextView tvEventCarName;
        private TextView tvRenter;
        private TextView tvCarOwner;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            tvEventCarName = itemView.findViewById(R.id.tvEventCarName);
            tvRenter = itemView.findViewById(R.id.tvRenter);
            tvCarOwner = itemView.findViewById(R.id.tvCarOwner);
            // onclicklistener for later
        }

        public void bind(Event event) {
            tvStart.setText(formatDate(event.getStart()));
            tvEnd.setText(" to " + formatDate(event.getEnd()));
            tvEventCarName.setText(event.getCar().getModel());
            if (event.getRentType() == (Integer) 1){
                // user is renter, not owner
                tvRenter.setVisibility(View.VISIBLE);
                tvRenter.setText("Renter: " + event.getRenter().getUsername());
            } else {
                tvRenter.setVisibility(View.GONE);
            }
            String name = "";
            try {
                name = event.getCar().getOwner().fetchIfNeeded().getUsername();
            } catch (com.parse.ParseException e){
                Log.v(TAG, e.toString());
                e.printStackTrace();
            }
            tvCarOwner.setText("Owner: " + name);
        }
    }
    public void clear(){
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> list){
        events.addAll(list);
        Log.i(TAG, "Events in adapter: " + events);
        notifyDataSetChanged();
    }

    // ideal date format:  1/16/2021 2:00pm
    public String formatDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int ampm = c.get(Calendar.AM_PM);
        String timeSign = "AM";
        if (ampm == 1){
            timeSign = "PM";
        }
        return "" + month + "/" + day + "/" + year + " " + hour + ":" + minute + " " + timeSign;
    }

}
