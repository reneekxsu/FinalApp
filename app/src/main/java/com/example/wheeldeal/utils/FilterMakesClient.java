package com.example.wheeldeal.utils;

import android.util.Log;
import android.view.View;

import com.example.wheeldeal.activities.CarDetailsActivity;
import com.example.wheeldeal.fragments.HomeFragment;
import com.example.wheeldeal.fragments.ViewMyCarsFragment;
import com.example.wheeldeal.models.CarModelList;
import com.example.wheeldeal.ParseApplication;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FilterMakesClient {
    public static final String TAG = "FilterMakesClient";
    int skip;
    public static ArrayList<String> models;
    public static HashMap<String, String> hmModelToMake;
    public static HashMap<String, ArrayList> hmMakeToModels;
    List<CarModelList> allCars;
    public FilterMakesClient(){
        skip = 0;
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
        ParseApplication.isDataReady = true;
        if (ViewMyCarsFragment.fabAddCar != null){
            ViewMyCarsFragment.fabAddCar.show();
        }
        if (CarDetailsActivity.ibtnEdit != null){
            CarDetailsActivity.ibtnEdit.setVisibility(View.VISIBLE);
        }
        if (HomeFragment.filters != null){
            HomeFragment.filters.setVisible(true);
        }
    }
}
