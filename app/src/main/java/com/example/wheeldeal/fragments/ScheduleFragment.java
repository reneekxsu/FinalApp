package com.example.wheeldeal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wheeldeal.R;
import com.example.wheeldeal.adapters.EventAdapter;
import com.example.wheeldeal.models.Event;
import com.example.wheeldeal.utils.QueryClient;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    public static final String TAG = "ScheduleFragment";

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private RecyclerView rvEvents;
    protected EventAdapter adapter;
    protected List<Event> allEvents;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeContainer;
    private QueryClient queryClient;
    private Toolbar toolbar;

    // newInstance constructor for creating fragment withs arguments
    public static ScheduleFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ScheduleFragment fragment = new ScheduleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queryClient = new QueryClient();

        pb = view.findViewById(R.id.pbScheduleLoading);
        swipeContainer = view.findViewById(R.id.scheduleSwipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "refreshing");
                if (mPage == 0){
                    Log.i(TAG, "fetching future events");
                    fetchAllFutureEvents();
                } else {
                    Log.i(TAG, "fetching past events");
                    fetchAllPastEvents();
                }
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
        Log.i(TAG, "querying all events");
        pb.setVisibility(View.GONE);
        if (mPage == 0){
            pb.setVisibility(ProgressBar.VISIBLE);
            fetchAllFutureEvents();
        } else {
            fetchAllPastEvents();
        }
    }

    private void fetchAllFutureEvents() {
        queryClient.fetchAllFutureEvents(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's events");
                    Log.e(TAG, e.getCause().toString());
                } else {
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

    private void fetchAllPastEvents() {
        queryClient.fetchAllPastEvents(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not get user's events");
                    Log.e(TAG, e.getCause().toString());
                } else {
                    adapter.clear();
                    adapter.addAll(events);
                }
            }
        });
        // Now we call setRefreshing(false) to signal refresh has finished
        if (swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
    }
}