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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.bumptech.glide.Glide;
import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.models.ParseApplication;
import com.example.wheeldeal.utils.DateClient;
import com.example.wheeldeal.utils.EventRangesOutValidator;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CarDetailsActivity extends AppCompatActivity {
    public static final String TAG = "UserCarDetailsActivity";

    Car car;
    TextView tvCarDetailName, tvDetailRate, tvDetailDescription;
    ImageView ivDetailCar;
    Context context;
    public static ImageButton ibtnEdit;
    ImageButton ibtnEvent;
    QueryClient queryClient;
    ArrayList<DateRangeHolder> rangeHolder;
    ArrayList<Event> allEvents;
    DateClient dateClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Car Details");

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        tvCarDetailName = findViewById(R.id.tvDetailMakeModelYear);
        tvDetailRate = findViewById(R.id.tvDetailRate);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        ivDetailCar = findViewById(R.id.ivDetailCar);
        ibtnEdit = findViewById(R.id.ibtnEdit);
        ibtnEvent = findViewById(R.id.ibtnEvent);
        context = this;
        queryClient = new QueryClient();
        rangeHolder = new ArrayList<>();
        allEvents = new ArrayList<>();
        dateClient = new DateClient();

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

        ibtnEdit.setVisibility(View.GONE);

        if (!userIsAuthor(car)){
            ibtnEdit.setVisibility(View.GONE);
        } else {
            if (!((ParseApplication) getApplication()).isDataReady){
                ibtnEdit.setVisibility(View.GONE);
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

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        builder.setCalendarConstraints(calConstraints().build());
        builder.setTitleText("Select a Date");
        final MaterialDatePicker picker = builder.build();

        // car owner will schedule when they are not free, while customer will schedule when they want to rent it
        ibtnEvent.setBackgroundDrawable(null);
        ibtnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "car schedule button clicked");
                picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });


        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override public void onPositiveButtonClick(Pair<Long,Long> selection) {
                Long startDate = selection.first;
                Long endDate = selection.second;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(startDate);
                Date start = c.getTime();
                c.setTimeInMillis(endDate);
                Date end = c.getTime();
                Log.i("ScheduleDatesActivity", start.toString());
                Log.i(TAG, "start date is: " + start.toString());
                Log.i(TAG, "end date is: " + end.toString());
                if (dateClient.isValidDateWindow(start, end)){
                    Log.i(TAG, "valid time window");
                    if (dateClient.EventConflictExists(start, end, rangeHolder)){
                        Log.i(TAG, "event conflicts exist");
                        Toast.makeText(CarDetailsActivity.this, "Event conflicts with another", Toast.LENGTH_SHORT).show();
                    } else {
                        saveEvent(start, end);
                    }
                } else {
                    Log.i(TAG, "not valid time window");
                    Toast.makeText(CarDetailsActivity.this, "Not a valid time window", Toast.LENGTH_SHORT).show();
                }
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
                allEvents.addAll(events);
                ibtnEvent.setVisibility(View.VISIBLE);
            }
        }, car);
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

    private CalendarConstraints.Builder calConstraints() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        EventRangesOutValidator m = new EventRangesOutValidator(2021, Calendar.JULY, Calendar.MONDAY, rangeHolder);
        CalendarConstraints.DateValidator validator = DateValidatorPointForward.now();
        ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<CalendarConstraints.DateValidator>();
        listValidators.add(validator);
        listValidators.add(m);
        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
        constraintsBuilderRange.setValidator(validators);
        return constraintsBuilderRange;
    }

    private void saveEvent(Date start, Date end){
        ParseUser current = ParseUser.getCurrentUser();
        if (userIsCustomer()){
            queryClient.fetchUserDetails(current, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    int numBooked = (int)user.get("carsBooked");
                    double avgScore;
                    if ((double)user.get("avgScore") == 0){
                        avgScore = (double) 0;
                    } else {
                        avgScore = (double)user.get("avgScore");
                    }
                    double sum = avgScore * numBooked;
                    Log.i(TAG, "carsbooked for user: " + numBooked);
                    user.put("carsBooked", numBooked + 1);
                    double newScore = (double)car.getScore();
                    double newAvgScore = (sum + newScore) / (numBooked + 1);
                    user.put("avgScore", newAvgScore);
                    user.saveInBackground();
                }
            });
        }

        queryClient.saveEvent(start, end, car, userIsCustomer(), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Could not save", e);
                    Toast.makeText(CarDetailsActivity.this, "Could not save", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.i(TAG, "Event was saved to backend");
                    Toast.makeText(CarDetailsActivity.this, "Event was saved", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CarDetailsActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

}