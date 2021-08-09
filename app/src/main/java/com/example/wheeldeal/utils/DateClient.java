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

    // Empty constructor
    public DateClient(){}

    /**
     * @brief Converts a date object into a more readable string format to show to the user
     * @param date A date object to convert to a string
     * @return String date formatted as M/D/Y
     */
    public String formatDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);
        // Increment month by 1 since it is zero-indexed
        return "" + (month + 1) + "/" + (day) + "/" + year;
    }

    /**
     * @brief Converts the year, month, and day extracted from a Calendar Date to a string format
     * @param year Calendar year
     * @param month Calendar month
     * @param day Calendar day
     * @return String date formatted as M/D/Y
     */
    public String formatDate(int year, int month, int day){
        // Increment month and day by 1 to avoid off-by-one error
        return (month + 1) + "/" + (day + 1) + "/" + year;
    }

    /**
     * @brief Compares dates based on the year, month, and day of month given
     *
     *
     *
     * @param year1
     * @param month1
     * @param day1
     * @param year2
     * @param month2
     * @param day2
     * @return Integer indicating result of comparison:
     *         0, if the dates are equal
     *         1, if the first date is after the second date
     *         2, if the first date is before the second date
     */
    public int compareDates(int year1, int month1, int day1, int year2, int month2, int day2){
        if (year1 == year2){
            // Checks if same year
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
            // First date has earlier year
            return -1;
        } else {
            // First date has later year
            return 1;
        }
    }


    /**
     * @brief Checks if a proposed date range will overlap with any other date ranges specified in
     *        a DateRangeHolder ArrayList
     *
     * In other words, we want to ensure that a date range will not conflict with other Event date
     * ranges. An assumption that we make is that all the date ranges in the rangeHolders array are
     * already not overlapping/conflicting. Then, we only have to compare each valid date range with
     * the proposed date range. A date range is considered overlapping if either the start Date or
     * end Date is anytime in between the valid date range's start and end dates.
     *
     * @param start Start date of range
     * @param end End date of range
     * @param rangeHolders ArrayList of other date ranges
     * @return Whether there was a date range conflict
     */
    public boolean doesEventConflictExist(Date start, Date end,
                                          ArrayList<DateRangeHolder> rangeHolders) {
        for (DateRangeHolder ranges : rangeHolders){
            // Extract start and end date of each range
            Date eventStart = ranges.getStart();
            Date eventEnd = ranges.getEnd();

            // Extract month, day, and year from the four dates we will use to compare
            int eventStartMonth = getMonth(eventStart);
            int eventStartDay = getDay(eventStart);
            int eventStartYear = getYear(eventStart);
            int eventEndMonth = getMonth(eventEnd);
            int eventEndDay = getDay(eventEnd);
            int eventEndYear = getYear(eventEnd);
            int startMonth = getMonth(start);
            int startDate = getDay(start);
            int startYear = getYear(start);
            int endMonth = getMonth(end);
            int endDate = getDay(end);
            int endYear = getYear(end);

            // Check if start is in between eventStart and eventEnd
            if (compareDates(eventStartYear, eventStartMonth, eventStartDay, startYear, startMonth,
                    startDate) <= 0
                    && compareDates(startYear, startMonth, startDate, eventEndYear, eventEndMonth,
                    eventEndDay) <= 0){
                Log.i(TAG, "eventStart: " + formatDate(eventStartYear, eventStartMonth,
                        eventStartDay));
                Log.i(TAG, "eventEnd: " + formatDate(eventEndYear, eventEndMonth,
                        eventEndDay));
                Log.i(TAG, "start: " + formatDate(startYear, startMonth, startDate));
                return true;
            }

            // Check if end is in between eventStart and eventEnd
            if (compareDates(eventStartYear, eventStartMonth, eventStartDay, endYear, endMonth,
                    endDate) <= 0
                    && compareDates(endYear, endMonth, endDate, eventEndYear, eventEndMonth,
                    eventEndDay) <= 0){
                Log.i(TAG, "eventStart: " + formatDate(eventStartYear, eventStartMonth,
                        eventStartDay));
                Log.i(TAG, "eventEnd: " + formatDate(eventEndYear, eventEndMonth,
                        eventEndDay));
                Log.i(TAG, "end: " + formatDate(endYear, endMonth, endDate));
                return true;
            }
        }

        // All comparisons passed, so the date range is valid
        return false;
    }

    /**
     * @brief Returns whether a date range is valid. Valid dates must have the end date be on the
     *        same day of OR after the start date
     * @param start Start date
     * @param end End date
     * @return Whether the date range is valid.
     */
    public boolean isValidDateWindow(Date start, Date end){
        // Ensures startDateTime <= endDateTime
        int comp = start.compareTo(end);
        // Usage: comp < 0 means start <  end
        if (comp > 0){
            // Checks if start date is after end date
            return false;
        } else {
            // Otherwise, date range is valid
            return true;
        }
    }

    /**
     * @brief Extract the month from a Date object
     * @param date The date we want to get the month from
     * @return Month of the Date object
     */
    public int getMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int month = c.get(Calendar.MONTH);
        return month;
    }

    /**
     * @brief Extract the day (of the month) from a Date object
     * @param date The date we want to get the day from
     * @return Day of the Date object
     */
    public int getDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * @brief Extract the year from a Date object
     * @param date The date we want to get the year from
     * @return Year of the Date object
     */
    public int getYear(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * @brief Calculates the number of days between a start and end date (inclusive)
     *        E.g. start = 1/3/21 and end = 1/5/21 yields a 3 day duration
     * @param start Start date
     * @param end End date
     * @return Number of days between start and end date
     */
    public int getDuration(Date start, Date end){
        long diff = end.getTime() - start.getTime();
        Log.i(TAG, "Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
        int days = (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
        return days;
    }

}
