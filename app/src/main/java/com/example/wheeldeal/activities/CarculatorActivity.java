package com.example.wheeldeal.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.utils.QueryClient;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class CarculatorActivity extends AppCompatActivity {

    QueryClient queryClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carculator);

        queryClient = new QueryClient();
        queryClient.fetchCars(new FindCallback<Car>() {
            @Override
            public void done(List<Car> objects, ParseException e) {

            }
        }, true);
    }
}