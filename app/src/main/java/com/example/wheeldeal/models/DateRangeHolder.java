package com.example.wheeldeal.models;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class DateRangeHolder {
    Date start;
    Date end;
    public DateRangeHolder(){};
    public DateRangeHolder(Date start, Date end){
        this.start = start;
        this.end = end;
    }
    public Date getStart(){
        return start;
    }
    public Date getEnd(){
        return end;
    }
}
