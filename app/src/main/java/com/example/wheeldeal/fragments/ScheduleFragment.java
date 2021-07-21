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

import com.example.wheeldeal.QueryClient;
import com.example.wheeldeal.R;
import com.example.wheeldeal.adapters.EventAdapter;
import com.example.wheeldeal.models.Event;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    public static final String TAG = "ScheduleFragment";

    private RecyclerView rvEvents;
    protected EventAdapter adapter;
    protected List<Event> allEvents;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    private QueryClient queryClient;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Schedule");

        queryClient = new QueryClient();

        pb = view.findViewById(R.id.pbScheduleLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
        swipeContainer = view.findViewById(R.id.scheduleSwipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "refreshing");
                fetchAllEvents();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvEvents = view.findViewById(R.id.rvAllEvents);
        allEvents = new ArrayList<>();
        adapter = new EventAdapter(view.getContext(), allEvents);
        rvEvents.setAdapter(adapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        rvEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        Log.i(TAG, "querying all events");
        fetchAllEvents();
    }

    private void fetchAllEvents() {
        queryClient.fetchAllEvents(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's events");
                    Log.e(TAG, e.getCause().toString());
                } else {
                    for (Event event : events){
                        Log.i(TAG, "Event showing in schedule for car: " + event.getCar().getModel());
                    }
                    adapter.clear();
                    adapter.addAll(events);
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