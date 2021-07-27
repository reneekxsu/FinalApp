package com.example.wheeldeal.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class CarculatorActivity extends AppCompatActivity {

    QueryClient queryClient;
    ArrayList<Car> sameMake;
    ArrayList<Car> sameModel;
    ArrayList<Car> sameYear;
    ArrayList<Car> samePassengers;
    ArrayList<Car> sameSizeType;
    ArrayList<Car> sameAddress;
    ArrayList<Car> allCars;
    String myMake, myModel, myYear, myPrice, myPassengers, mySizeType, myAddress;
    TextInputEditText etCarMake, etCarModel, etCarYear,etCarPassengers, etCarSizeType, etCarAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carculator);

        allCars = new ArrayList<>();

        myMake = getIntent().getStringExtra("make");
        myModel = getIntent().getStringExtra("model");
        myYear = getIntent().getStringExtra("year");
//        myPrice = getIntent().getStringExtra("price");
        myPassengers = getIntent().getStringExtra("passengers");
        mySizeType = getIntent().getStringExtra("sizetype");
        myAddress = getIntent().getStringExtra("address");

        etCarMake = findViewById(R.id.etCarculatorMake);
        etCarModel = findViewById(R.id.etCarculatorModel);
        etCarYear = findViewById(R.id.etCarculatorYear);
        etCarPassengers = findViewById(R.id.etCarculatorPassengers);
        etCarSizeType = findViewById(R.id.etCarculatorSizeType);
        etCarAddress = findViewById(R.id.etCarculatorAddress);

        etCarMake.setText(myMake);
        etCarModel.setText(myModel);
        etCarYear.setText(myYear);
        etCarPassengers.setText(myPassengers);
        etCarSizeType.setText(mySizeType);
        etCarAddress.setText(myAddress);

        queryClient = new QueryClient();
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                allCars.addAll(cars);
                updateCategories();
            }
        }, true);
    }
    public void updateCategories(){
        for (Car car : allCars){

        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}