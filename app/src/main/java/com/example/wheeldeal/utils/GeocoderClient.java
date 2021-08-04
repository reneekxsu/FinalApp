package com.example.wheeldeal.utils;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeocoderClient {
    public interface GeocoderResponseHandler{
        void consumeAddress(ParseGeoPoint geoPoint);
    }
    public static final String TAG = "GeocoderClient";
    Activity activity;
    public GeocoderClient(Activity activity){
        this.activity = activity;
    }
    public void lookupAddress(String address, GeocoderResponseHandler handler){
        Log.i(TAG, "getAddressFromString called");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Geocoder g = new Geocoder(activity);
                double lat, lng;
                ParseGeoPoint gp = null;
                try {
                    ArrayList<Address> addresses = (ArrayList<Address>) g.getFromLocationName(address, 50);
                    for(Address add : addresses){
                        double longitude = add.getLongitude();
                        double latitude = add.getLatitude();
                        Log.i(TAG, "Latitude: " + latitude);
                        Log.i(TAG, "Longitude: " + longitude);
                    }
                    if (addresses.size() != 0){
                        lat = addresses.get(0).getLatitude();
                        lng = addresses.get(0).getLongitude();
                        gp = new ParseGeoPoint(lat, lng);
                    }
                } catch (IOException ie){
                    Log.e(TAG, "geocoder failed", ie);
                }
                final ParseGeoPoint finalGP = gp;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalGP == null){
                            Toast.makeText(activity, "No location found", Toast.LENGTH_SHORT).show();
                        }
                        handler.consumeAddress(finalGP);
                    }
                });
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(runnable);
    }

}
