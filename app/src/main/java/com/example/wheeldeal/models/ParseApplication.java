package com.example.wheeldeal.models;

import android.app.Application;
import android.util.Log;
import android.view.View;

import com.example.wheeldeal.BuildConfig;
import com.example.wheeldeal.activities.CarDetailsActivity;
import com.example.wheeldeal.fragments.ViewMyCarsFragment;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ParseApplication extends Application {

    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    public static final String CLIENT_KEY = BuildConfig.CLIENT_KEY;
    public static final String TAG = "ParseApplication";
    public static ArrayList<String> models;
    public static HashMap<String, String> hmModelToMake;
    public static HashMap<String, ArrayList> hmMakeToModels;
    List<CarModelList> allCars;
    int skip = 0;
    public static boolean isDataReady;
    // Pair: make, model

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        isDataReady = false;

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
        hmModelToMake = new HashMap<String, String>();
        hmMakeToModels = new HashMap<String, ArrayList>();

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
            hmModelToMake.put(c.getModel(), c.getMake());
        }
        Log.i("ModelClient", "list size: " + cars.size());
        HashSet<String> uniqueMakes = new HashSet<String>();
        uniqueMakes.addAll(models);
        models.clear();
        models.addAll(uniqueMakes);
        for (String model : models){
            String make = hmModelToMake.get(model);
            if (hmMakeToModels.get(make) == null){
                hmMakeToModels.put(make, new ArrayList<String>(Arrays.asList(model)));
            } else {
                ArrayList<String> modelsMapped = hmMakeToModels.get(make);
                modelsMapped.add(model);
            }
        }
        Log.i(TAG, "model unique size: " + models.size());
        Log.i(TAG, "hm model to make size: " + hmModelToMake.size());
        Log.i(TAG, "hm make to models size: " + hmMakeToModels.size());
        Log.i(TAG, "finished filtering");
        isDataReady = true;
        if (ViewMyCarsFragment.fabAddCar != null){
            ViewMyCarsFragment.fabAddCar.show();
        }
        if (CarDetailsActivity.ibtnEdit != null){
            CarDetailsActivity.ibtnEdit.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<String> getModels(){
        return models;
    }

    public HashMap<String, String> getHashMapModelMake(){
        return hmModelToMake;
    }
    
    public HashMap<String, ArrayList> getHashMapMakeModel(){
        return hmMakeToModels;
    }

}
