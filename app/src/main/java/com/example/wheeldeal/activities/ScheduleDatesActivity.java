package com.example.wheeldeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.DateClient;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleDatesActivity extends AppCompatActivity {

    public static final String TAG = "ScheduleDatesActivity";

    List<Event> carEvents = new ArrayList<>();
    Button btnPickDate, btnDonePickDates;
    TextView tvShowSelectedDate;
    Date start, end;
    Car car;
    DateClient dateClient;
    ArrayList<DateRangeHolder> rangeHolders;
    QueryClient queryClient = new QueryClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_dates);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra("ParcelableCar"))).getCar();
        rangeHolders = (ArrayList<DateRangeHolder>) Parcels.unwrap(getIntent().getParcelableExtra("CarEvents"));

        for (DateRangeHolder range : rangeHolders){
            Log.i(TAG, "range: " + range.getStart().toString() + " to " + range.getEnd().toString());
        }

        btnPickDate = findViewById(R.id.pick_date_button);
        tvShowSelectedDate = findViewById(R.id.show_selected_date);
        btnDonePickDates = findViewById(R.id.btnDonePickDates);
        btnDonePickDates.setEnabled(false);
        dateClient = new DateClient();

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        builder.setCalendarConstraints(calConstraints().build());
        builder.setTitleText("Select a Date");
        final MaterialDatePicker picker = builder.build();
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override public void onPositiveButtonClick(Pair<Long,Long> selection) {
                tvShowSelectedDate.setText("Selected Date Range: " + picker.getHeaderText());
                Long startDate = selection.first;
                Long endDate = selection.second;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(startDate);
                start = c.getTime();
                c.setTimeInMillis(endDate);
                end = c.getTime();
                Log.i("ScheduleDatesActivity", start.toString());
                btnDonePickDates.setEnabled(true);
            }
        });

        btnDonePickDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "start date is: " + start.toString());
                Log.i(TAG, "end date is: " + end.toString());
                if (dateClient.isValidDateWindow(start, end)){
                    Log.i(TAG, "valid time window");
                    if (dateClient.EventConflictExists(start, end, rangeHolders)){
                        Log.i(TAG, "event conflicts exist");
                        Toast.makeText(ScheduleDatesActivity.this, "Event conflicts with another", Toast.LENGTH_SHORT).show();
                    } else {
                        saveEvent(start, end);
                    }
                } else {
                    Log.i(TAG, "not valid time window");
                    Toast.makeText(ScheduleDatesActivity.this, "Not a valid time window", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private CalendarConstraints.Builder calConstraints() {
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        EventRangesOutValidator m = new EventRangesOutValidator(2021, Calendar.JULY, Calendar.MONDAY, rangeHolders);
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
                    Toast.makeText(ScheduleDatesActivity.this, "Could not save", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.i(TAG, "Event was saved to backend");
                    Toast.makeText(ScheduleDatesActivity.this, "Event was saved", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ScheduleDatesActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
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
}