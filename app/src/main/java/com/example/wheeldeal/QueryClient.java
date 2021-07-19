package com.example.wheeldeal;

import android.util.Log;

import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.Event;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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

    public void fetchCars(FindCallback<Car> callback, boolean isAll){
        Log.i(TAG, "fetching all cars");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        if (!isAll){
            query.whereEqualTo(Car.KEY_OWNER, ParseUser.getCurrentUser());
        }
        setCarQuery(query, callback);

    }

    public void setCarQuery(ParseQuery<Car> query, FindCallback<Car> callback){
        query.include(Car.KEY_OWNER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(callback);
    }
}
