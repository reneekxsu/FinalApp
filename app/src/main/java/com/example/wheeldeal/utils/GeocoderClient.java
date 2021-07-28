package com.example.wheeldeal.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.parse.ParseGeoPoint;

import java.io.IOException;
import java.util.ArrayList;

public class GeocoderClient {
    public static final String TAG = "GeocoderClient";
    Context context;
    public GeocoderClient(Context context){
        this.context = context;
    }
    public ParseGeoPoint getAddressFromString(String address){
        Geocoder g = new Geocoder(context);
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
            lat = addresses.get(0).getLatitude();
            lng = addresses.get(0).getLongitude();
            gp = new ParseGeoPoint(lat, lng);
        } catch (IOException ie){
            Log.e(TAG, "geocoder failed");
            ie.printStackTrace();
        }
        return gp;
    }

}
