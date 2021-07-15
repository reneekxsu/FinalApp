package com.example.finalapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.finalapp.R;
import com.example.finalapp.fragments.DatePickerFragment;
import com.example.finalapp.models.Car;
import com.example.finalapp.models.Event;
import com.example.finalapp.models.ParcelableCar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ScheduleTimesActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener{
    public static final String TAG = "ScheduleTimesActivity";
    List<Event> carEvents = new ArrayList<>();

    Button btnStartDate, btnEndDate, btnDone;
    FragmentManager fm = getSupportFragmentManager();
    int DATE_DIALOG = 0;
    int TIME_DIALOG = 0;
    int startDay, startMonth, startYear, startHour, startMinute;
    int endDay, endMonth, endYear, endHour, endMinute;
    Car car;
    TimeZone tz = TimeZone.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_times);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra("ParcelableCar"))).getCar();
        btnStartDate = (Button) findViewById(R.id.btnStartDate);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnDone = (Button) findViewById(R.id.btnDone);

        // SELECT A START DATE
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATE_DIALOG = 1;
                openDateDialog();
            }
        });
        // SELECT A STOP DATE
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATE_DIALOG = 2;
                openDateDialog();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date start = getDateTimeFromPickers(startDay, startMonth, startYear, startHour, startMinute);
                Date end = getDateTimeFromPickers(endDay, endMonth, endYear, endHour, endMinute);
                Log.i(TAG, "start date is: " + start.toString());
                Log.i(TAG, "end date is: " + end.toString());
                if (isValidDateWindow(start, end)){
                    Log.i(TAG, "valid time window");
                    getCarEvents();
                } else {
                    Log.i(TAG, "not valid time window");
                    Toast.makeText(ScheduleTimesActivity.this, "Not a valid time window", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getCarEvents(){
        // check for all events that has this car
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo(Event.KEY_CAR, car);
        query.addAscendingOrder(Event.KEY_START);
        query.include(Event.KEY_CAR);
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get events associated with this car");
                    Log.e(TAG, e.getCause().toString());
                } else {
                    for (Event event : events){
                        Log.i(TAG, "Events associated with this car: " + event.getCar().getModel());
                    }
                    carEvents.addAll(events);
                    Date start = getDateTimeFromPickers(startDay, startMonth, startYear, startHour, startMinute);
                    Date end = getDateTimeFromPickers(endDay, endMonth, endYear, endHour, endMinute);
                    if (EventConflictExists(events, start, end)){
                        Toast.makeText(ScheduleTimesActivity.this, "Event conflicts with another", Toast.LENGTH_SHORT).show();
                    } else {
                        saveEvent(start, end);
                    }

                }
            }
        });
    }

    public boolean EventConflictExists(List<Event> events, Date start, Date end) {
        for (Event event : events){
            Date eventStart = event.getStart();
            Date eventEnd = event.getEnd();
            int compEventStartAndDateStart = eventStart.compareTo(start);
            int compDateStartAndEventEnd = start.compareTo(eventEnd);
            if (compEventStartAndDateStart <= 0 && compDateStartAndEventEnd <= 0){
                // start date of new event lies in interval of another event
                Log.i(TAG, "eventStart: " + formatDate(eventStart));
                Log.i(TAG, "eventEnd: " + formatDate(eventEnd));
                Log.i(TAG, "start: " + formatDate(start));
                Log.i(TAG, "end: " + formatDate(end));
                Toast.makeText(this, "Start interval invalid", Toast.LENGTH_SHORT).show();
                return true;
            }
            int compEventStartAndDateEnd = eventStart.compareTo(end);
            int compDateEndAndEventEnd = end.compareTo(eventEnd);
            if (compEventStartAndDateEnd <= 0 && compDateEndAndEventEnd <= 0){
                // start date of new event lies in interval of another event
                Log.i(TAG, "eventStart: " + formatDate(eventStart));
                Log.i(TAG, "eventEnd: " + formatDate(eventEnd));
                Log.i(TAG, "start: " + formatDate(start));
                Log.i(TAG, "end: " + formatDate(end));
                Toast.makeText(this, "End interval invalid", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public void openDateDialog(){
        DatePickerFragment datepickDialog = new DatePickerFragment();
        datepickDialog.show(fm, "Start Date");
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        if (DATE_DIALOG == 1){
            // set start date
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            TextView tvStartDate = (TextView) findViewById(R.id.tvStartDate);
            tvStartDate.setText(currentDateString);
            updateStartDateTime(day, month, year, 0, 0, true);
        }
        else if (DATE_DIALOG == 2){
            // set end date
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            TextView tvEndDate = (TextView) findViewById(R.id.tvEndDate);
            tvEndDate.setText(currentDateString);
            updateEndDateTime(day, month, year, 0, 0, true);
        }
    }

    public Date getDateTimeFromPickers(int day, int month, int year, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        calendar.setTimeZone(tz);
        return calendar.getTime();
    }

    public void updateStartDateTime(int day, int month, int year, int hour, int minute, boolean date){
        if (date){
            startDay = day;
            startMonth = month;
            startYear = year;
        } else {
            startHour = hour;
            startMinute = minute;
        }
    }

    public void updateEndDateTime(int day, int month, int year, int hour, int minute, boolean date){
        if (date){
            endDay = day;
            endMonth = month;
            endYear = year;
        } else {
            endHour = hour;
            endMinute = minute;
        }
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public boolean isValidDateWindow(Date start, Date end){
        // ensures startDateTime < endDateTime
        int comp = start.compareTo(end);
        if (comp >= 0){
            // dates cannot be the same (or else zero time)
            return false;
        } else {
            // comp < 0 means start <  end
            return checkHourDiff(start, end);
        }
    }

    public boolean checkHourDiff(Date start, Date end){
        long diffMillis = end.getTime() - start.getTime();
        long diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis);
        Log.i(TAG, "difference in hours: " + diffHours);
        return true;
    }

    private void saveEvent(Date start, Date end){
        Log.i(TAG, "Saving event");
        Event event = new Event();
        event.setStart(start);
        event.setEnd(end);
        event.setRenter(ParseUser.getCurrentUser());
        event.setCar(car);
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
                    Toast.makeText(ScheduleTimesActivity.this, "Could not save", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.i(TAG, "Event was saved to backend");
                    Toast.makeText(ScheduleTimesActivity.this, "Event was saved", Toast.LENGTH_SHORT).show();
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

    public String formatDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        return "" + month + "/" + day + "/" + year;
    }
}