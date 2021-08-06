package com.example.wheeldeal.oldclasses;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableEvent;
import com.example.wheeldeal.utils.DateClient;

import org.parceler.Parcels;

public class EventDetailsActivity extends AppCompatActivity {
    public static final String TAG = "EventDetailsActivity";
    Event event;
    private TextView tvEventDetailStart, tvEventDetailEnd, tvEventDetailCarName,
            tvEventDetailRenter, tvEventDetailCarOwner, tvGetDirections, tvEventPrice;
    private DateClient dateClient;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Booking Details");

        event = ((ParcelableEvent) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableEvent.class.getSimpleName()))).getEvent();
        tvEventDetailStart = findViewById(R.id.tvEventDetailStart);
        tvEventDetailEnd = findViewById(R.id.tvEventDetailEnd);
        tvEventDetailCarName = findViewById(R.id.tvEventDetailCarName);
        tvEventDetailRenter = findViewById(R.id.tvEventDetailRenter);
        tvEventDetailCarOwner = findViewById(R.id.tvEventDetailCarOwner);
        tvGetDirections = findViewById(R.id.tvGetDirections);
        tvEventPrice = findViewById(R.id.tvEventPrice);
        dateClient = new DateClient();

        tvGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sDestination = event.getCar().getAddress();

                if (sDestination.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Car has no associated address", Toast.LENGTH_SHORT).show();
                } else {
                    displayTrack(sDestination);
                }
            }
        });

        tvEventDetailStart.setText(dateClient.formatDate(event.getStart()));
        tvEventDetailEnd.setText(" to " + dateClient.formatDate(event.getEnd()) + ": (" + event.getNumDays() + " day trip)");
        tvEventDetailCarName.setText(event.getCar().getMake() + " " + event.getCar().getModel() + " " + event.getCar().getYear());
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
        tvEventPrice.setText("$" + event.getPrice() + "/day");
        tvEventDetailCarOwner.setText("Owner: " + name);
    }

    private void displayTrack(String sDestination) {
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + sDestination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}