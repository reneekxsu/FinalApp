package com.example.wheeldeal.oldclasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.AddOwnCarActivity;
import com.example.wheeldeal.adapters.CarAdapter;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class UserCarFeedActivity extends AppCompatActivity {

    private RecyclerView rvCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar pb;
    private FloatingActionButton fabAddCar;
    private QueryClient queryClient;
    public static final String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_feed);

        queryClient = new QueryClient();

        pb = findViewById(R.id.pbUserLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        swipeContainer = findViewById(R.id.swipeUserContainer);
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

        fabAddCar = findViewById(R.id.fabAddCar);
        fabAddCar.hide();
        fabAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "clicked add car button");
                Intent i = new Intent(UserCarFeedActivity.this, AddOwnCarActivity.class);
                startActivityForResult(i, 20);
            }
        });
        // query cars
        fetchOwnCars();
    }

    private void fetchOwnCars() {
        queryClient.fetchCars(new FindCallback<Car>() {
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
        }, false, false);

        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
    }
}