package com.example.wheeldeal.fragments;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wheeldeal.R;
import com.example.wheeldeal.activities.CarMapActivity;
import com.example.wheeldeal.adapters.CarAdapter;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.CarFeedScorePair;
import com.example.wheeldeal.models.DateRangeHolder;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.ParseApplication;
import com.example.wheeldeal.utils.GeocoderClient;
import com.example.wheeldeal.utils.QueryClient;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private RecyclerView rvAllCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    protected List<Car> allCarsDefault;
    protected List<Car> allCarsSortedPrice;
    protected List<Car> allCarsSortedPassengers;
    protected ArrayList<ArrayList<DateRangeHolder>> allEventDates;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    private QueryClient queryClient;
    private GeocoderClient geocoderClient;
    private Toolbar toolbar;
    MenuItem loadedMap;
    public static MenuItem filters;
    boolean firstLoad;
    boolean isLoaded;
    boolean inSearch;
    ParseGeoPoint currentPoint;
    Spinner spinner;
    int savedSelection;
    String submittedQuery;
    boolean isSearchComplete;

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

        savedSelection = 0;

        queryClient = new QueryClient();
        geocoderClient = new GeocoderClient(getActivity());

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

        firstLoad = true;
        isLoaded = false;
        inSearch = false;
        isSearchComplete = false;

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvAllCars = view.findViewById(R.id.rvAllCars);
        allCars = new ArrayList<>();
        allCarsDefault = new ArrayList<>();
        allCarsSortedPrice = new ArrayList<>();
        allCarsSortedPassengers = new ArrayList<>();
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

        spinner = (Spinner) view.findViewById(R.id.mySpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        spinner.setVisibility(View.GONE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "item selected");
                updateOrder(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public ArrayList<Car> sortFeed(List<Car> cars, double avgScore){
        ArrayList<CarFeedScorePair> scores = new ArrayList<>();
        for (Car car : cars){
            scores.add(new CarFeedScorePair(car, avgScore));
        }
        Collections.sort(scores, new Comparator<CarFeedScorePair>() {
            @Override
            public int compare(CarFeedScorePair score1, CarFeedScorePair score2) {
                return (Double.compare(score2.getScore(), score1.getScore()));
            }
        });
        ArrayList<Car> sortedCars = new ArrayList<>();
        Log.i(TAG, "myScore: " + avgScore);
        for (CarFeedScorePair score : scores){
            Log.i(TAG, "car: " + score.getCar().getModel() + " score: " + score.getScore());
            sortedCars.add(score.getCar());
        }
        return sortedCars;
    }

    private void fetchAllCars() {
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                    Log.i(TAG, "error message: " + e.getCause().getMessage());
                } else {
                    queryClient.fetchUserDetails(ParseUser.getCurrentUser(), new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            Log.i(TAG, "score from background: " + user.get("avgScore"));
                            ArrayList<Car> sortedCars = sortFeed(cars, user.getDouble("avgScore"));
                            adapter.clear();
                            adapter.addAll(sortedCars);
                            allCarsDefault.clear();
                            allCarsDefault.addAll(sortedCars);
                            isLoaded = true;
                            if (isLoaded){
                                if (loadedMap != null){
                                    loadedMap.setVisible(true);
                                }
                            } else {
                                if (loadedMap != null){
                                    loadedMap.setVisible(false);
                                }
                            }
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                            firstLoad = true;
                            spinner.setSelection(0, true);
                        }
                    });
                }
            }
        }, true, false);


        queryClient.fetchCarsBySeats(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                    Log.i(TAG, "error message: " + e.getCause().getMessage());
                } else {
                    allCarsSortedPassengers.clear();
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
                    allCarsSortedPrice.clear();
                    allCarsSortedPrice.addAll(cars);
                }
            }
        });

        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
    }
    
    public void fetchCarByQuery(String query){
        // see if location matches
        geocoderClient.lookupAddress(query, new GeocoderClient.GeocoderResponseHandler() {
            @Override
            public void consumeAddress(ParseGeoPoint geoPoint) {
                currentPoint = geoPoint;
                Log.i(TAG, "geopoint: " + currentPoint);
                if (currentPoint != null){
                    rvAllCars.setVisibility(View.VISIBLE);
                    queryClient.fetchCarByProximity(new FindCallback<Car>() {
                        @Override
                        public void done(List<Car> cars, ParseException e) {
                            if (cars.size() > 0){
                                adapter.clear();
                                adapter.addAll(cars);
                            } else {
                                rvAllCars.setVisibility(View.GONE);
                            }
                        }
                    }, currentPoint, 100);
                } else {
                    Log.i(TAG, "no location found");
                    rvAllCars.setVisibility(View.GONE);
                }
            }
        });
    }

    public void fetchCarByFilter(String search, String model, String make){
        if (search.isEmpty()) {
            currentPoint = null;
        }
        if (currentPoint != null){
            Log.i(TAG, "currentPoint lat: " + currentPoint.getLatitude() + " " + currentPoint.getLongitude());
            Log.i(TAG, "search: " + search);
        }
        Log.i(TAG, "geopoint: " + currentPoint);
        queryClient.fetchCarsByFilter(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (cars.size() > 0){
                    adapter.clear();
                    adapter.addAll(cars);
                    rvAllCars.setVisibility(View.VISIBLE);
                } else {
                    rvAllCars.setVisibility(View.GONE);
                }
            }
        }, currentPoint, 100, model, make);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action, menu);
        loadedMap = menu.findItem(R.id.action_map);
        filters = menu.findItem(R.id.action_filter);
        if (isLoaded){
            loadedMap.setVisible(true);
        } else {
            loadedMap.setVisible(false);
        }
        if (((ParseApplication) getActivity().getApplication()).isDataReady){
            filters.setVisible(true);
        } else {
            filters.setVisible(false);
        }
        Log.i(TAG, "onCreateOptions");
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Where are you going?");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                isSearchComplete = true;
                submittedQuery = query;
                Log.i(TAG, "text submitted");
                // perform query here
                fetchCarByQuery(query);
                Log.i(TAG, "returning");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isSearchComplete = false;
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                spinner.setVisibility(View.GONE);
                swipeContainer.setEnabled(false);
                inSearch = true;
                firstLoad = false;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                spinner.setVisibility(View.VISIBLE);
                swipeContainer.setEnabled(true);
                updateOrder(savedSelection);
                inSearch = false;
                rvAllCars.setVisibility(View.VISIBLE);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_map:
                ArrayList<ParcelableCar> parcelableCars = new ArrayList<ParcelableCar>();
                for (Car car : allCars){
                    parcelableCars.add(new ParcelableCar(car));
                }
                Intent intent = new Intent(getContext(), CarMapActivity.class);
                intent.putExtra("ParcelableCars", Parcels.wrap(parcelableCars));
                if (inSearch && (currentPoint != null && rvAllCars.getVisibility() == View.VISIBLE)){
                    intent.putExtra("locationFlag", true);
                    intent.putExtra("ParseGeoPoint", currentPoint);
                } else {
                    intent.putExtra("locationFlag", false);
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                break;
            case R.id.action_filter:
                openDialog();
                break;
        }
        Log.i(TAG, "default");
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        FilterDialogFragment dialog = new FilterDialogFragment();
        dialog.setTargetFragment(this, 1);
        dialog.show(getFragmentManager().beginTransaction(), TAG);
    }

    public void updateOrder(int position){
        Log.i(TAG, "updateOrder called");
        if (position == 0){
            if (!firstLoad){
                Log.i(TAG, "default selected");
                adapter.clear();
                adapter.addAll(allCarsDefault);
                savedSelection = 0;
            }
        } else if (position == 1){
            adapter.clear();
            adapter.addAll(allCarsSortedPrice);
            firstLoad = false;
            savedSelection = 1;
        } else {
            adapter.clear();
            adapter.addAll(allCarsSortedPassengers);
            firstLoad = false;
            savedSelection = 2;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        switch(requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    Log.i(TAG, "returned to home fragment from full screen dialog");
                    String filterMake = data.getStringExtra("Make");
                    String filterModel = data.getStringExtra("Model");
                    Log.i(TAG, "filterMake: " + filterMake);
                    Log.i(TAG, "filterModel: " + filterModel);
                    String query = "";
                    if (isSearchComplete){
                        query = submittedQuery;
                    }
                    if (filterModel.isEmpty() && filterMake.isEmpty()){
                        spinner.setVisibility(View.VISIBLE);
                    } else {
                        spinner.setVisibility(View.GONE);
                    }
                    fetchCarByFilter(query, filterModel, filterMake);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}