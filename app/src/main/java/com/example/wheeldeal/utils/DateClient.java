package com.example.wheeldeal.utils;

import android.util.Log;

import com.example.wheeldeal.models.DateRangeHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @brief Provides date-related methods that other classes can utilize.
 */
public class DateClient {
    public static final String TAG = "DateClient";

    public DateClient(){}

    /**
     * @brief Converts a date object into a more readable string format to show to the user
     * @param date A date object to convert to a string
     * @return String date formated as M/D/Y
     */
    public String formatDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        return "" + (month + 1) + "/" + (day) + "/" + year;
    }

    public String formatDate(int year, int month, int day){
        return (month + 1) + "/" + (day + 1) + "/" + year;
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


    public boolean EventConflictExists(Date start, Date end, ArrayList<DateRangeHolder> rangeHolders) {
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
                Log.i(TAG, "eventStart: " + formatDate(eventStartYear, eventStartMonth, eventStartDate));
                Log.i(TAG, "eventEnd: " + formatDate(eventEndYear, eventEndMonth, eventEndDate));
                Log.i(TAG, "start: " + formatDate(startYear, startMonth, startDate));
                return true;
            }

            // check if end is in between eventStart and eventEnd
            if (isDateBefore(eventStartYear, eventStartMonth, eventStartDate, endYear, endMonth, endDate) <= 0
                    && isDateBefore(endYear, endMonth, endDate, eventEndYear, eventEndMonth, eventEndDate) <= 0){
                Log.i(TAG, "eventStart: " + formatDate(eventStartYear, eventStartMonth, eventStartDate));
                Log.i(TAG, "eventEnd: " + formatDate(eventEndYear, eventEndMonth, eventEndDate));
                Log.i(TAG, "end: " + formatDate(endYear, endMonth, endDate));
                return true;
            }
        }
        return false;
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

    public int getDuration(Date start, Date end){
        long diff = end.getTime() - start.getTime();
        Log.i(TAG, "Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
        int days = (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
        return days;
    }

}
