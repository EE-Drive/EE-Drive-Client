package com.example.ee_drive_client.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.model.GPS;
import com.example.ee_drive_client.model.OBDData;
import com.example.ee_drive_client.model.Point;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrivingController {
    GPSHandler gpsHandler;
    OBDHandler obdHandler;
    DriveData driveData;
    final Handler handler = new Handler();
    public DrivingController(MainActivity activity) {

        this.obdHandler = new OBDHandler(activity);
        this.driveData=DriveData.getInstance();
    }


    public void onStart(MainActivity view) {
        //location manager
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(view), view);
        gpsHandler.startLocationUpdates();
        gpsHandler.gpsData.observeForever(new Observer<GPS>() {
            @Override
            public void onChanged(GPS gps) {
                String msg = "obsereved Location: " +
                       Double.toString(gps.getAltitude()) + "," +
                        Double.toString(gps.getLongitude());
             //   Toast.makeText(view, msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent = new Point(gps.getLongitude(), gps.getAltitude());
                if (driveData.getPointsSize() == 0) {
                    driveData.addPoint(pointCurrent);
                } else {
                    if (driveData.getLastPoint().getLat() != gps.getLongitude()) {
                        driveData.points.add(pointCurrent);
                    }
                }
            }
        });

        final int delay = 420000 ; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                JSONObject jsonObject = jsonObject = driveData.toJson();
                JsonHandler jsonHandler = new JsonHandler(jsonObject);
                jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObject);
                handler.postDelayed(this, delay);
            }
        }, delay);
    }


    public void onConnect(MainActivity view, Context context) {
        obdHandler.connect(view, context);
        obdHandler.obdLiveData.observeForever(new Observer<OBDData>() {
            @Override
            public void onChanged(OBDData obdData) {
                driveData.getRecordingData().postValue(true);
                if (driveData.getPointsSize() != 0) {
                    driveData.addInfoToLastPoint(obdData);
                    driveData.getSpeed().postValue(obdData.getSpeed());
                }
            }
        });
    }


}