package com.example.finalapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalapp.R;
import com.example.finalapp.models.Event;
import com.example.finalapp.models.ParcelableEvent;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {
    public static final String TAG = "EventDetailsActivity";
    Event event;
    private TextView tvEventDetailStart;
    private TextView tvEventDetailEnd;
    private TextView tvEventDetailCarName;
    private TextView tvEventDetailRenter;
    private TextView tvEventDetailCarOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        event = ((ParcelableEvent) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableEvent.class.getSimpleName()))).getEvent();
        tvEventDetailStart = (TextView) findViewById(R.id.tvEventDetailStart);
        tvEventDetailEnd = (TextView) findViewById(R.id.tvEventDetailEnd);
        tvEventDetailCarName = (TextView) findViewById(R.id.tvEventDetailCarName);
        tvEventDetailRenter = (TextView) findViewById(R.id.tvEventDetailRenter);
        tvEventDetailCarOwner = (TextView) findViewById(R.id.tvEventDetailCarOwner);

        tvEventDetailStart.setText(formatDate(event.getStart()));
        tvEventDetailEnd.setText(" to " + formatDate(event.getEnd()));
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

    public String formatDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        return "" + month + "/" + day + "/" + year;
    }
}