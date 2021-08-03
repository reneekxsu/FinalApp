package com.example.wheeldeal.models;

public class CarFeedScorePair {
    double score;
    Car car;
    public CarFeedScorePair(Car car){
        this.car = car;
        calculateScore();
    }

    private void calculateScore() {
        score = (Math.log10((int)car.getEventCount() + 2));
    }

    public Car getCar(){
        return car;
    }

    public double getScore(){
        return score;
    }
}
