package com.example.wheeldeal.models;

import java.util.Calendar;

public class CarFeedScorePair {
    double score;
    Car car;
    public CarFeedScorePair(Car car){
        this.car = car;
        calculateScore();
    }

    private void calculateScore() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        score = (Math.log10((int)car.getEventCount() + 2) +
                5 * Math.exp(-(year - Integer.parseInt(car.getYear()))));
    }

    public Car getCar(){
        return car;
    }

    public double getScore(){
        return score;
    }
}
