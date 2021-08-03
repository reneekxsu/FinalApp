package com.example.wheeldeal.models;

import android.app.Application;
import android.util.Log;

import com.example.wheeldeal.BuildConfig;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ParseApplication extends Application {

    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    public static final String CLIENT_KEY = BuildConfig.CLIENT_KEY;
    public static final String TAG = "ParseApplication";
    public static ArrayList<String> models;
    public static HashMap<String, String> hmMakeToModel;
    List<CarModelList> allCars;
    int skip = 0;
    // Pair: make, model

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Car.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(CarModelList.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                .build()
        );

        // Creating an empty HashMap
        hmMakeToModel = new HashMap<String, String>();

        models = new ArrayList<>();
        allCars = new ArrayList<>();
        ParseQuery<CarModelList> query = ParseQuery.getQuery(CarModelList.class);
        query.setLimit(1000);
        query.findInBackground(getAllObjects());

    }

    private FindCallback<CarModelList> getAllObjects() {
        return new FindCallback<CarModelList>(){
            @Override
            public void done(List<CarModelList> cars, ParseException e) {
                int limit = 1000;
                allCars.addAll(cars);
                if (cars.size() == limit){
                    skip += limit;
                    ParseQuery query = ParseQuery.getQuery(CarModelList.class);
                    query.setSkip(skip);
                    query.setLimit(limit);
                    query.findInBackground(getAllObjects());
                } else {
                    filterList(allCars);
                }
            }
        };
    }

    private void filterList(List<CarModelList> cars) {
        for (CarModelList c : cars){
            models.add(c.getModel());
            hmMakeToModel.put(c.getModel(), c.getMake());
        }
        Log.i("ModelClient", "list size: " + cars.size());
        HashSet<String> uniqueMakes = new HashSet<String>();
        uniqueMakes.addAll(models);
        models.clear();
        models.addAll(uniqueMakes);
        Log.i(TAG, "model unique size: " + models.size());
        Log.i(TAG, "finished filtering");
    }

    public ArrayList<String> getModels(){
        return models;
    }

    public HashMap<String, String> getHashMapModelMake(){
        return hmMakeToModel;
    }

}
