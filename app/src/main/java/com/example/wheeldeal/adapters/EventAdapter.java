package com.example.wheeldeal.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.utils.DateClient;

import org.jetbrains.annotations.NotNull;

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
        private TextView tvEventCarMake;
        private TextView tvRenter;
        private TextView tvCarOwner;
        private TextView tvAddress;
        private TextView tvPrice;
        private ImageButton ibtnDirections;
        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            tvEventCarMake = itemView.findViewById(R.id.tvEventCarMakeModelYear);
            tvRenter = itemView.findViewById(R.id.tvRenter);
            tvCarOwner = itemView.findViewById(R.id.tvCarOwner);
            tvAddress = itemView.findViewById(R.id.tvPickUpAddress);
            tvPrice = itemView.findViewById(R.id.tvTotalPrice);
            ibtnDirections = itemView.findViewById(R.id.ibtnDirections);
        }

        public void bind(Event event) {
            tvStart.setText(dateClient.formatDate(event.getStart()));
            tvEnd.setText(" to " + dateClient.formatDate(event.getEnd()));
            tvEventCarMake.setText(event.getCar().getMake() + " " + event.getCar().getModel()
                    + " " + event.getCar().getYear());
            tvPrice.setText("$" + Integer.toString((int)event.getNumDays() * (int)Integer.parseInt(event.getPrice())));
            ibtnDirections.setBackground(null);
            ibtnDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Displaying Route", Toast.LENGTH_SHORT).show();
                    displayTrack(event.getCar().getAddress());
                }
            });

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

    private void displayTrack(String sDestination) {
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + sDestination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
