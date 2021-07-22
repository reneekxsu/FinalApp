package com.example.wheeldeal.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wheeldeal.R;
import com.example.wheeldeal.adapters.CarAdapter;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.ParcelableCar;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SameAddressCarsActivity extends AppCompatActivity {
    private RecyclerView rvCars;
    protected CarAdapter adapter;
    protected List<Car> allCars;
    Toolbar toolbar;
    public static final String TAG = "SameAddressCarsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_same_address_cars_feed);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        allCars = new ArrayList<>();

        ArrayList<ParcelableCar> parcelableCars = (ArrayList<ParcelableCar>) Parcels.unwrap(getIntent().getParcelableExtra("ParcelableCars"));
        for (ParcelableCar parcelableCar : parcelableCars){
            allCars.add(parcelableCar.getCar());
        }

        getSupportActionBar().setTitle("Cars at " + allCars.get(0).getAddress());

        rvCars = findViewById(R.id.rvSameAddressCars);
        adapter = new CarAdapter(this, allCars);
        rvCars.setAdapter(adapter);
        rvCars.setLayoutManager(new LinearLayoutManager(this));
    }
}
