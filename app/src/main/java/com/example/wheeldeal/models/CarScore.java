package com.example.wheeldeal.models;

import android.util.Log;

public class CarScore {
    int year;
    int passengers;
    int flagLux; // 0 = normal, 1 = luxury, 2 = ultimate luxury
    public CarScore(int year, int passengers, int flagLux){
        this.year = year;
        this.passengers = passengers;
        this.flagLux = flagLux;
    }

    public double getScore(){
        return weighYear(year) + 11 * (0.94 * (passengers - 1)) + 10 * flagLux;
    }

    public double weighYear(int year){
        // sigmoid
        int n = 2021 - year;
        double scaled = (23.0 / (1 + Math.exp(0.34 * (n - 12)))) - 1.7;
//        double scaled = -1 * Math.pow(1.3, n) + 20;
        Log.i("CarScore", "year scaled to: " + scaled);
        return 3.5 * scaled;
    }

}
