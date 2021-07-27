package com.example.wheeldeal.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.wheeldeal.MainActivity;
import com.example.wheeldeal.R;
import com.example.wheeldeal.models.BitmapScaler;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.ParcelableCar;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditCarActivity extends AppCompatActivity {

    public static final String TAG = "EditCarActivity";

    EditText etEditName, etEditCarMake, etEditCarModel, etEditYear,etEditPassengers, etEditSizeType,
            etEditAddress, etEditDescription, etEditRate;
    Button btnEditSave, btnEditCamera;
    ImageView ivEditPreview;
    TextView tvEditClose;
    Car car;
    Context context;
    TextInputLayout tilPrice;


    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";
    File photoFile;
    ParseFile image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "entered EditCarActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        context = this;

        etEditName = findViewById(R.id.etCarName);
        etEditCarMake = findViewById(R.id.etCarMake);
        etEditCarModel = findViewById(R.id.etCarculatorModel);
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

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();

        etEditName.setText(car.getName());
        etEditCarMake.setText(car.getMake());
        etEditCarModel.setText(car.getModel());
        etEditYear.setText(car.getYear());
        etEditRate.setText(car.getRate());
        etEditPassengers.setText(car.getPassengers());
        etEditSizeType.setText(car.getSizeType());
        etEditDescription.setText(car.getDescription());
        etEditAddress.setText(car.getAddress());
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
//                etEditName, etEditCarMake, etEditCarModel, etEditYear,etEditPassengers, etEditSizeType,
//                        etEditAddress, etEditDescription, etEditRate;
                String name = etEditName.getText().toString();
                String make = etEditCarMake.getText().toString();
                String model = etEditCarModel.getText().toString();
                String year = etEditYear.getText().toString();
                String price = etEditRate.getText().toString();
                String passengerCount = etEditPassengers.getText().toString();
                String sizeType = etEditSizeType.getText().toString();
                String description = etEditDescription.getText().toString();
                String address = etEditAddress.getText().toString();


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
                    editCar(name, make, model, year, price, passengerCount, sizeType, description,
                            address, photoFile);
                }
            }
        });

        tvEditClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Close button clicked");
//                Intent i = new Intent(EditCarActivity.this, UserCarFeedActivity.class);
                Intent i = new Intent(EditCarActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        tilPrice.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditCarActivity.this, CarculatorActivity.class);
                i.putExtra("make", etEditCarMake.getText().toString());
                i.putExtra("model", etEditCarModel.getText().toString());
                i.putExtra("year", etEditYear.getText().toString());
//                i.putExtra("price", etPrice.getText().toString());
                i.putExtra("passengers", etEditPassengers.getText().toString());
                i.putExtra("sizetype", etEditSizeType.getText().toString());
                i.putExtra("address", etEditAddress.getText().toString());
                startActivity(i);
            }
        });

    }

    private void editCar(String name, String make, String model, String year, String rate,
                         String passengers, String sizeType, String description, String address,
                         File photoFile) {
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);

        // Retrieve the object by id
        query.getInBackground(car.getObjectId(), new GetCallback<Car>() {
            @Override
            public void done(Car car, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Could not save edits to car");
                } else {
                    Log.i(TAG, "Car was saved to backend");
                    car.setDescription(description);
                    if (photoFile != null){
                        image = new ParseFile(photoFile);
                    }
                    car.setDescription(description);
                    car.setImage(image);
                    car.setRate(rate);
                    car.setModel(model);
                    car.setName(name);
                    car.setMake(make);
                    car.setYear(year);
                    car.setPassengers(passengers);
                    car.setSizeType(sizeType);
                    car.setAddress(address);
                    Geocoder g = new Geocoder(context);
                    double lat, lng;
                    try {
                        ArrayList<Address> addresses = (ArrayList<Address>) g.getFromLocationName(address, 50);
                        for(Address add : addresses){
                            double longitude = add.getLongitude();
                            double latitude = add.getLatitude();
                            Log.i(TAG, "Latitude: " + latitude);
                            Log.i(TAG, "Longitude: " + longitude);
                        }
                        lat = addresses.get(0).getLatitude();
                        lng = addresses.get(0).getLongitude();
                        ParseGeoPoint gp = new ParseGeoPoint(lat, lng);
                        car.setAddressGeoPoint(gp);
                    } catch (IOException ie){
                        Log.e(TAG, "geocoder failed");
                        ie.printStackTrace();
                    }
                    car.saveInBackground();
                    Toast.makeText(EditCarActivity.this, "Edits to car were saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void launchCamera() {
        Log.i(TAG, "camera launched");
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

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

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}