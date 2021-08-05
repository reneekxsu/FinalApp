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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.BitmapScaler;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.models.ParseApplication;
import com.example.wheeldeal.utils.BinarySearchClient;
import com.example.wheeldeal.utils.CameraClient;
import com.example.wheeldeal.utils.GeocoderClient;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class EditCarActivity extends AppCompatActivity {

    public static final String TAG = "EditCarActivity";

    EditText etEditYear,etEditPassengers, etEditSizeType,
            etEditAddress, etEditDescription, etEditRate;
    Button btnEditSave, btnEditCamera;
    ImageView ivEditPreview;
    TextView tvEditClose;
    Car car;
    TextInputLayout tilPrice, tilCarMake;
    HashMap<String, String> hmModelMake;
    HashMap<String, ArrayList> hmMakeModels;


    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";
    File photoFile;
    ParseFile image;
    QueryClient queryClient = new QueryClient();
    GeocoderClient geocoderClient;
    CameraClient cameraClient;
    BinarySearchClient bs;
    String[] makes;
    AppCompatAutoCompleteTextView acMake, acModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "entered EditCarActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        Resources res = getResources();
        makes = res.getStringArray(R.array.makes_array);

        geocoderClient = new GeocoderClient(this);
        cameraClient = new CameraClient(this);
        bs = new BinarySearchClient();

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

        etEditYear = findViewById(R.id.etCarculatorYear);
        etEditRate = findViewById(R.id.etCarPrice);
        etEditPassengers = findViewById(R.id.etCarculatorPassengers);
        etEditSizeType = findViewById(R.id.etCarculatorSizeType);
        etEditDescription = findViewById(R.id.etCarDescription);
        etEditAddress = findViewById(R.id.etCarculatorAddress);
        btnEditCamera = findViewById(R.id.btnCamera);
        btnEditSave = findViewById(R.id.btnUpdateCar);
        ivEditPreview = findViewById(R.id.ivPreview);
        tvEditClose = findViewById(R.id.tvClose);
        tilPrice = findViewById(R.id.tilPrice);
        tilCarMake = findViewById(R.id.tilCarMake);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();

        acMake.setText(car.getMake());
        acModel.setText(car.getModel());
        etEditYear.setText(car.getYear());
        etEditRate.setText(car.getRate().toString());
        etEditPassengers.setText(car.getPassengers());
        etEditSizeType.setText(car.getSizeType());
        etEditDescription.setText(car.getDescription());
        etEditAddress.setText(car.getAddress());

        acMake.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                acMake.setText("");
                return true;
            }
        });
        acModel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                acModel.setText("");
                return true;
            }
        });
        etEditYear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEditYear.setText("");
                return true;
            }
        });
        etEditRate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEditRate.setText("");
                return true;
            }
        });
        etEditPassengers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEditPassengers.setText("");
                return true;
            }
        });
        etEditSizeType.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEditSizeType.setText("");
                return true;
            }
        });
        etEditDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEditDescription.setText("");
                return true;
            }
        });
        etEditAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                etEditAddress.setText("");
                return true;
            }
        });

        image = car.getImage();
        if (image != null) {
            ivEditPreview.setVisibility(View.VISIBLE);
            Glide.with(this).load(image.getUrl()).into(ivEditPreview);
        } else {
            ivEditPreview.setVisibility(View.GONE);
        }
        
        btnEditCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnEditSave.setText("Save");

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String make = acMake.getText().toString();
                String model = acModel.getText().toString();
                String year = etEditYear.getText().toString();
                String price = etEditRate.getText().toString();
                String passengerCount = etEditPassengers.getText().toString();
                String sizeType = etEditSizeType.getText().toString();
                String description = etEditDescription.getText().toString();
                String address = etEditAddress.getText().toString();

                if (myMake[0] == null){
                    // text was inputted rather than selected from autocomplete, must search array
                    int res = bs.binarySearch(makes, make);
                    if (res == -1){
                        tilCarMake.setError("Please select valid car make");
                        return;
                    }
                }


                if (description.isEmpty()){
                    Toast.makeText(EditCarActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (price.isEmpty()){
                    Toast.makeText(EditCarActivity.this, "Rate cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (model.isEmpty()){
                    Toast.makeText(EditCarActivity.this, "Model name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if ((image == null) && (photoFile == null || ivEditPreview.getDrawable() == null)){
                        Toast.makeText(EditCarActivity.this, "No image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editCar(make, model, year, price, passengerCount, sizeType, description,
                            address, photoFile);
                }
            }
        });

        tvEditClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Close button clicked");
                Intent i = new Intent(EditCarActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(0, R.anim.slide_out_down);
                finish();
            }
        });

        tilPrice.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditCarActivity.this, CarculatorActivity.class);
                i.putExtra("make", acMake.getText().toString());
                i.putExtra("model", acModel.getText().toString());
                i.putExtra("year", etEditYear.getText().toString());
                i.putExtra("passengers", etEditPassengers.getText().toString());
                ParcelableCar c = new ParcelableCar(car);
                i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(c));
                i.putExtra("carFlag", true);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
            }

        });

    }

    private void editCar(String make, String model, String year, String rate,
                         String passengers, String sizeType, String description, String address,
                         File photoFile) {
        Log.i(TAG, "Car was saved to backend");
        if (photoFile != null){
            image = new ParseFile(photoFile);
        }
        geocoderClient.lookupAddress(address, new GeocoderClient.GeocoderResponseHandler() {
            @Override
            public void consumeAddress(ParseGeoPoint geoPoint) {
                ParseGeoPoint gp = geoPoint;
                queryClient.saveCarFields(car, description, ParseUser.getCurrentUser(), image,
                        rate, model, make, year, passengers, sizeType, address, gp, null, false);
                Toast.makeText(EditCarActivity.this, "Edits to car were saved", Toast.LENGTH_SHORT).show();
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
        Uri fileProvider = FileProvider.getUriForFile(EditCarActivity.this, "com.codepath.fileprovider.FinalApp", photoFile);
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
                ivEditPreview.setImageBitmap(resizedBitmap);
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