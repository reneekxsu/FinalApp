package com.example.finalapp;

import org.parceler.Parcel;

@Parcel
public class ParcelableCar {
    public Car c;
    public ParcelableCar(){}

    public ParcelableCar(Car c){
        this.c = c;
    }

    public Car getCar(){
        return c;
    }
}
