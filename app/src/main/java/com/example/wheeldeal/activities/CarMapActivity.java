package com.example.wheeldeal.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.wheeldeal.R;
import com.example.wheeldeal.models.Car;
import com.example.wheeldeal.models.MarkerCarCountHolder;
import com.example.wheeldeal.models.ParcelableCar;
import com.example.wheeldeal.utils.QueryClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.ParseException;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CarMapActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    public static final String TAG = "CarMapActivity";
    Hashtable<LatLng, MarkerCarCountHolder> markerLookup = new Hashtable<LatLng, MarkerCarCountHolder>();

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    ArrayList<Car> allCars;
    QueryClient queryClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_map);

        allCars = new ArrayList<>();

        queryClient = new QueryClient();

        ArrayList<ParcelableCar> parcelableCars = (ArrayList<ParcelableCar>) Parcels.unwrap(getIntent().getParcelableExtra("ParcelableCars"));
        for (ParcelableCar parcelableCar : parcelableCars){
            allCars.add(parcelableCar.getCar());
        }

        for (Car car : allCars){
            Log.i(TAG, "each car in map: " + car.getModel());
        }

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
//            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                public boolean onMarkerClick(Marker marker) {
//                    // Handle marker click here
//                }
//            });
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i(TAG, "window clicked: " + marker.getTitle());
                    if (!marker.getTitle().equals("I am here")) {
                        queryClient.fetchCarsWithAddress(new FindCallback<Car>() {
                            @Override
                            public void done(List<Car> cars, ParseException e) {
                                if (cars.size() == 1) {
                                    Log.i(TAG, "one car at this address");
                                    ParcelableCar pc = new ParcelableCar(cars.get(0));
                                    Intent i = new Intent(CarMapActivity.this, CarDetailsActivity.class);
                                    i.putExtra(ParcelableCar.class.getSimpleName(), Parcels.wrap(pc));
                                    startActivity(i);
                                    finish();
                                } else {
                                    Log.i(TAG, "multiple cars at this address");
                                    ArrayList<ParcelableCar> parcelableCars = new ArrayList<ParcelableCar>();
                                    for (Car car : cars) {
                                        Log.i(TAG, "Car on textview click: " + car.getModel());
                                        parcelableCars.add(new ParcelableCar(car));
                                    }
                                    Intent i = new Intent(CarMapActivity.this, SameAddressCarsActivity.class);
                                    i.putExtra("ParcelableCars", Parcels.wrap(parcelableCars));
                                    startActivity(i);
                                    finish();
                                }
                                for (Car car : cars) {
                                    Log.i(TAG, "car with address: " + car.getMake() + " at " + car.getAddress());
                                }
                            }
                        }, marker.getSnippet());
                    }
                }
            });
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

            CarMapActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            CarMapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CarMapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayLocation();

        CarMapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    private void displayLocation() {
        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            map.animateCamera(cameraUpdate);
            MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");
            map.addMarker(options);
            Geocoder g = new Geocoder(this);
            for (Car car : allCars){
                Log.i(TAG, "car in allCars: " + car.getMake());
                double lat;
                double lng;
                try {
                    ArrayList<Address> addresses = (ArrayList<Address>) g.getFromLocationName(car.getAddress(), 50);
                    for(Address add : addresses){
                        double longitude = add.getLongitude();
                        double latitude = add.getLatitude();
                        Log.i(TAG, "Latitude: " + latitude);
                        Log.i(TAG, "Longitude: " + longitude);
                    }
                    lat = addresses.get(0).getLatitude();
                    lng = addresses.get(0).getLongitude();
                    LatLng carLatLng = new LatLng(lat, lng);
                    MarkerCarCountHolder lookup = markerLookup.get(carLatLng);
                    if (lookup == null){
                        MarkerOptions markerOption = new MarkerOptions().position(carLatLng).title("1 car found").snippet(car.getAddress());
                        Marker marker = map.addMarker(markerOption);
                        markerLookup.put(carLatLng, new MarkerCarCountHolder(marker, 1));
                    } else {
                        int count = lookup.getCount();
                        Log.i(TAG, "count before " + count);
                        lookup.incrementCount();
                        count = lookup.getCount();
                        Log.i(TAG, "count incremented to " + count);
                        lookup.getMarker().setTitle(count + " cars found");
                    }


                } catch (IOException e) {
                    Log.e(TAG, "geocoder failed");
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        Log.i(TAG, "location changed");
        // GPS may be turned off
        if (location == null) {
            return;
        }

        if (mCurrentLocation == null){
            mCurrentLocation = location;
            String msg = "Updated Location: " +
                    Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude());
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            Log.i(TAG, msg);
            displayLocation();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

}

