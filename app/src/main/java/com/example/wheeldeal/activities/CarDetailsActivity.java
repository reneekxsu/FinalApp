package com.example.wheeldeal.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.QueryClient;
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
    TextView tvCarDetailName;
    TextView tvDetailRate;
    TextView tvDetailDescription;
    ImageView ivDetailCar;
    Context context;
    ImageButton ibtnEdit;
    ImageButton ibtnEvent;
    QueryClient queryClient;
    ArrayList<DateRangeHolder> rangeHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_details);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        tvCarDetailName = findViewById(R.id.tvCarDetailName);
        tvDetailRate = findViewById(R.id.tvDetailRate);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        ivDetailCar = findViewById(R.id.ivDetailCar);
        ibtnEdit = findViewById(R.id.ibtnEdit);
        ibtnEvent = findViewById(R.id.ibtnEvent);
        context = this;
        queryClient = new QueryClient();
        rangeHolder = new ArrayList<>();

        ibtnEdit.setVisibility(View.GONE);
        fetchAllCarEvents(car);

        tvCarDetailName.setText(car.getModel());
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
            ibtnEdit.setVisibility(View.VISIBLE);
            ibtnEdit.setBackgroundDrawable(null);
            ibtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParcelableCar c = new ParcelableCar(car);
                    Intent i = new Intent(context, EditCarActivity.class);
                    i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(c));
                    startActivity(i);
                    finish();
                }
            });
        }

        // car owner will schedule when they are not free, while customer will schedule when they want to rent it
        ibtnEvent.setBackgroundDrawable(null);
        ibtnEvent.setVisibility(View.VISIBLE);
        ibtnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "car schedule button clicked");
                Intent i = new Intent(context, ScheduleDatesActivity.class);
                ParcelableCar c = new ParcelableCar(car);
                i.putExtra("ParcelableCar", Parcels.wrap(c));
                i.putExtra("CarEvents", Parcels.wrap(rangeHolder));
                startActivity(i);
            }
        });

    }

    boolean userIsAuthor(Car car){
        return car.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    boolean userIsCustomer(){
        return !userIsAuthor(car);
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
                ibtnEvent.setVisibility(View.VISIBLE);
            }
        }, car);
    }

}