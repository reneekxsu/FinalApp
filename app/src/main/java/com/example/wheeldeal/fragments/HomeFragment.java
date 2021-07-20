package com.example.wheeldeal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.QueryClient;
import com.example.wheeldeal.R;
import com.example.wheeldeal.adapters.CarAdapter;
import com.example.wheeldeal.models.Car;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    private RecyclerView rvAllCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    private QueryClient queryClient;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

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
        adapter = new CarAdapter(view.getContext(), allCars);
        rvAllCars.setAdapter(adapter);
        rvAllCars.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvAllCars.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((MainActivity)getActivity()).setNavigationVisibility(false);
                } else if (dy < 0 ) {
                    ((MainActivity)getActivity()).setNavigationVisibility(true);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        Log.i(TAG, "querying all cars");
        fetchAllCars();
    }

    private void fetchAllCars() {
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's cars");
                    Log.i(TAG, "error message: " + e.getCause().getMessage());
                } else {
                    for (Car car : cars) {
                        Log.i(TAG, "Car: " + car.getModel());
                    }
                    adapter.clear();
                    adapter.addAll(cars);
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        }, true);
        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }
    }
}