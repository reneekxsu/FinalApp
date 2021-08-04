package com.example.wheeldeal.utils;

import android.util.Log;

import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.CarScore;
import com.example.wheeldeal.models.Event;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Overview:
 *
 */
public class QueryClient {
    public static final String TAG = "QueryClient";
    public QueryClient(){};
    public void fetchAllEvents(FindCallback<Event> callback){
        Log.i(TAG, "fetching all events");
        // query events in which user is renter OR owner
        ParseQuery<Event> queryRenter = ParseQuery.getQuery(Event.class);
        queryRenter.whereEqualTo(Event.KEY_RENTER, ParseUser.getCurrentUser());

        ParseQuery<Event> queryOwner = ParseQuery.getQuery(Event.class);
        ParseQuery<Car> innerQuery = ParseQuery.getQuery(Car.class);
        innerQuery.whereEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        queryOwner.whereMatchesQuery(Event.KEY_CAR, innerQuery);

        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();
        queries.add(queryRenter);
        queries.add(queryOwner);
        ParseQuery<Event> query = ParseQuery.or(queries);

        query.addAscendingOrder(Event.KEY_START);
        query.setLimit(20);
        query.include(Event.KEY_CAR);
        query.include(Event.KEY_RENTER);
        query.findInBackground(callback);
    }

    public void fetchAllFutureEvents(FindCallback<Event> callback){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date d = c.getTime();
        Log.i(TAG, "fetching all future events");
        // query events in which user is renter OR owner
        ParseQuery<Event> queryRenter = ParseQuery.getQuery(Event.class);
        queryRenter.whereEqualTo(Event.KEY_RENTER, ParseUser.getCurrentUser());
        queryRenter.whereGreaterThanOrEqualTo(Event.KEY_END, d);

        ParseQuery<Event> queryOwner = ParseQuery.getQuery(Event.class);
        ParseQuery<Car> innerQuery = ParseQuery.getQuery(Car.class);
        innerQuery.whereEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        queryOwner.whereMatchesQuery(Event.KEY_CAR, innerQuery);
        queryOwner.whereGreaterThanOrEqualTo(Event.KEY_END, d);

        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();
        queries.add(queryRenter);
        queries.add(queryOwner);
        ParseQuery<Event> query = ParseQuery.or(queries);

        query.addAscendingOrder(Event.KEY_START);
        query.setLimit(20);
        query.include(Event.KEY_CAR);
        query.include(Event.KEY_RENTER);
        query.findInBackground(callback);
    }

    public void fetchAllPastEvents(FindCallback<Event> callback){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date d = c.getTime();
        Log.i(TAG, "fetching all past events");
        // query events in which user is renter OR owner
        ParseQuery<Event> queryRenter = ParseQuery.getQuery(Event.class);
        queryRenter.whereEqualTo(Event.KEY_RENTER, ParseUser.getCurrentUser());
        queryRenter.whereLessThan(Event.KEY_END, d);

        ParseQuery<Event> queryOwner = ParseQuery.getQuery(Event.class);
        ParseQuery<Car> innerQuery = ParseQuery.getQuery(Car.class);
        innerQuery.whereEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        queryOwner.whereMatchesQuery(Event.KEY_CAR, innerQuery);
        queryOwner.whereLessThan(Event.KEY_END, d);

        List<ParseQuery<Event>> queries = new ArrayList<ParseQuery<Event>>();
        queries.add(queryRenter);
        queries.add(queryOwner);
        ParseQuery<Event> query = ParseQuery.or(queries);

        query.addAscendingOrder(Event.KEY_START);
        query.setLimit(20);
        query.include(Event.KEY_CAR);
        query.include(Event.KEY_RENTER);
        query.findInBackground(callback);
    }


    public void fetchCars(FindCallback<Car> callback, boolean isAllNonOwner, boolean isAll){
        Log.i(TAG, "fetching all cars");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        if (!isAllNonOwner){
            query.whereEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        } else if (!isAll){
            query.whereNotEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        }
        setCarQuery(query, callback);
    }

    public void fetchCarsWithAddress(FindCallback<Car> callback, String address){
        Log.i(TAG, "fetching all cars with address");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.whereEqualTo(Car.KEY_ADDRESS, address);
        query.whereNotEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        setCarQuery(query, callback);
    }

    public void fetchCarsBySeats(FindCallback<Car> callback){
        Log.i(TAG, "fetching cars ordered by num passengers");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.include(Car.KEY_OWNER);
        query.setLimit(20);
        query.addDescendingOrder("passengers");
        query.whereNotEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        query.findInBackground(callback);
    }

    public void fetchCarsByPrice(FindCallback<Car> callback){
        Log.i(TAG, "fetching cars ordered by num passengers");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.include(Car.KEY_OWNER);
        query.setLimit(20);
        query.addAscendingOrder("rate");
        query.whereNotEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        query.findInBackground(callback);
    }

