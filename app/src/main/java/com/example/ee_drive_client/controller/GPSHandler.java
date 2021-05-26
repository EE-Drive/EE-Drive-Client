package com.example.ee_drive_client.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drive_client.model.GPS;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;


import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class GPSHandler {

    HandlerThread handlerThread;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest.Builder builder;
    private SettingsClient settingsClient;
    private Context activity;
   public MutableLiveData<GPS> gpsData=new MutableLiveData<GPS>();

    public GPSHandler(SettingsClient settingsClient)
    {
        // Create the location request to start receiving updates

        this.activity= GlobalContextApplication.getContext();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        settingsClient.checkLocationSettings(locationSettingsRequest);
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
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
       // Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        GPS gpsLocation=new GPS();
        gpsLocation.setLongitude(lastLocation.getLongitude());
        gpsLocation.setLatitude(lastLocation.getLatitude());

        gpsData.postValue(gpsLocation);

    }

    public void stopLocationChanged(){
        getFusedLocationProviderClient(activity).removeLocationUpdates(new LocationCallback());
        Toast.makeText(activity,"Stpopped",Toast.LENGTH_SHORT);
    }

    public LocationRequest getLocationRequest() {
        return mLocationRequest;
    }
}

