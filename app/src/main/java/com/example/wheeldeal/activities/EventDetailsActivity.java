package com.example.wheeldeal.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.utils.DateClient;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableEvent;

import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {
    public static final String TAG = "EventDetailsActivity";
    Event event;
    private TextView tvEventDetailStart;
    private TextView tvEventDetailEnd;
    private TextView tvEventDetailCarName;
    private TextView tvEventDetailRenter;
    private TextView tvEventDetailCarOwner;
    private DateClient dateClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        event = ((ParcelableEvent) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableEvent.class.getSimpleName()))).getEvent();
        tvEventDetailStart = findViewById(R.id.tvEventDetailStart);
        tvEventDetailEnd = findViewById(R.id.tvEventDetailEnd);
        tvEventDetailCarName = findViewById(R.id.tvEventDetailCarName);
        tvEventDetailRenter = findViewById(R.id.tvEventDetailRenter);
        tvEventDetailCarOwner = findViewById(R.id.tvEventDetailCarOwner);
        dateClient = new DateClient();

        tvEventDetailStart.setText(dateClient.formatDate(event.getStart()));
        tvEventDetailEnd.setText(" to " + dateClient.formatDate(event.getEnd()));
        tvEventDetailCarName.setText(event.getCar().getModel());
        if (event.getRentType() == (Integer) 1){
            // user is renter, not owner
            tvEventDetailRenter.setVisibility(View.VISIBLE);
            tvEventDetailRenter.setText("Renter: " + event.getRenter().getUsername());
        } else {
            tvEventDetailRenter.setVisibility(View.GONE);
        }
        String name = "";
        try {
            name = event.getCar().getOwner().fetchIfNeeded().getUsername();
        } catch (com.parse.ParseException e){
            Log.v(TAG, e.toString());
            e.printStackTrace();
        }
        tvEventDetailCarOwner.setText("Owner: " + name);
    }
}