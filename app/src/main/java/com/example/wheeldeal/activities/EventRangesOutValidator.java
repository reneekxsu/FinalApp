package com.example.wheeldeal.activities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.utils.DateClient;
import com.google.android.material.datepicker.CalendarConstraints;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EventRangesOutValidator implements CalendarConstraints.DateValidator {

    int mYear, mMonth, mDayOfWeek;
    ArrayList<DateRangeHolder> rangeHolders;
    public static final String TAG = "EventRangesOutValidator";
    DateClient dateClient;

    EventRangesOutValidator(int year, int month, int dayOfWeek, ArrayList<DateRangeHolder> rangeHolders) {
        mYear = year;
        mMonth = month;
        mDayOfWeek = dayOfWeek;
        this.rangeHolders = rangeHolders;
        dateClient = new DateClient();
    }

    EventRangesOutValidator(Parcel parcel) {
        mYear = parcel.readInt();
        mMonth = parcel.readInt();
        mDayOfWeek = parcel.readInt();
    }


    @Override
    public boolean isValid(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        Date d = c.getTime();

        for (DateRangeHolder range : rangeHolders){
            int eventStartMonth = getMonth(range.getStart());
            int eventStartDate = getDay(range.getStart());
            int eventStartYear = getYear(range.getStart());
            int eventEndMonth = getMonth(range.getEnd());
            int eventEndDate = getDay(range.getEnd());
            int eventEndYear = getYear(range.getEnd());
            int startMonth = getMonth(d);
            int startDate = getDay(d);
            int startYear = getYear(d);

            // check if start is in between eventStart and eventEnd
            if (isDateBefore(eventStartYear, eventStartMonth, eventStartDate, startYear, startMonth, startDate) <= 0
                    && isDateBefore(startYear, startMonth, startDate, eventEndYear, eventEndMonth, eventEndDate) <= 0){
                Log.i(TAG, "eventStart: " + dateClient.formatDate(eventStartYear, eventStartMonth, eventStartDate));
                Log.i(TAG, "eventEnd: " + dateClient.formatDate(eventEndYear, eventEndMonth, eventEndDate));
                Log.i(TAG, "start: " + dateClient.formatDate(startYear, startMonth, startDate));
                return false;
            }
        }

        return true;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mYear);
        dest.writeInt(mMonth);
        dest.writeInt(mDayOfWeek);
    }

    public static final Parcelable.Creator<EventRangesOutValidator> CREATOR = new Parcelable.Creator<EventRangesOutValidator>() {

        @Override
        public EventRangesOutValidator createFromParcel(Parcel parcel) {
            return new EventRangesOutValidator(parcel);
        }

        @Override
        public EventRangesOutValidator[] newArray(int size) {
            return new EventRangesOutValidator[size];
        }
    };

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

