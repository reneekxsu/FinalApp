package com.example.wheeldeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.CarScore;
import com.example.wheeldeal.models.LinearRegression;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CarculatorActivity extends AppCompatActivity {

    public static final String TAG = "CarculatorActivity";
    QueryClient queryClient;
    ArrayList<Car> sameMake = new ArrayList<>();
    ArrayList<Car> sameModel = new ArrayList<>();
    ArrayList<Car> sameYear = new ArrayList<>();
    ArrayList<Car> samePassengers = new ArrayList<>();
    ArrayList<Car> sameSizeType = new ArrayList<>();
    ArrayList<Car> sameAddress = new ArrayList<>();
    ArrayList<Car> allCars = new ArrayList<>();
    ArrayList<String> UltLuxury = new ArrayList<>();
    ArrayList<String> Luxury = new ArrayList<>();
    ArrayList<CarScore> scores = new ArrayList<>();
    ArrayList<Double> x = new ArrayList<>();
    ArrayList<Double> y = new ArrayList<>();
    String myMake, myModel, myYear, myPrice, myPassengers, mySizeType, myAddress;
    TextInputEditText etCarMake, etCarModel, etCarYear,etCarPassengers, etCarSizeType, etCarAddress;
    TextView tvCalculatedPrice;
    Button btnCalculate;
    Car car = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carculator);

        Intent intent = getIntent();
        boolean carFlag = intent.getExtras().getBoolean("carFlag");

        if (carFlag){
            car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        }

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
        btnCalculate = findViewById(R.id.btnCalculate);
        tvCalculatedPrice = findViewById(R.id.tvCalculatedPrice);

        etCarMake.setText(myMake);
        etCarModel.setText(myModel);
        etCarYear.setText(myYear);
        etCarPassengers.setText(myPassengers);
        etCarSizeType.setText(mySizeType);
        etCarAddress.setText(myAddress);
        tvCalculatedPrice.setVisibility(View.GONE);

        btnCalculate.setEnabled(false);

        initLuxury();

        queryClient = new QueryClient();
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                allCars.addAll(cars);
                if (carFlag){
                    allCars.remove(car);
                }
                updateCategories();
            }
        }, true);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatePricing();
            }
        });
    }

    private void calculatePricing() {
        int price;
        double score, prediction;
        int flagLux = 0;
        for (CarScore cs : scores){
            x.add(cs.getScore());
            y.add(Double.parseDouble(cs.getCar().getRate()));
        }
        LinearRegression lr = new LinearRegression(x,y);
        Log.i(TAG, "intercept: " + lr.intercept());
        Log.i(TAG, "slope: " + lr.slope());
        for (String s : UltLuxury){
            if (etCarMake.getText().toString().equals(s)){
                flagLux = 2;
                break;
            }
        }
        if (flagLux == 0){
            for (String s : Luxury){
                if (etCarMake.getText().toString().equals(s)){
                    flagLux = 1;
                    break;
                }
            }
        }
        CarScore thisCar = new CarScore(Integer.parseInt(etCarYear.getText().toString()),
                Integer.parseInt(etCarPassengers.getText().toString()), flagLux);
        score = thisCar.getScore();
        prediction = lr.intercept() + lr.slope() * score;
        price = scoreToPrice((score + prediction) / 2);
        tvCalculatedPrice.setVisibility(View.VISIBLE);
        tvCalculatedPrice.setText("Your recommended price is $" + price + "/day");
    }

    public void updateCategories(){
        for (Car car : allCars){
//            if (!car.equals(this.car)){
                Log.i(TAG, "car: " + car.getModel());
                // remove own car
                int flagLux = 0;
                for (String s : UltLuxury){
                    if (car.getMake().equals(s)){
                        flagLux = 2;
                        break;
                    }
                }
                if (flagLux == 0){
                    for (String s : Luxury){
                        if (car.getMake().equals(s)){
                            flagLux = 1;
                            break;
                        }
                    }
                }
                CarScore thisCar = new CarScore(Integer.parseInt(car.getYear()),
                        Integer.parseInt(car.getPassengers()), flagLux, car);

                scores.add(thisCar);
//            }

//            if (car.getMake().equals(etCarMake.getText().toString())){
//                sameMake.add(car);
//            }
//            if (car.getModel().equals(etCarModel.getText().toString())){
//                sameModel.add(car);
//            }
//            if (car.getYear().equals(etCarYear.getText().toString())){
//                sameYear.add(car);
//            }
//            if (car.getPassengers().equals(etCarPassengers.getText().toString())){
//                samePassengers.add(car);
//            }
//            if (car.getSizeType().equals(etCarSizeType.getText().toString())){
//                sameSizeType.add(car);
//            }
//            if (car.getAddress().equals(etCarAddress.getText().toString())){
//                sameAddress.add(car);
//            }
        }
        btnCalculate.setEnabled(true);
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

    private void initLuxury(){
        UltLuxury.addAll(List.of("Bentley","Ferrari", "Lamborghini", "Maserati", "Mclaren", "Rolls Royce", "Aston Martin", "Corvette"));
        Luxury.addAll(List.of("BMW", "Audi", "Land Rover", "Range Rover", "Mercedes-Benz", "Tesla"));
    }

    private int scoreToPrice(double score){
        return (int) score;
    }
}