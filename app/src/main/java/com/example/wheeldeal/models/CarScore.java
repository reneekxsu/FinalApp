package com.example.wheeldeal.models;

import android.util.Log;

public class CarScore {
    int year;
    int passengers;
    int flagLux; // 0 = normal, 1 = luxury, 2 = ultimate luxury
    Car car;
    public CarScore(int year, int passengers, int flagLux){
        this.year = year;
        this.passengers = passengers;
        this.flagLux = flagLux;
        car = null;
    }

    public CarScore(int year, int passengers, int flagLux, Car car){
        this.year = year;
        this.passengers = passengers;
        this.flagLux = flagLux;
        this.car = car;
    }

    public double getScore(){
        return weighYear(year) + 11 * (0.98 * (passengers - 1)) + (7 * flagLux) * (Math.pow(3.5,(flagLux + 1)));
    }

    public double weighYear(int year){
        // sigmoid
        int n = 2021 - year;
        double scaled = (20 / (1 + Math.exp(0.3 * (n - 9)))) + 3;
        Log.i("CarScore", "year scaled to: " + scaled);
        return 4 * scaled;
    }

    public Car getCar(){
        return car;
    }

}
