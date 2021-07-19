package com.example.wheeldeal.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Event")
public class Event extends ParseObject {
    // keys: start, end, renter, car, rentType
    public static final String KEY_START = "start"; // Date
    public static final String KEY_END = "end"; // Date
    public static final String KEY_RENTER = "renter"; // Pointer to User
    public static final String KEY_CAR = "car"; // Pointer to Car
    public static final String KEY_RENTTYPE = "rentType"; // Number

    // start date getter and setter
    public Date getStart(){
            return getDate(KEY_START);
    }

    public void setStart(Date start){
        put(KEY_START,start);
    }

    // end date getter and setter
    public Date getEnd(){
        return getDate(KEY_END);
    }

    public void setEnd(Date end){
        put(KEY_END,end);
    }

    // renter getter and setter
    public ParseUser getRenter(){
        return getParseUser(KEY_RENTER);
    }

    public void setRenter(ParseUser renter){
        put(KEY_RENTER,renter);
    }

    // car getter and setter
    public Car getCar(){
        return (Car) getParseObject(KEY_CAR);
    }

    public void setCar(Car car){
        put(KEY_CAR,car);
    }

    // rentType getter and setter
    public Number getRentType() {
        return getNumber(KEY_RENTTYPE);
    }

    public void setRentType(Number rentType) {
        put(KEY_RENTTYPE, rentType);
    }
}
