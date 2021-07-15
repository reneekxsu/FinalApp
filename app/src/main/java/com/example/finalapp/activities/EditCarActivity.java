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

import com.bumptech.glide.Glide;
import com.example.finalapp.MainActivity;
import com.example.finalapp.models.ParcelableCar;
import com.example.finalapp.R;
import com.example.finalapp.models.BitmapScaler;
import com.example.finalapp.models.Car;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;

public class EditCarActivity extends AppCompatActivity {

    public static final String TAG = "EditCarActivity";

    EditText etEditCarModel;
    EditText etEditDescription;
    EditText etEditRate;
    Button btnEditSave;
    ImageView ivEditPreview;
    Button btnEditCamera;
    TextView tvEditClose;
    Car car;

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public String photoFileName = "photo.jpg";
    File photoFile;
    ParseFile image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);

        etEditCarModel = findViewById(R.id.etEditCarModel);
        etEditDescription = findViewById(R.id.etEditDescription);
        etEditRate = findViewById(R.id.etEditRate);
        btnEditSave = findViewById(R.id.btnEditSave);
        ivEditPreview = findViewById(R.id.ivEditPreview);
        btnEditCamera = findViewById(R.id.btnEditCamera);
        tvEditClose = findViewById(R.id.tvEditClose);

        car = ((ParcelableCar) Parcels.unwrap(getIntent().getParcelableExtra(ParcelableCar.class.getSimpleName()))).getCar();

        etEditCarModel.setText(car.getModel());
        etEditDescription.setText(car.getDescription());
        etEditRate.setText(car.getRate());
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

        btnEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etEditDescription.getText().toString();
                String rate = etEditRate.getText().toString();
                String name = etEditCarModel.getText().toString();
                if (description.isEmpty()){
                    Toast.makeText(EditCarActivity.this, "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (rate.isEmpty()){
                    Toast.makeText(EditCarActivity.this, "Rate cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (name.isEmpty()){
                    Toast.makeText(EditCarActivity.this, "Model name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if ((image == null) && (photoFile == null || ivEditPreview.getDrawable() == null)){
                        Toast.makeText(EditCarActivity.this, "No image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editCar(name, description, rate, currentUser, photoFile);
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

    }

    private void editCar(String model, String description, String rate, ParseUser currentUser, File photoFile) {
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
                    car.setImage(image);
                    car.setOwner(currentUser);
                    car.setRate(rate);
                    car.setModel(model);
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