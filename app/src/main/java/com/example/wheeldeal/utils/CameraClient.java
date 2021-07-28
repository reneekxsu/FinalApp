package com.example.wheeldeal.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class CameraClient {
    Context context;
    public CameraClient(Context context){
        this.context = context;
    };
    public File getPhotoFileUri(String fileName, String tag) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(tag, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}
