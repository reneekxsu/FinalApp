package com.example.wheeldeal.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.CarMapActivity;
import com.example.wheeldeal.adapters.CarAdapter;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.QueryClient;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private RecyclerView rvAllCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    protected List<Car> allCarsDefault = new ArrayList<>();
    protected List<Car> allCarsSortedPrice = new ArrayList<>();
    protected List<Car> allCarsSortedPassengers = new ArrayList<>();
    protected List<Car> allCarsSortedDistance = new ArrayList<>();
    protected ArrayList<ArrayList<DateRangeHolder>> allEventDates;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    private QueryClient queryClient;
    private Toolbar toolbar;
    MenuItem finishedLoading;
    boolean isLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        queryClient = new QueryClient();

        pb = view.findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "refreshing");
                fetchAllCars();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvAllCars = view.findViewById(R.id.rvAllCars);
        allCars = new ArrayList<>();
        adapter = new CarAdapter(getActivity(), allCars);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setDuration(1000);
        alphaInAnimationAdapter.setInterpolator(new OvershootInterpolator());
        alphaInAnimationAdapter.setFirstOnly(false);
        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(alphaInAnimationAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
        rvAllCars.setAdapter(scaleInAnimationAdapter);
        rvAllCars.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Log.i(TAG, "querying all cars");
        fetchAllCars();

        Spinner spinner = (Spinner) view.findViewById(R.id.mySpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    Toast.makeText(getContext(), "Default order", Toast.LENGTH_SHORT).show();
                    adapter.clear();
                    adapter.addAll(allCarsDefault);
                } else if (position == 1){
                    Toast.makeText(getContext(), "Sort by price: low to high", Toast.LENGTH_SHORT).show();
                    adapter.clear();
                    adapter.addAll(allCarsSortedPrice);
                } else {
                    Toast.makeText(getContext(), "Sort by number of passengers", Toast.LENGTH_SHORT).show();
                    adapter.clear();
                    adapter.addAll(allCarsSortedPassengers);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void fetchAllCars() {
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                    Log.i(TAG, "error message: " + e.getCause().getMessage());
                } else {
                    adapter.clear();
                    adapter.addAll(cars);
                    for (Car car : allCars) {
                        Log.i(TAG, "Car in allCars: " + car.getModel());
                    }
                    allCarsDefault.addAll(cars);
                    isLoaded = true;
                    if (isLoaded){
                        finishedLoading.setVisible(true);
                    } else {
                        finishedLoading.setVisible(false);
                    }
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        }, true);


        queryClient.fetchCarsBySeats(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                    Log.i(TAG, "error message: " + e.getCause().getMessage());
                } else {
//                    for (Car car : cars) {
//                        Log.i(TAG, "Car: " + car.getModel());
//                    }
                    allCarsSortedPassengers.addAll(cars);
                }
            }
        });

        queryClient.fetchCarsByPrice(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                    Log.i(TAG, "error message: " + e.getCause().getMessage());
                } else {
//                    for (Car car : cars) {
//                        Log.i(TAG, "Car: " + car.getModel());
//                    }
                    allCarsSortedPrice.addAll(cars);
                }
            }
        });

        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action, menu);
        finishedLoading = menu.findItem(R.id.action_map);
        if (isLoaded){
            finishedLoading.setVisible(true);
        } else {
            finishedLoading.setVisible(false);
        }
        Log.i(TAG, "onCreateOptions");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_map:
                ArrayList<ParcelableCar> parcelableCars = new ArrayList<ParcelableCar>();
                for (Car car : allCars){
//                    Log.i(TAG, "Car on textview click: " + car.getModel());
                    parcelableCars.add(new ParcelableCar(car));
                }
                Intent intent = new Intent(getContext(), CarMapActivity.class);
                intent.putExtra("ParcelableCars", Parcels.wrap(parcelableCars));
                startActivity(intent);
        }
        Log.i(TAG, "default");
        return super.onOptionsItemSelected(item);
    }
}