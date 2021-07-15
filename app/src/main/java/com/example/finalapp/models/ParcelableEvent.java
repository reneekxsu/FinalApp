package com.example.finalapp.models;

import org.parceler.Parcel;

@Parcel
public class ParcelableEvent {
    public Event e;
    public ParcelableEvent(){}

    public ParcelableEvent(Event e){
        this.e = e;
    }

    public Event getEvent(){
        return e;
    }
}
