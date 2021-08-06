package com.example.wheeldeal.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.EventDetailsActivity;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableEvent;
import com.example.wheeldeal.utils.DateClient;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.Viewholder> {
    private Context context;
    private List<Event> events;
    private DateClient dateClient;
    public static final String TAG = "EventAdapter";

    public EventAdapter(Context context, List<Event> events){
        this.context = context;
        this.events = events;
        dateClient = new DateClient();
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
        private TextView tvEventCarModel;
        private TextView tvEventCarMake;
        private TextView tvEventCarYear;
        private TextView tvRenter;
        private TextView tvCarOwner;
        private TextView tvAddress;
        private TextView tvPrice;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            tvEventCarModel = itemView.findViewById(R.id.tvEventCarModel);
            tvEventCarMake = itemView.findViewById(R.id.tvEventCarMake);
            tvEventCarYear = itemView.findViewById(R.id.tvEventCarYear);
            tvRenter = itemView.findViewById(R.id.tvRenter);
            tvCarOwner = itemView.findViewById(R.id.tvCarOwner);
            tvAddress = itemView.findViewById(R.id.tvPickUpAddress);
            tvPrice = itemView.findViewById(R.id.tvTotalPrice);
            // onclicklistener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Event event = events.get(position);
                        ParcelableEvent e = new ParcelableEvent(event);
                        Intent i = new Intent(context, EventDetailsActivity.class);
                        i.putExtra(ParcelableEvent.class.getSimpleName(), Parcels.wrap(e));
                        context.startActivity(i);
                    }
                }
            });
        }

        public void bind(Event event) {
            tvStart.setText(dateClient.formatDate(event.getStart()));
            tvEnd.setText(" to " + dateClient.formatDate(event.getEnd()));
            tvEventCarMake.setText(event.getCar().getMake());
            tvEventCarModel.setText(" " + event.getCar().getModel());
            tvEventCarYear.setText(" " + event.getCar().getYear());
            tvPrice.setText("$" + Integer.toString((int)event.getNumDays() * (int)Integer.parseInt(event.getPrice())));

            if (event.getRentType() == (Integer) 1){
                // user is renter, not owner
                tvRenter.setVisibility(View.VISIBLE);
                tvRenter.setText("Renter: " + event.getRenter().getUsername());
                tvCarOwner.setVisibility(View.GONE);
            } else {
                // user is owner of their own car event
                tvRenter.setVisibility(View.GONE);
                tvCarOwner.setVisibility(View.VISIBLE);
                String name = "";
                try {
                    name = event.getCar().getOwner().fetchIfNeeded().getUsername();
                } catch (com.parse.ParseException e){
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
                tvCarOwner.setText("Owner: " + name);
            }
            tvAddress.setText("Pick Up Address: " + event.getCar().getAddress());
        }
    }
    public void clear(){
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> list){
        events.addAll(list);
        notifyDataSetChanged();
    }

}
