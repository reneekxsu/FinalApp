package com.example.wheeldeal.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.BitmapScaler;
import com.example.wheeldeal.models.Car;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class RegisterCarActivity extends AppCompatActivity {

    public static final String TAG = "RegisterCarActivity";

    private EditText etName, etCarMake, etCarModel, etYear, etPrice, etPassengers, etSizeType;
    private EditText etDescription, etAddress;
    private Button btnCamera, btnRegister;
    private ImageView ivPreview;
    private TextView tvClose;
    String name, make, model, year, price, passengerCount, sizeType, description, address;
    boolean nameFilled = false;
    boolean makeFilled = false;
    boolean modelFilled = false;
    boolean yearFilled = false;
    boolean priceFilled = false;
    boolean passengerCountFilled = false;
    boolean sizeTypeFilled = false;
    boolean descriptionFilled= false;
    boolean addressFilled = false;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_car);

        etName = findViewById(R.id.etName);
        etCarMake = findViewById(R.id.etCarMake);
        etCarModel = findViewById(R.id.etCarModel);
        etYear = findViewById(R.id.etYear);
        etPrice = findViewById(R.id.etPrice);
        etPassengers = findViewById(R.id.etPassengers);
        etSizeType = findViewById(R.id.etSizeType);
        etDescription = findViewById(R.id.etDescription);
        etAddress = findViewById(R.id.etAddress);
        btnCamera = findViewById(R.id.btnCamera);
        btnRegister = findViewById(R.id.btnRegister);
        ivPreview = findViewById(R.id.ivPreview);
        tvClose = findViewById(R.id.tvClose);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    nameFilled = true;
                    if (!btnRegister.isEnabled() && areAllFieldsFilled()){
                        btnRegister.setEnabled(true);
                    }
                } else {
                    nameFilled = false;
                    btnRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCarMake.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0){
                    makeFilled = true;
                    if (!btnRegister.isEnabled() && areAllFieldsFilled()){
                        btnRegister.setEnabled(true);
                    }
                } else {
                    makeFilled = false;
                    btnRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnRegister.setEnabled(false);

//        btnRegister.setEnabled(true);

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

                if (isEntryEmpty(name, make, model, year, price, passengerCount, sizeType, description, address)){
                    Toast.makeText(RegisterCarActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (photoFile == null || ivPreview.getDrawable() == null){
                        Toast.makeText(RegisterCarActivity.this, "No image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveCar(name, description, price, currentUser, photoFile);
                }
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Close button clicked");
                Intent i = new Intent(RegisterCarActivity.this, UserCarFeedActivity.class);
//                startActivity(i);
//                Intent i = new Intent();
                setResult(20, i);
                finish();
            }
        });
    }

    private void saveCar(String model, String description, String rate, ParseUser currentUser, File photoFile) {
        Log.i(TAG, "saving car");
        Car car = new Car();
        car.setDescription(description);
        car.setImage(new ParseFile(photoFile));
        car.setOwner(currentUser);
        car.setRate(rate);
        car.setModel(model);
        car.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Could not save", e);
                    Toast.makeText(RegisterCarActivity.this, "Could not save", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.i(TAG, "Car was saved to backend");
                    Toast.makeText(RegisterCarActivity.this, "Car was saved", Toast.LENGTH_SHORT).show();
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
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(RegisterCarActivity.this, "com.codepath.fileprovider.FinalApp", photoFile);
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

    boolean isEntryEmpty(String name, String make, String model, String year, String price, String passengerCount,
                         String sizeType, String description, String address){
        return (name.isEmpty() || make.isEmpty() || model.isEmpty() || year.isEmpty() || price.isEmpty() || passengerCount.isEmpty()
                || sizeType.isEmpty() || description.isEmpty() || address.isEmpty());
    }

    boolean areAllFieldsFilled(){
        return (nameFilled && makeFilled && modelFilled && yearFilled && priceFilled &&
                passengerCountFilled && sizeTypeFilled && descriptionFilled && addressFilled);
    }
}