package com.example.finalapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalapp.R;
import com.example.finalapp.adapters.CarAdapter;
import com.example.finalapp.models.Car;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserCarFeedActivity extends AppCompatActivity {

    private RecyclerView rvCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar pb;
    public static final String TAG = "UserCarFeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_feed);

        pb = (ProgressBar) findViewById(R.id.pbUserLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeUserContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "refreshing");
                fetchOwnCars();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvCars = findViewById(R.id.rvCars);

        // initialize array for holding cars, and create CarAdapter
        allCars = new ArrayList<>();
        adapter = new CarAdapter(this, allCars);
        // set adapter on RV
        rvCars.setAdapter(adapter);
        // set layout manager on RV
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        // query cars
        fetchOwnCars();
    }

    private void fetchOwnCars() {
        Log.i(TAG, "fetching own cars");
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        query.include(Car.KEY_AUTHOR);
        query.whereEqualTo(Car.KEY_AUTHOR, ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                } else {
                    for (Car car : cars){
                        Log.i(TAG, "Car: " + car.getModel());
                    }
                    adapter.clear();
                    adapter.addAll(cars);
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
    }
}