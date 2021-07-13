package com.example.finalapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class UserCarDetailsActivity extends AppCompatActivity {
    Car car;
    TextView tvCarDetailName;
    TextView tvDetailRate;
    TextView tvDetailDescription;
    ImageView ivDetailCar;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_details);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        tvCarDetailName = (TextView) findViewById(R.id.tvCarDetailName);
        tvDetailRate = (TextView) findViewById(R.id.tvDetailRate);
        tvDetailDescription = (TextView) findViewById(R.id.tvDetailDescription);
        ivDetailCar = (ImageView) findViewById(R.id.ivDetailCar);
        context = (Context) this;

        tvCarDetailName.setText(car.getModel());
        ParseFile image = car.getImage();
        if (image != null) {
            ivDetailCar.setVisibility(View.VISIBLE);
            Glide.with(context).load(image.getUrl()).into(ivDetailCar);
        } else {
            ivDetailCar.setVisibility(View.GONE);
        }
        tvDetailRate.setText("$" + car.getRate() + "/hr");
        tvDetailDescription.setText(car.getDescription());
    }
}