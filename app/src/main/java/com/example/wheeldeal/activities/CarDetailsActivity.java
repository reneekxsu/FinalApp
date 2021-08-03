package com.example.wheeldeal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.models.ParseApplication;
import com.example.wheeldeal.utils.QueryClient;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CarDetailsActivity extends AppCompatActivity {
    public static final String TAG = "UserCarDetailsActivity";

    Car car;
    TextView tvCarDetailName, tvDetailRate, tvDetailDescription;
    ImageView ivDetailCar;
    Context context;
    public static ImageButton ibtnEdit;
    ImageButton ibtnEvent, ibtnDelete;
    QueryClient queryClient;
    ArrayList<DateRangeHolder> rangeHolder;
    ArrayList<Event> allEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        tvCarDetailName = findViewById(R.id.tvDetailMakeModelYear);
        tvDetailRate = findViewById(R.id.tvDetailRate);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        ivDetailCar = findViewById(R.id.ivDetailCar);
        ibtnEdit = findViewById(R.id.ibtnEdit);
        ibtnEvent = findViewById(R.id.ibtnEvent);
        ibtnDelete = findViewById(R.id.ibtnDelete);
        context = this;
        queryClient = new QueryClient();
        rangeHolder = new ArrayList<>();
        allEvents = new ArrayList<>();

        ibtnEvent.setVisibility(View.GONE);
        fetchAllCarEvents(car);

        tvCarDetailName.setText(car.getMake() + " " + car.getModel() + " " + car.getYear());
        ParseFile image = car.getImage();
        if (image != null) {
            ivDetailCar.setVisibility(View.VISIBLE);
            Glide.with(context).load(image.getUrl()).into(ivDetailCar);
        } else {
            ivDetailCar.setVisibility(View.GONE);
        }
        tvDetailRate.setText("$" + car.getRate() + "/day");
        tvDetailDescription.setText(car.getDescription());

        if (!userIsAuthor(car)){
            ibtnEdit.setVisibility(View.GONE);
        } else {
            if (!((ParseApplication) getApplication()).isDataReady){
                ibtnEdit.setVisibility(View.INVISIBLE);
            } else {
                ibtnEdit.setVisibility(View.VISIBLE);
            }
            ibtnEdit.setBackgroundDrawable(null);
            ibtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParcelableCar c = new ParcelableCar(car);
                    Intent i = new Intent(context, EditCarActivity.class);
                    i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(c));
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
                    finish();
                }
            });
        }

        // car owner will schedule when they are not free, while customer will schedule when they want to rent it
        ibtnEvent.setBackgroundDrawable(null);
        ibtnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "car schedule button clicked");
                Intent i = new Intent(context, ScheduleDatesActivity.class);
                ParcelableCar c = new ParcelableCar(car);
                i.putExtra("ParcelableCar", Parcels.wrap(c));
                if (rangeHolder.size() == 0){
                    Log.i(TAG, "no event ranges for this car");
                }
                i.putExtra("CarEvents", Parcels.wrap(rangeHolder));
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        ibtnDelete.setVisibility(View.GONE);
        ibtnDelete.setBackgroundDrawable(null);
        ibtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAssociatedEvents(car);
            }
        });

    }

    boolean userIsAuthor(Car car){
        return car.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    private void fetchAllCarEvents(Car car){
        queryClient.fetchAllCarEvents(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                Log.i(TAG, "event query for this car complete");
                for (Event event : events){
                    Log.i(TAG, "event start: " + event.getStart().toString());
                    rangeHolder.add(new DateRangeHolder(event.getStart(), event.getEnd()));
                }
                allEvents.addAll(events);
                ibtnEvent.setVisibility(View.VISIBLE);
                ibtnDelete.setVisibility(View.VISIBLE);
            }
        }, car);
    }
    private void deleteAssociatedEvents(Car car){
        queryClient.deleteAssociatedEvents(car, allEvents);
        queryClient.deleteCar(car, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}