package com.example.wheeldeal.utils;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.wheeldeal.models.CarModelList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ModelClient {
    ArrayList<String> models;
    ArrayAdapter<String> adapter;
    List<CarModelList> allCars;
    int skip = 0;
    public ModelClient(ArrayAdapter<String> adapter){
        this.adapter = adapter;
        models = new ArrayList<>();
        allCars = new ArrayList<>();
        ParseQuery<CarModelList> query = ParseQuery.getQuery(CarModelList.class);
        query.setLimit(1000);
        query.findInBackground(getAllObjects());

    }

    private FindCallback<CarModelList> getAllObjects() {
        return new FindCallback<CarModelList>(){
//            query.findInBackground(new FindCallback<CarModelList>() {
//                @Override
//                public void done(List<CarModelList> cars, ParseException e) {
//                    allCars.addAll(cars);
//                    filterList(cars);
//                }
//            });
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
        }
        Log.i("ModelClient", "list size: " + cars.size());
        HashSet<String> uniqueMakes = new HashSet<String>();
        uniqueMakes.addAll(models);
        models.clear();
        models.addAll(uniqueMakes);
        Log.i("ModelClient", "model unique size: " + models.size());
        adapter.clear();
        adapter.addAll(models);
        adapter.notifyDataSetChanged();
        Log.i("ModelClient", "finished filtering");
    }

    public ArrayList<String> getModels(){
        return models;
    }


}
