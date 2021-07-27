package com.example.wheeldeal.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.DateClient;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
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
                if (isValidDateWindow(start, end)){
                    Log.i(TAG, "valid time window");
                    if (EventConflictExists(start, end)){
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
    /*
     * Limit selectable range to days other than Mondays of the month
     */
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

    public int isDateBefore(int year1, int month1, int day1, int year2, int month2, int day2){
        if (year1 == year2){
            if (month1 == month2){
                if (day1 == day2){
                    return 0;
                } else if (day1 < day2){
                    return -1;
                } else {
                    return 1;
                }
            } else if (month1 < month2){
                return -1;
            } else {
                return 1;
            }
        } else if (year1 < year2){
            return -1;
        } else {
            return 1;
        }
    }


    public boolean EventConflictExists(Date start, Date end) {
        for (DateRangeHolder ranges : rangeHolders){
            Date eventStart = ranges.getStart();
            Date eventEnd = ranges.getEnd();
            int eventStartMonth = getMonth(eventStart);
            int eventStartDate = getDay(eventStart);
            int eventStartYear = getYear(eventStart);
            int eventEndMonth = getMonth(eventEnd);
            int eventEndDate = getDay(eventEnd);
            int eventEndYear = getYear(eventEnd);
            int startMonth = getMonth(start);
            int startDate = getDay(start);
            int startYear = getYear(start);
            int endMonth = getMonth(end);
            int endDate = getDay(end);
            int endYear = getYear(end);

            // check if start is in between eventStart and eventEnd
            if (isDateBefore(eventStartYear, eventStartMonth, eventStartDate, startYear, startMonth, startDate) <= 0
                    && isDateBefore(startYear, startMonth, startDate, eventEndYear, eventEndMonth, eventEndDate) <= 0){
                Log.i(TAG, "eventStart: " + dateClient.formatDate(eventStartYear, eventStartMonth, eventStartDate));
                Log.i(TAG, "eventEnd: " + dateClient.formatDate(eventEndYear, eventEndMonth, eventEndDate));
                Log.i(TAG, "start: " + dateClient.formatDate(startYear, startMonth, startDate));
                return true;
            }

            // check if end is in between eventStart and eventEnd
            if (isDateBefore(eventStartYear, eventStartMonth, eventStartDate, endYear, endMonth, endDate) <= 0
                    && isDateBefore(endYear, endMonth, endDate, eventEndYear, eventEndMonth, eventEndDate) <= 0){
                Log.i(TAG, "eventStart: " + dateClient.formatDate(eventStartYear, eventStartMonth, eventStartDate));
                Log.i(TAG, "eventEnd: " + dateClient.formatDate(eventEndYear, eventEndMonth, eventEndDate));
                Log.i(TAG, "end: " + dateClient.formatDate(endYear, endMonth, endDate));
                return true;
            }
        }
        return false;
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public boolean isValidDateWindow(Date start, Date end){
        // ensures startDateTime < endDateTime
        int comp = start.compareTo(end);
        if (comp > 0){
            // allow one day rentals
            return false;
        } else {
            // comp < 0 means start <  end
            return true;
        }
    }

    private void saveEvent(Date start, Date end){
        Log.i(TAG, "Saving event");
        Event event = new Event();
        event.setStart(start);
        event.setEnd(end);
        event.setRenter(ParseUser.getCurrentUser());
        event.setCar(car);
        event.setPrice(car.getRate());
        int rentType = 0;
        if (userIsCustomer()){
            rentType = 1;
        }
        event.setRentType(rentType);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Could not save", e);
                    Toast.makeText(ScheduleDatesActivity.this, "Could not save", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.i(TAG, "Event was saved to backend");
                    Toast.makeText(ScheduleDatesActivity.this, "Event was saved", Toast.LENGTH_SHORT).show();
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

    public int getMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month;
    }

    public int getDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public int getYear(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }
}