package com.example.finalapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.finalapp.MainActivity;
import com.example.finalapp.R;
import com.example.finalapp.models.BitmapScaler;
import com.example.finalapp.models.Car;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class RegisterCarActivity extends AppCompatActivity {

    public static final String TAG = "RegisterCarActivity";

    private EditText etName;
    private EditText etCarMake;
    private EditText etCarModel;
    private EditText etYear;
    private EditText etPrice;
    private EditText etPassengers;
    private EditText etSizeType;
    private EditText etDescription;
    private EditText etAddress;
    private TextView tvName;
    private TextView tvCarMake;
    private TextView tvCarModel;
    private TextView tvYear;
    private TextView tvPrice;
    private TextView tvPassengers;
    private TextView tvSizeType;
    private TextView tvDescription;
    private TextView tvAddress;
    private Button btnCamera;
    private Button btnRegister;
    private ImageView ivPreview;
    private TextView tvClose;

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
        tvName = findViewById(R.id.tvName);
        tvCarMake = findViewById(R.id.tvCarMake);
        tvCarModel = findViewById(R.id.tvCarModel);
        tvYear = findViewById(R.id.tvYear);
        tvPrice = findViewById(R.id.tvPrice);
        tvPassengers = findViewById(R.id.tvPassengers);
        tvSizeType = findViewById(R.id.tvSizeType);
        tvDescription = findViewById(R.id.tvDescription);
        tvAddress = findViewById(R.id.tvAddress);
        btnCamera = findViewById(R.id.btnCamera);
        btnRegister = findViewById(R.id.btnRegister);
        ivPreview = findViewById(R.id.ivPreview);
        tvClose = findViewById(R.id.tvClose);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String make = etCarMake.getText().toString();
                String model = etCarModel.getText().toString();
                String year = etYear.getText().toString();
                String price = etPrice.getText().toString();
                String passengerCount = etPassengers.getText().toString();
                String sizeType = etSizeType.getText().toString();
                String description = etDescription.getText().toString();
                String address = etAddress.getText().toString();

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
                Intent i = new Intent(RegisterCarActivity.this, MainActivity.class);
//                startActivity(i);
//                Intent i = new Intent();
                setResult(111, i);
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
}