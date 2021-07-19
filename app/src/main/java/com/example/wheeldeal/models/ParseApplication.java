package com.example.wheeldeal.models;

import android.app.Application;

import com.example.wheeldeal.BuildConfig;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    public static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    public static final String CLIENT_KEY = BuildConfig.CLIENT_KEY;

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Car.class);
        ParseObject.registerSubclass(Event.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}
