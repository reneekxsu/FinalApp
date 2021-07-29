package com.example.wheeldeal.utils;

import com.google.android.gms.maps.model.Marker;

public class MarkerCarCountHolder {
    Marker marker;
    int count;
    public MarkerCarCountHolder(Marker marker, int count){
        this.marker = marker;
        this.count = count;
    }
    public void incrementCount(){
        count++;
    }
    public Marker getMarker(){
        return marker;
    }
    public int getCount(){
        return count;
    }
}