    public void setCarQuery(ParseQuery<Car> query, FindCallback<Car> callback){
        query.include(Car.KEY_OWNER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(callback);
    }

    public void fetchAllCarEvents(FindCallback<Event> callback, Car car){
        Log.i(TAG, "fetching all events for this car");
        ParseQuery<Event> query = ParseQuery.getQuery(Event.class);
        query.whereEqualTo(Event.KEY_CAR, car);
        query.addAscendingOrder(Event.KEY_START);
        query.setLimit(20);
        query.include(Event.KEY_CAR);
        query.include(Event.KEY_RENTER);
        query.findInBackground(callback);
    }

    public void fetchCarByProximity(FindCallback<Car> callback, ParseGeoPoint point, double maxDistance){
        Log.i(TAG, "fetching cars by proximity");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.include(Car.KEY_OWNER);
        query.whereWithinMiles("addressGeoPoint", point, maxDistance);
        query.whereNotEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        query.findInBackground(callback);
    }

    public void fetchCarsByFilter(FindCallback<Car> callback, ParseGeoPoint point,
                                  double maxDistance, String model, String make){
        Log.i(TAG, "fetching cars by proximity, model, and make");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.include(Car.KEY_OWNER);
        if (point != null){
            query.whereWithinMiles("addressGeoPoint", point, maxDistance);
        } else {
            query.addDescendingOrder("createdAt");
        }
        if (!make.isEmpty()){
            query.whereEqualTo(Car.KEY_MAKE, make);
        }
        if (!model.isEmpty()){
            query.whereEqualTo(Car.KEY_MODEL, model);
        }
        query.whereNotEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        query.findInBackground(callback);
    }

    public void saveCar(String description, ParseUser currentUser, File photoFile, String rate,
                        String model, String name, String make, String year, String passengers,
                        String size, String address, ParseGeoPoint gp, SaveCallback callback, boolean newCar) {
        Log.i(TAG, "saving car");
        Car car = new Car();
        saveCarFields(car, description, currentUser, new ParseFile(photoFile), rate, model, name, make, year,
                passengers, size, address, gp, callback, newCar);
    }

    public void saveCarFields(Car car, String description, ParseUser currentUser, ParseFile image, String rate,
                              String model, String name, String make, String year, String passengers,
                              String size, String address, ParseGeoPoint gp, SaveCallback callback, boolean newCar){
        car.setDescription(description);
        car.setOwner(currentUser);
        car.setImage(image);
        car.setRate(Integer.parseInt(rate));
        car.setModel(model);
        car.setName(name);
        car.setMake(make);
        car.setYear(year);
        car.setPassengers(passengers);
        car.setSizeType(size);
        car.setAddress(address);
        car.setAddressGeoPoint(gp);
        if (newCar){
            car.setEventCount(0);
        }
        CarculatorClient carculatorClient = new CarculatorClient(make, year, passengers);
        int flagLux = carculatorClient.getLux(make);
        Log.i(TAG, "flagLux of car: " + "make " + make + " " + flagLux);
        CarScore carScore = new CarScore(Integer.parseInt(year), Integer.parseInt(passengers), flagLux);
        car.setScore(carScore.getScore());
        if (callback == null){
            car.saveInBackground();
        } else {
            car.saveInBackground(callback);
        }
    }

    public void saveEvent(Date start, Date end, Car car, boolean userIsCustomer, SaveCallback callback){
        Log.i(TAG, "Saving event");
        Event event = new Event();
        event.setStart(start);
        event.setEnd(end);
        event.setRenter(ParseUser.getCurrentUser());
        event.setCar(car);
        event.setPrice(car.getRate().toString());
        int rentType = 0;
        if (userIsCustomer){
            rentType = 1;
        }
        event.setRentType(rentType);
        event.saveInBackground(callback);
        if (userIsCustomer){
            car.setEventCount((int)car.getEventCount() + 1);
        }
        car.saveInBackground();
    }


    public void deleteAssociatedEvents(Car car, List<Event> allEvents){
        for (Event event : allEvents){
            event.deleteInBackground();
            if (car.getOwner().getObjectId() != ParseUser.getCurrentUser().getObjectId()){
                car.setEventCount((int)car.getEventCount() - 1);
            }
        }
    }

    public void deleteCar(Car car, DeleteCallback callback){
        car.deleteInBackground(callback);
    }

    public void fetchUserDetails(ParseUser user, GetCallback callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        // Specify the object id
        query.getInBackground(user.getObjectId(), callback);
    }

    public void signUpUser(String username, String password, SignUpCallback callback){
        // Create the new ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.put("avgScore", 0);
        user.put("carsBooked", 0);
        // Try to sign user up
        user.signUpInBackground(callback);
    }

    public void logInUser(String username, String password, LogInCallback callback){
        ParseUser.logInInBackground(username, password, callback);
    }

    public void saveUserDetails(ParseUser currentUser, String email, String address){
        currentUser.put("email", email);
        currentUser.put("address", address);
        currentUser.saveInBackground();
    }
}
