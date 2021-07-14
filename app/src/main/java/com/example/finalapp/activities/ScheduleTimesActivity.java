package com.example.finalapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.finalapp.R;
import com.example.finalapp.fragments.DatePickerFragment;
import com.example.finalapp.fragments.TimePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleTimesActivity extends AppCompatActivity implements DatePickerFragment.DatePickerFragmentListener,
        TimePickerFragment.TimePickerFragmentListener{

    Button btnStartDate, btnEndDate, btnStartTime, btnEndTime;
    FragmentManager fm = getSupportFragmentManager();
    int DATE_DIALOG = 0;
    int TIME_DIALOG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_times);

        btnStartDate = (Button) findViewById(R.id.btnStartDate);
        btnEndDate = (Button) findViewById(R.id.btnEndDate);
        btnStartTime = (Button) findViewById(R.id.btnStartTime);
        btnEndTime = (Button) findViewById(R.id.btnEndTime);

//        SELECT A START DATE
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATE_DIALOG = 1;
                openDateDialog();
            }
        });
//        SELECT A STOP DATE
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATE_DIALOG = 2;
                openDateDialog();
            }
        });
        //        SELECT A START TIME
        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TIME_DIALOG = 1;
                openTimeDialog();
            }
        });
//        SELECT A STOP TIME
        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TIME_DIALOG = 2;
                openTimeDialog();
            }
        });
    }
    public void openDateDialog(){
        DatePickerFragment datepickDialog = new DatePickerFragment();
        datepickDialog.show(fm, "Start Date");
    }

    public void openTimeDialog(){
        TimePickerFragment timepickDialog = new TimePickerFragment();
        timepickDialog.show(fm, "Start Time");
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        if (DATE_DIALOG == 1){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            TextView tvStartDate = (TextView) findViewById(R.id.tvStartDate);
            tvStartDate.setText(currentDateString);
        }
        else if (DATE_DIALOG == 2){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);
            String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            TextView tvEndDate = (TextView) findViewById(R.id.tvEndDate);
            tvEndDate.setText(currentDateString);
        }
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        if (TIME_DIALOG == 1){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
            TextView tvStartTime = (TextView) findViewById(R.id.tvStartTime);
            tvStartTime.setText(currentTimeString);
        }
        else if (TIME_DIALOG == 2){
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
            TextView tvEndTime = (TextView) findViewById(R.id.tvEndTime);
            tvEndTime.setText(currentTimeString);
        }
    }

    public Date getDateTimeFromPickers(int day, int month, int year, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTime();
    }
}