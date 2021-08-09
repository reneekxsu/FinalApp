package com.example.wheeldeal.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.BitmapScaler;
import com.example.wheeldeal.ParseApplication;
import com.example.wheeldeal.utils.BinarySearchClient;
import com.example.wheeldeal.utils.CameraClient;
import com.example.wheeldeal.utils.FormClient;
import com.example.wheeldeal.utils.GeocoderClient;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AddOwnCarActivity extends AppCompatActivity {

    public static final String TAG = "RegisterCarActivity";

    private TextInputEditText etYear, etPrice, etPassengers, etSizeType,
            etDescription, etAddress;
    private Button btnCamera, btnRegister;
    private ImageView ivPreview;
    private TextView tvClose;
    String make, model, year, price, passengerCount, sizeType, description, address;
    private TextInputLayout tilPrice, tilCarMake, tilCarModel, tilCarYear, tilCarPrice,
            tilPassengers, tilSize, tilDescription, tilAddress;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";
    File photoFile;
    FormClient formClient = new FormClient();
    QueryClient queryClient = new QueryClient();
    GeocoderClient geocoderClient;
    CameraClient cameraClient;
    BinarySearchClient bs = new BinarySearchClient();
    String makes[];
    AppCompatAutoCompleteTextView acMake, acModel;
    HashMap<String, String> hmModelMake;
    HashMap<String, ArrayList> hmMakeModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        Resources res = getResources();
        makes = res.getStringArray(R.array.makes_array);

        hmModelMake = ((ParseApplication) getApplication()).getHashMapModelMake();

        hmMakeModels = ((ParseApplication) getApplication()).getHashMapMakeModel();


        ArrayList<String> allModels = new ArrayList<String>(((ParseApplication) getApplication()).getModels());
        ArrayList<String> allModelsExtra = new ArrayList<String>(((ParseApplication) getApplication()).getModels());

        ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_singlechoice,
                allModels);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, makes);
        //Find TextView control
        acMake = (AppCompatAutoCompleteTextView) findViewById(R.id.etCarMake);
        //Set the number of characters the user must type before the drop down list is shown
        acMake.setThreshold(1);
        //Set the adapter
        acMake.setAdapter(adapter);

        final String[] myMake = new String[1];
        acMake.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myMake[0] = adapter.getItem(position).toString();
                ArrayList<String> models = hmMakeModels.get(myMake[0]);
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
                myMake[0] = null;
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

        etYear = findViewById(R.id.etCarculatorYear);
        etPrice = findViewById(R.id.etCarPrice);
        etPassengers = findViewById(R.id.etCarculatorPassengers);
        etSizeType = findViewById(R.id.etCarculatorSizeType);
        etDescription = findViewById(R.id.etCarDescription);
        etAddress = findViewById(R.id.etCarculatorAddress);
        btnCamera = findViewById(R.id.btnCamera);
        btnRegister = findViewById(R.id.btnUpdateCar);
        ivPreview = findViewById(R.id.ivPreview);
        tvClose = findViewById(R.id.tvClose);
        tilPrice = findViewById(R.id.tilPrice);
        tilCarMake = findViewById(R.id.tilCarMake);
        tilCarModel = findViewById(R.id.tilCarModel);
        tilCarYear = findViewById(R.id.tilCarYear);
        tilPassengers = findViewById(R.id.tilPassengers);
        tilSize = findViewById(R.id.tilSizeType);
        tilDescription = findViewById(R.id.tilDescription);
        tilAddress = findViewById(R.id.tilAddress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a new car");

        geocoderClient = new GeocoderClient(this);
        cameraClient = new CameraClient(this);


        tilPrice.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddOwnCarActivity.this, CarculatorActivity.class);
                i.putExtra("make", acMake.getText().toString());
                i.putExtra("model", acModel.getText().toString());
                i.putExtra("year", etYear.getText().toString());
                i.putExtra("passengers", etPassengers.getText().toString());
                i.putExtra("carFlag", false);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
            }
        });


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make = acMake.getText().toString();
                model = acModel.getText().toString();
                year = etYear.getText().toString();
                price = etPrice.getText().toString();
                passengerCount = etPassengers.getText().toString();
                sizeType = etSizeType.getText().toString();
                description = etDescription.getText().toString();
                address = etAddress.getText().toString();

                boolean isError = false;

                if (myMake[0] == null){
                    // text was inputted rather than selected from autocomplete, must search array
                    int res = bs.binarySearch(makes, make);
                    if (res == -1){
                        tilCarMake.setError("Please select valid car make");
                        isError = true;
                    }
                }

                if (model.isEmpty()){
                    tilCarModel.setError("Please select valid car model");
                    isError = true;
                }

                if(year.isEmpty()){
                    tilCarYear.setError("Please enter a valid year");
                    isError = true;
                }

                if (price.isEmpty()){
                    tilPrice.setError("Please enter a valid price");
                    isError = true;
                }

                if (passengerCount.isEmpty()){
                    tilPassengers.setError("Please indicate number of passengers");
                    isError = true;
                }

                if (sizeType.isEmpty()){
                    tilSize.setError("Please specify a size");
                    isError = true;
                }

                if (description.isEmpty()){
                    tilDescription.setError("Please write a description");
                    isError = true;
                }

                if (address.isEmpty()){
                    tilAddress.setError("Please specify an address");
                    isError = true;
                }

                if (formClient.isEntryEmpty(make, model, year, price, passengerCount, sizeType, description, address)){
                    Toast.makeText(AddOwnCarActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (isError){
                        return;
                    }
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (photoFile == null || ivPreview.getDrawable() == null){
                        Toast.makeText(AddOwnCarActivity.this, "No image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveCar(description, currentUser, photoFile, price, model, make, year, passengerCount,
                            sizeType, address);
                }
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Close button clicked");
                onBackPressed();
            }
        });
    }

    private void saveCar(String description, ParseUser currentUser, File photoFile, String rate, String model,
                         String make, String year, String passengers, String size, String address) {
        geocoderClient.lookupAddress(address, new GeocoderClient.GeocoderResponseHandler() {
            @Override
            public void consumeAddress(ParseGeoPoint geoPoint) {
                ParseGeoPoint gp = geoPoint;
                queryClient.saveCar(description, currentUser, photoFile, rate, model, make, year,
                        passengers, size, address, gp, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null){
                                    Log.e(TAG, "Could not save", e);
                                    Toast.makeText(AddOwnCarActivity.this, "Could not save", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    Log.i(TAG, "Car was saved to backend");
                                    Toast.makeText(AddOwnCarActivity.this, "Car was saved", Toast.LENGTH_SHORT).show();
                                    etDescription.setText("");
                                    acModel.setText("");
                                    etPrice.setText("");
                                    acMake.setText("");
                                    etYear.setText("");
                                    etPassengers.setText("");
                                    etSizeType.setText("");
                                    etDescription.setText("");
                                    etAddress.setText("");
                                    // set to empty image
                                    ivPreview.setImageResource(0);
                                }
                            }
                        }, true);
                ParseUser current = ParseUser.getCurrentUser();
                queryClient.fetchUserDetails(current, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        int numCars = (int)user.get("carOwnedCount");
                        user.put("carOwnedCount", numCars + 1);
                        user.saveInBackground();
                    }
                });
            }
        });
    }

    private void launchCamera() {
        Log.i(TAG, "camera launched");
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = cameraClient.getPhotoFileUri(photoFileName, TAG);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(AddOwnCarActivity.this, "com.codepath.fileprovider.FinalApp", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 100);
                // Load the taken image into a preview
                ivPreview.setImageBitmap(resizedBitmap);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}