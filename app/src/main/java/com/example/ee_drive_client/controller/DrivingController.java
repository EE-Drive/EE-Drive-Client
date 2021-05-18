package com.example.ee_drive_client.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.model.GPS;
import com.example.ee_drive_client.model.OBDData;
import com.example.ee_drive_client.model.Point;
import com.example.ee_drive_client.repositories.Calculator;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.service.DriveService;
import com.google.android.gms.location.LocationServices;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DrivingController {
    GPSHandler gpsHandler;
    OBDHandler obdHandler;
    DriveData driveData;
    SendToServer sendToServer = new SendToServer();
    Thread thread;
    Thread startThread;
    final Handler handler = new Handler();
    Calculator calculator;
    Double currentFuel;
    JSONObject response;
    String driveId;
    MainActivity mainActivity;
    Intent intent;

    public DrivingController(MainActivity activity) throws IOException {
        driveData = DriveData.getInstance();
        obdHandler = new OBDHandler(activity);
   //     gpsHandler = new GPSHandler(LocationServices.getSettingsClient(activity));
        calculator = new Calculator();
        mainActivity = activity;
    }


    public void onStart(MainActivity view) {
        intent = new Intent(GlobalContextApplication.getContext(), DriveService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mainActivity.startForegroundService(intent);
        } else {
            mainActivity.startService(intent);
        }
        JSONObject jsonObjectToServer = driveData.toJsonServerStartOfDrive();
        startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    driveData.setId(sendToServer.sendStartOfDriveToServerAndGetDriveId(jsonObjectToServer).getString("createdItemId"));
                } catch (UnirestException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        startThread.start();


//        final int delay = 420000; // 1000 milliseconds == 1 second
//        handler.postDelayed(new Runnable() {
//            public void run() {
//             //   writeData(driveData);
//                if (driveData.driveInProcess == true) {
//                    writeData(driveData);
//                } else {
//                    Toast.makeText(view, "No drive in progress", Toast.LENGTH_SHORT).show();
//                }
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
    }


    public void onConnect(MainActivity view, Context context) {
        driveData.driveInProcess = true;
        driveData.getRecordingData().postValue(true);
        obdHandler.connect(view, context);
        obdHandler.obdLiveData.observeForever(new Observer<OBDData>() {
            @Override
            public void onChanged(OBDData obdData) {
                Double fuel;
                fuel = calculator.calcFuel(calculator.calcMaf(obdData.getRpm(), obdData.getmMap(), obdData.getmIat(), view));
                if (currentFuel != fuel) {
                    obdData.setFuel(fuel);
                    currentFuel = fuel;
                    Toast.makeText(GlobalContextApplication.getContext(), Double.toString(fuel), Toast.LENGTH_LONG);
                }
                if (driveData.getPointsSize() != 0) {
                    driveData.addInfoToLastPoint(obdData);
                    driveData.getSpeed().postValue(obdData.getSpeed());

                    driveData.getFuel().postValue(obdData.getFuel());
                }
                if (driveData.driveInProcess == false) {
                    obdHandler.obdLiveData.removeObserver(this);
                }
            }
        });
    }

    public void onStop() {
        mainActivity.stopService(new Intent(GlobalContextApplication.getContext(), DriveService.class));
        if (driveData.driveInProcess == true)
            writeData(driveData);
        driveData.resetData();
        driveData.driveInProcess = false;
        driveData.getRecordingData().postValue(false);
    //    gpsHandler.stopLocationChanged();
    }

    public void writeData(DriveData driveData) {

        Log.d("Data", "Writing data");
        JSONObject jsonObjectToServer = driveData.toJsonSaveFile();
        JSONObject jsonObjectTOFile = driveData.toJsonSaveFile();
        JsonHandler jsonHandler = new JsonHandler(jsonObjectToServer);
        jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObjectToServer);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendToServer.sendDataTOExsistinDrive(jsonObjectToServer, driveData.getId());
                } catch (UnirestException | JSONException exception) {
                    exception.printStackTrace();
                }
            }
        });
        thread.start();
    }


}