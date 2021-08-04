package com.example.wheeldeal.models;

import android.util.Log;

import java.util.Calendar;

public class CarFeedScorePair {
    double score;
    double userScore;
    Car car;
    public CarFeedScorePair(Car car, double userScore){
        this.car = car;
        this.userScore = userScore;
        calculateScore();
    }

    private void calculateScore() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Log.i("CarFeedScorePair", "diff in scores: " + Math.abs((double)car.getScore() - userScore));
        score = ((Math.log10((int)car.getEventCount() + 2) +
                5 * Math.exp(-(year - Integer.parseInt(car.getYear()))))
                 + 300 / (Math.abs((double)car.getScore() - userScore)));
    }

    public Car getCar(){
        return car;
    }

    public double getScore(){
        return score;
    }
}
