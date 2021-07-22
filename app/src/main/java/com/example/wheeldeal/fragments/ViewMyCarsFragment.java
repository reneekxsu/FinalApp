package com.example.wheeldeal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewMyCarsFragment extends Fragment {

    private RecyclerView rvCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar pb;
    private FloatingActionButton fabAddCar;
    private QueryClient queryClient;
    public static final String TAG = "UserCarFeedFragment";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_user_car_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queryClient = new QueryClient();

        pb = view.findViewById(R.id.pbUserLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        swipeContainer = view.findViewById(R.id.swipeUserContainer);
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

        rvCars = view.findViewById(R.id.rvCars);

        // initialize array for holding cars, and create CarAdapter
        allCars = new ArrayList<>();
        adapter = new CarAdapter(view.getContext(), allCars);
        // set adapter on RV
        rvCars.setAdapter(adapter);
        // set layout manager on RV
        rvCars.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        rvCars.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0) {
//                    ((MainActivity)getActivity()).setNavigationVisibility(false);
//                } else if (dy < 0 ) {
//                    ((MainActivity)getActivity()).setNavigationVisibility(true);
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });

        fabAddCar = view.findViewById(R.id.fabAddCar);
        fabAddCar.hide();
        fabAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("FeedActivity", "clicked post button");
                Intent i = new Intent(view.getContext(), AddOwnCarActivity.class);
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
                    fabAddCar.show();
                }
            }
        }, false);

        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
    }
}