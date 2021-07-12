package com.example.ee_drivefinal.Repositories;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Model.GPS;
import com.example.ee_drivefinal.Model.Point;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class GPSHandler {

    //Variables
    private final long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest.Builder builder;
    private Context activity;
    private DriveData driveData;
    public MutableLiveData<GPS> gpsData = new MutableLiveData<GPS>();

    public GPSHandler(SettingsClient settingsClient) {
        // Create the location request to start receiving updates
        this.activity = GlobalContextApplication.getContext();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        // Create LocationSettingsRequest object using location request
        builder = new LocationSettingsRequest.Builder();
        driveData =DriveData.getInstance();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        settingsClient.checkLocationSettings(locationSettingsRequest);
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    public void onLocationChanged(Location lastLocation) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(lastLocation.getLatitude()) + "," +
                Double.toString(lastLocation.getLongitude());
         Log.d("Location" , msg);
        GPS gpsLocation = new GPS();
        gpsLocation.setLongitude(lastLocation.getLongitude());
        gpsLocation.setLatitude(lastLocation.getLatitude());
        Point pointCurrent = new Point(gpsLocation.getLatitude(), gpsLocation.getLongitude());
        driveData.addPoint(pointCurrent);
       // DriveAssistant.getInstance().setCurrentX(lastLocation.getLatitude());
       // DriveAssistant.getInstance().setCurrentY(lastLocation.getLongitude());
        gpsData.postValue(gpsLocation);

    }

    public void stopLocationChanged() {
        getFusedLocationProviderClient(GlobalContextApplication.getContext()).removeLocationUpdates(new LocationCallback());
        Toast.makeText(activity, "Stopped", Toast.LENGTH_SHORT);
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }
}
