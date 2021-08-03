package com.example.wheeldeal.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.models.ParseApplication;
import com.example.wheeldeal.utils.BinarySearchClient;
import com.example.wheeldeal.utils.CarculatorClient;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CarculatorActivity extends AppCompatActivity {

    public static final String TAG = "CarculatorActivity";
    QueryClient queryClient;
    String myMake, myModel, myYear, myPassengers, mySizeType, myAddress;
    TextInputEditText etCarModel, etCarYear,etCarPassengers, etCarSizeType, etCarAddress;
    TextInputLayout tilCarMake;
    TextView tvCalculatedPrice;
    Button btnCalculate;
    Car car = null;
    CarculatorClient carculatorClient;
    BinarySearchClient bs;
    String[] makes;
    AppCompatAutoCompleteTextView acMake, acModel;
    HashMap<String, String> hmModelMake;
    HashMap<String, ArrayList> hmMakeModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carculator);

        Resources res = getResources();
        makes = res.getStringArray(R.array.makes_array);

        bs = new BinarySearchClient();

        Intent intent = getIntent();
        boolean carFlag = intent.getExtras().getBoolean("carFlag");

        if (carFlag){
            car = ((ParcelableCar) Parcels.unwrap(intent.getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();
        }

        myMake = getIntent().getStringExtra("make");
        myModel = getIntent().getStringExtra("model");
        myYear = getIntent().getStringExtra("year");
        myPassengers = getIntent().getStringExtra("passengers");
        mySizeType = getIntent().getStringExtra("sizetype");
        myAddress = getIntent().getStringExtra("address");

        hmModelMake = ((ParseApplication) getApplication()).getHashMapModelMake();

        hmMakeModels = ((ParseApplication) getApplication()).getHashMapMakeModel();

        ArrayList<String> allModels = new ArrayList<String>(((ParseApplication) getApplication()).getModels());
        ArrayList<String> allModelsExtra = new ArrayList<String>(((ParseApplication) getApplication()).getModels());

        ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_singlechoice,
                allModels);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, makes);
        //Find TextView control
        acMake = (AppCompatAutoCompleteTextView) findViewById(R.id.etCarculatorMake);
        //Set the number of characters the user must type before the drop down list is shown
        acMake.setThreshold(1);
        //Set the adapter
        acMake.setAdapter(adapter);

        final String[] myEnteredMake = new String[1];
        acMake.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myEnteredMake[0] = adapter.getItem(position).toString();
                ArrayList<String> models = hmMakeModels.get(myEnteredMake[0]);
                modelAdapter.clear();
                modelAdapter.addAll(models);
                modelAdapter.notifyDataSetChanged();
            }
        });

        acMake.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myEnteredMake[0] = null;
                if (s.toString().length() == 0){
                    modelAdapter.clear();
                    modelAdapter.addAll(allModelsExtra);
                    modelAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Log.i(TAG, "adapter set");
        //Find TextView control
        acModel = (AppCompatAutoCompleteTextView) findViewById(R.id.etCarculatorModel);
        //Set the number of characters the user must type before the drop down list is shown
        acModel.setThreshold(1);
        //Set the adapter
        acModel.setAdapter(modelAdapter);

        acModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedModel = modelAdapter.getItem(position).toString();
                String make = hmModelMake.get(selectedModel);
                acMake.setText(make);
                Log.i(TAG, "matching make is: " + make);
            }
        });

        etCarYear = findViewById(R.id.etCarculatorYear);
        etCarPassengers = findViewById(R.id.etCarculatorPassengers);
        etCarSizeType = findViewById(R.id.etCarculatorSizeType);
        etCarAddress = findViewById(R.id.etCarculatorAddress);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvCalculatedPrice = findViewById(R.id.tvCalculatedPrice);
        tilCarMake = findViewById(R.id.tilCarMake);

        acMake.setText(myMake);
        acModel.setText(myModel);
        etCarYear.setText(myYear);
        etCarPassengers.setText(myPassengers);
        etCarSizeType.setText(mySizeType);
        etCarAddress.setText(myAddress);
        tvCalculatedPrice.setVisibility(View.GONE);

        btnCalculate.setEnabled(false);

        carculatorClient = new CarculatorClient(acMake.getText().toString(), etCarYear.getText().toString(),
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
                String make = acMake.getText().toString();
                if (myEnteredMake[0] == null){
                    // text was inputted rather than selected from autocomplete, must search array
                    int res = bs.binarySearch(makes, make);
                    if (res == -1){
                        tilCarMake.setError("Please select valid car make");
                        return;
                    }
                }
                carculatorClient.updateMake(acMake.getText().toString());
                carculatorClient.updateYear(etCarYear.getText().toString());
                carculatorClient.updatePassengers(etCarPassengers.getText().toString());
                calculatePricing();
            }
        });
    }

    private void calculatePricing() {
        int price = carculatorClient.calculatePricing();
        int days = carculatorClient.calculateDays();
        int totalEarnings = price * days;
        tvCalculatedPrice.setVisibility(View.VISIBLE);
        tvCalculatedPrice.setText("Your recommended price is $" + price + "/day. Your expected earnings are $" + totalEarnings + ".");
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}