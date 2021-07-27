package com.example.wheeldeal.utils;

import java.util.Calendar;
import java.util.Date;

public class DateClient {
    public DateClient(){}
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

}
