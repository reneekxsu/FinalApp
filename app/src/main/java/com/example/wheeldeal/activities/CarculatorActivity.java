package com.example.wheeldeal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.CarScore;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.CarculatorClient;
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
    ArrayList<Car> allCars = new ArrayList<>();
    ArrayList<String> UltLuxury = new ArrayList<>();
    ArrayList<String> Luxury = new ArrayList<>();
    ArrayList<CarScore> scores = new ArrayList<>();
    ArrayList<Double> x = new ArrayList<>();
    ArrayList<Double> y = new ArrayList<>();
    String myMake, myModel, myYear, myPassengers, mySizeType, myAddress;
    TextInputEditText etCarMake, etCarModel, etCarYear,etCarPassengers, etCarSizeType, etCarAddress;
    TextView tvCalculatedPrice;
    Button btnCalculate;
    Car car = null;
    CarculatorClient carculatorClient;

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

        carculatorClient = new CarculatorClient(etCarMake.getText().toString(), etCarYear.getText().toString(),
                etCarPassengers.getText().toString());

        carculatorClient.initLuxury();

        queryClient = new QueryClient();
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> cars, ParseException e) {
                carculatorClient.updateAllCars(cars);
                if (carFlag){
                    carculatorClient.removeFromAllCars(car);
                }
                carculatorClient.updateCategories();
                btnCalculate.setEnabled(true);
            }
        }, true);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carculatorClient.updateMake(etCarMake.getText().toString());
                carculatorClient.updateYear(etCarYear.getText().toString());
                carculatorClient.updatePassengers(etCarPassengers.getText().toString());
                calculatePricing();
            }
        });
    }

    private void calculatePricing() {
        int price = carculatorClient.calculatePricing();
        tvCalculatedPrice.setVisibility(View.VISIBLE);
        tvCalculatedPrice.setText("Your recommended price is $" + price + "/day");
    }

}