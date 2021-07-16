package com.example.finalapp.activities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MondaysOutValidator implements CalendarConstraints.DateValidator {

    int mYear, mMonth, mDayOfWeek;

    MondaysOutValidator(int year, int month, int dayOfWeek) {
        mYear = year;
        mMonth = month;
        mDayOfWeek = dayOfWeek;
    }

    MondaysOutValidator(Parcel parcel) {
        mYear = parcel.readInt();
        mMonth = parcel.readInt();
        mDayOfWeek = parcel.readInt();
    }


    @Override
    public boolean isValid(long date) {

        List<Integer> allXDayOfMonth = getAllXDayOfMonth(mYear, mMonth, mDayOfWeek);

        boolean isValidDays = false;
        for (int xDay : allXDayOfMonth) {
            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            ArrayList<Long> minDate = new ArrayList<>();
            ArrayList<Long> maxDate = new ArrayList<>();
            calendarStart.set(mYear, mMonth, xDay - 1);
            calendarEnd.set(mYear, mMonth, xDay);
            minDate.add(calendarStart.getTimeInMillis());
            maxDate.add(calendarEnd.getTimeInMillis());
            isValidDays = isValidDays || !(minDate.get(0) > date || maxDate.get(0) < date);
        }

        return !isValidDays;

    }


    private static int getFirstXDayOfMonth(int year, int month, int dayOfWeek) {
        Calendar cacheCalendar = Calendar.getInstance();
        cacheCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        cacheCalendar.set(Calendar.MONTH, month);
        cacheCalendar.set(Calendar.YEAR, year);
        return cacheCalendar.get(Calendar.DATE);
    }

    private static List<Integer> getAllXDayOfMonth(int year, int month, int dayOfWeek) {
        final int ONE_WEEK = 7;
        int firstDay = getFirstXDayOfMonth(year, month, dayOfWeek);
        List<Integer> xDays = new ArrayList<>();
        xDays.add(firstDay);

        Calendar calendar = new GregorianCalendar(year, month, firstDay);
        calendar.add(Calendar.DAY_OF_MONTH, ONE_WEEK); // adding 1 Week
        while (calendar.get(Calendar.MONTH) == month) {
            xDays.add(calendar.get(Calendar.DAY_OF_MONTH));
            calendar.add(Calendar.DAY_OF_MONTH, ONE_WEEK); // adding 1 Week
        }

        return xDays;
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

    public static final Parcelable.Creator<MondaysOutValidator> CREATOR = new Parcelable.Creator<MondaysOutValidator>() {

        @Override
        public MondaysOutValidator createFromParcel(Parcel parcel) {
            return new MondaysOutValidator(parcel);
        }

        @Override
        public MondaysOutValidator[] newArray(int size) {
            return new MondaysOutValidator[size];
        }
    };

}

