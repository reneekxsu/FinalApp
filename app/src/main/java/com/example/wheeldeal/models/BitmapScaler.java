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


    // scale and keep aspect ratio
    public static Bitmap scaleToFitHeight(Bitmap map, int height)
    {
        float factor = height / (float) map.getHeight();
        return Bitmap.createScaledBitmap(map, (int) (map.getWidth() * factor), height, true);
    }


    // scale and keep aspect ratio
    public static Bitmap scaleToFill(Bitmap map, int width, int height)
    {
        float factorH = height / (float) map.getWidth();
        float factorW = width / (float) map.getWidth();
        float factorToUse = (factorH > factorW) ? factorW : factorH;
        return Bitmap.createScaledBitmap(map, (int) (map.getWidth() * factorToUse),
                (int) (map.getHeight() * factorToUse), true);
    }


    // scale and don't keep aspect ratio
    public static Bitmap stretchToFill(Bitmap b, int width, int height)
    {
        float factorH = height / (float) b.getHeight();
        float factorW = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorW),
                (int) (b.getHeight() * factorH), true);
    }
}
