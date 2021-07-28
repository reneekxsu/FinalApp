package com.example.wheeldeal.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.BitmapScaler;
import com.example.wheeldeal.utils.CameraClient;
import com.example.wheeldeal.utils.FormClient;
import com.example.wheeldeal.utils.GeocoderClient;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class AddOwnCarActivity extends AppCompatActivity {

    public static final String TAG = "RegisterCarActivity";

    private TextInputEditText etName, etCarMake, etCarModel, etYear, etPrice, etPassengers, etSizeType,
            etDescription, etAddress;
    private Button btnCamera, btnRegister;
    private ImageView ivPreview;
    private TextView tvClose;
    String name, make, model, year, price, passengerCount, sizeType, description, address;
    private TextInputLayout tilPrice;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";
    File photoFile;
    FormClient formClient = new FormClient();
    QueryClient queryClient = new QueryClient();
    GeocoderClient geocoderClient;
    CameraClient cameraClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        etName = findViewById(R.id.etCarName);
        etCarMake = findViewById(R.id.etCarMake);
        etCarModel = findViewById(R.id.etCarculatorModel);
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

        geocoderClient = new GeocoderClient(this);
        cameraClient = new CameraClient(this);

        tilPrice.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddOwnCarActivity.this, CarculatorActivity.class);
                i.putExtra("make", etCarMake.getText().toString());
                i.putExtra("model", etCarModel.getText().toString());
                i.putExtra("year", etYear.getText().toString());
                i.putExtra("passengers", etPassengers.getText().toString());
                i.putExtra("sizetype", etSizeType.getText().toString());
                i.putExtra("address", etAddress.getText().toString());
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
                name = etName.getText().toString();
                make = etCarMake.getText().toString();
                model = etCarModel.getText().toString();
                year = etYear.getText().toString();
                price = etPrice.getText().toString();
                passengerCount = etPassengers.getText().toString();
                sizeType = etSizeType.getText().toString();
                description = etDescription.getText().toString();
                address = etAddress.getText().toString();

                if (formClient.isEntryEmpty(name, make, model, year, price, passengerCount, sizeType, description, address)){
                    Toast.makeText(AddOwnCarActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (photoFile == null || ivPreview.getDrawable() == null){
                        Toast.makeText(AddOwnCarActivity.this, "No image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveCar(description, currentUser, photoFile, price, model, name, make, year, passengerCount,
                            sizeType, address);
                }
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Close button clicked");
                Intent i = new Intent(AddOwnCarActivity.this, UserCarFeedActivity.class);
                setResult(20, i);
                finish();
            }
        });
    }

    private void saveCar(String description, ParseUser currentUser, File photoFile, String rate, String model,
                         String name, String make, String year, String passengers, String size, String address) {
        ParseGeoPoint gp = geocoderClient.getAddressFromString(address);
        queryClient.saveCar(description, currentUser, photoFile, rate, model, name, make, year,
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
                            etCarModel.setText("");
                            etPrice.setText("");
                            // set to empty image
                            ivPreview.setImageResource(0);
                        }
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
}