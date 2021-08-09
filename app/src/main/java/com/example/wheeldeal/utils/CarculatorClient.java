package com.example.wheeldeal.utils;

import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.CarScore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @brief All car calculator-related logic is done in this class.
 */
public class CarculatorClient {
    public static final String TAG = "CarculatorClient";
    ArrayList<Car> allCars;
    ArrayList<String> ultLuxury;
    ArrayList<String> luxury;
    ArrayList<Double> x;
    ArrayList<Double> y;
    ArrayList<Double> scoreX;
    ArrayList<Double> daysY;
    String make, year, passengers;
    CarScore thisCar;

    /**
     * @brief Constructs a CarculatorClient object, based on a specified make, year, and number of
     *        passengers
     * @param make Make of a car
     * @param year Year of a car
     * @param passengers Number of passengers in a car
     */
    public CarculatorClient(String make, String year, String passengers){
        allCars = new ArrayList<>();
        ultLuxury = new ArrayList<>();
        luxury = new ArrayList<>();
        x = new ArrayList<>();
        y = new ArrayList<>();
        scoreX = new ArrayList<>();
        daysY = new ArrayList<>();
        this.make = make;
        this.year = year;
        this.passengers = passengers;
        initLuxury();
    }

    /**
     * @brief Predicts the price of a car, given its year, make, and number of passengers
     * @return Price prediction, rounded to the nearest int
     */
    public int predictPricing() {
        int price;
        double score, prediction;
        for (Car car : allCars){
            x.add((double)car.getScore());
            y.add(Double.parseDouble(car.getRate().toString()));
        }
        LinearRegression lr = new LinearRegression(x,y);
        int flagLux = getLux(make);
        // Make a new score object given the current stats that the client has specified
        thisCar = new CarScore(Integer.parseInt(year),
                Integer.parseInt(passengers), flagLux);

        // Extract score and plug into linear regression model
        score = thisCar.getScore();
        prediction = lr.predict(score);

        // Weigh the score and prediction to get the predicted price
        price = scoreToPrice((0.6 * score + 0.4 * prediction));
        return price;
    }

    /**
     * @brief Predicts the number of days a car would be rented
     * @return Number of days a car is predicted to be rented per month, rounded to highest int
     */
    public int predictDays(){
        DateClient dateClient = new DateClient();
        Date currentDate = Calendar.getInstance().getTime();
        for (Car car : allCars){
            scoreX.add((double)car.getScore());
            int daysElapsed = dateClient.getDuration(car.getCreatedAt(), currentDate);
            double monthsElapsed = Math.ceil(daysElapsed / 30 + 1);
            daysY.add(Double.parseDouble(car.getEventCount().toString()) / monthsElapsed);
        }
        LinearRegression lr = new LinearRegression(scoreX,daysY);
        double prediction = lr.intercept() + lr.slope() * thisCar.getScore();
        if (prediction <= 0){
            prediction = (Math.abs(prediction) + 1) * 0.7;
        }
        if (prediction * 2 > 30){
            return (int)Math.ceil(prediction);
        }
        return (int)Math.ceil(prediction);
    }

    /**
     * @brief Calculates the luxury status of a car, given the make.
     *
     * Checks if the make matches any of the makes in the ultLuxury or luxury class variable
     * ArrayLists.
     *
     * @param make String specifying the make of a car, that we want to classify
     * @return Integer representing the luxury status of a car
     *         0, if the car is not classified as a luxury car
     *         1, if the car is classified as a luxury car
     *         2, if the car is classified as an "ultra" luxury car
     */
    public int getLux(String make){
        int flagLux = 0;
        for (String s : ultLuxury){
            if (make.equals(s)){
                flagLux = 2;
                break;
            }
        }
        if (flagLux == 0){
            for (String s : luxury){
                if (make.equals(s)){
                    flagLux = 1;
                    break;
                }
            }
        }
        return flagLux;
    }

    /**
     * @brief Initialize ultLuxury and luxury ArrayList class variables
     */
    public void initLuxury(){
        ultLuxury.addAll(List.of("Aston Martin", "Bentley", "Corvette", "Ferrari", "Lamborghini",
                                 "Maserati", "Mclaren", "Rolls Royce"));
        luxury.addAll(List.of("Audi", "BMW", "Land Rover", "Mercedes-Benz", "Range Rover",
                              "Tesla"));
    }

    /**
     * @brief Converts a car score into a price
     * @param score Double car score value
     * @return Integer price based on score
     */
    private int scoreToPrice(double score){
        return (int) score;
    }

    /**
     * @brief Add a list of Car objects to the allCars class variable
     * @param cars List of Car objects to add
     */
    public void updateAllCars(List<Car> cars){
        allCars.addAll(cars);
    }

    /**
     * @brief Remove a Car object from allCars class variable
     * @param car Car object to remove from allCars class variable
     */
    public void removeFromAllCars(Car car){
        allCars.remove(car);
    }

    /**
     * @brief Update the make class variable
     * @param make An updated string for the passengers class variable
     */
    public void updateMake(String make){
        this.make = make;
    }

    /**
     * @brief Update the year class variable
     * @param year An updated string for the passengers class variable
     */
    public void updateYear(String year){
        this.year = year;
    }

    /**
     * @brief Update the passengers class variable
     * @param passengers An updated string for the passengers class variable
     */
    public void updatePassengers(String passengers){
        this.passengers = passengers;
    }

}
