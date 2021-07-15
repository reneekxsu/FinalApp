package com.example.finalapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.finalapp.R;
import com.example.finalapp.models.Car;
import com.example.finalapp.models.ParcelableCar;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class CarDetailsActivity extends AppCompatActivity {
    public static final String TAG = "UserCarDetailsActivity";

    Car car;
    TextView tvCarDetailName;
    TextView tvDetailRate;
    TextView tvDetailDescription;
    ImageView ivDetailCar;
    Context context;
    ImageButton ibtnEdit;
    ImageButton ibtnEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_details);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        tvCarDetailName = (TextView) findViewById(R.id.tvCarDetailName);
        tvDetailRate = (TextView) findViewById(R.id.tvDetailRate);
        tvDetailDescription = (TextView) findViewById(R.id.tvDetailDescription);
        ivDetailCar = (ImageView) findViewById(R.id.ivDetailCar);
        ibtnEdit = (ImageButton) findViewById(R.id.ibtnEdit);
        ibtnEvent = (ImageButton) findViewById(R.id.ibtnEvent);
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

        if (!userIsAuthor(car)){
            ibtnEdit.setVisibility(View.GONE);
        } else {
            ibtnEdit.setVisibility(View.VISIBLE);
            ibtnEdit.setBackgroundDrawable(null);
            ibtnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParcelableCar c = new ParcelableCar(car);
                    Intent i = new Intent(context, EditCarActivity.class);
                    i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(c));
                    startActivity(i);
                    finish();
                }
            });
        }

        // car owner will schedule when they are not free, while customer will schedule when they want to rent it
        ibtnEvent.setBackgroundDrawable(null);
        ibtnEvent.setVisibility(View.VISIBLE);
        ibtnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "car schedule button clicked");
                Intent i = new Intent(context, ScheduleTimesActivity.class);
                ParcelableCar c = new ParcelableCar(car);
                i.putExtra("ParcelableCar", Parcels.wrap(c));
                startActivity(i);
            }
        });

    }

    boolean userIsAuthor(Car car){
        return car.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
    }

    boolean userIsCustomer(){
        return !userIsAuthor(car);
    }

}