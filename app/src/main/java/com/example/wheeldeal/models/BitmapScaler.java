package com.example.wheeldeal.models;

import android.graphics.Bitmap;

public class BitmapScaler
{
    // scale and keep aspect ratio
    public static Bitmap scaleToFitWidth(Bitmap map, int width)
    {
        float factor = width / (float) map.getWidth();
        return Bitmap.createScaledBitmap(map, width, (int) (map.getHeight() * factor), true);
    }
}
