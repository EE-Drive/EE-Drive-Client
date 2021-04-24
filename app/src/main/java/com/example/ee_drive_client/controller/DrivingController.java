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
import com.example.ee_drive_client.repositories.Calculator;
import com.google.android.gms.location.LocationServices;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    Boolean driveInProcess = false;
    JSONObject response;
    String driveId;

    public DrivingController(MainActivity activity) throws IOException {
        driveData=DriveData.getInstance();
        obdHandler = new OBDHandler(activity);
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(activity), activity);
        calculator = new Calculator();
    }


    public void onStart(MainActivity view) {
        JSONObject jsonObjectToServer = driveData.toJsonServerStartOfDrive();
        startThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    driveData.setId(sendToServer.sendStartOfDriveToServerAndGetDriveId(jsonObjectToServer).getString("driveId"));
                } catch (UnirestException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        startThread.start();



        final int delay = 10000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                if(driveInProcess==true){
                    writeData(driveData);
                }else{
                    Toast.makeText(view, "No drive in progress", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }


    public void onConnect(MainActivity view, Context context) {
        driveInProcess=true;
        gpsHandler.startLocationUpdates();
        gpsHandler.gpsData.observeForever(new Observer<GPS>() {
            @Override
            public void onChanged(GPS gps) {
                String msg = "obsereved Location: " +
                        Double.toString(gps.getAltitude()) + "," +
                        Double.toString(gps.getLongitude());
            //    Toast.makeText(view, msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent = new Point(gps.getLatitude(), gps.getLongitude());
                if(driveInProcess=true) {
                    if (driveData.getPointsSize() == 0) {
                        driveData.addPoint(pointCurrent);
                    } else {
                        if (driveData.getLastPoint().getLang() != gps.getLongitude()) {
                            driveData.points.add(pointCurrent);
                        }
                    }
                }
            }
        });
        obdHandler.connect(view, context);
        obdHandler.obdLiveData.observeForever(new Observer<OBDData>() {
            @Override
            public void onChanged(OBDData obdData) {
                driveData.getRecordingData().postValue(true);
                //            Log.d("Type",obdData.getmType());
//                if (obdData.getFuel() == 0 && obdData.getmMaf() == 0) {  //IF obd is rpm
//                    obdData.setFuel(calculator.calcFuel(calculator.calcMaf(obdData.getRpm(), obdData.getmMap(), obdData.getmIat())));
//                } else if (obdData.getFuel() == 0 && obdData.getmMaf() != 0) { //If obd is maf
//                    obdData.setFuel(calculator.calcFuel(obdData.getmMaf()));
//
//                }
                Double fuel;
                fuel = calculator.calcFuel(calculator.calcMaf(obdData.getRpm(), obdData.getmMap(), obdData.getmIat(), view));
                if (currentFuel != fuel) {
                    obdData.setFuel(fuel);
                    currentFuel = fuel;
                }


                if (driveData.getPointsSize() != 0) {
                    driveData.addInfoToLastPoint(obdData);
                    driveData.getSpeed().postValue(obdData.getSpeed());
                    driveData.getFuel().postValue(obdData.getFuel());
                }
            }
        });
    }

    public void onStop() {
        if(driveInProcess=true)
        writeData(driveData);
       Log.d("drive id",driveData.getId());
        driveData.resetData();
        driveInProcess=false;
        driveData.getRecordingData().postValue(false);
        gpsHandler.stopLocationChanged();

        //TODO: obdhandler.disconnect
        //TODO: write remaining data to the json file. Reset drive data info (because he is a singelton)
        //TODO: remove observers
        obdHandler.obdLiveData.removeObserver(new Observer<OBDData>() {

            @Override
            public void onChanged(OBDData obdData) {
                Log.d("Disconnect", "No longer getting obd information");
            }
        });

    }

    public void writeData(DriveData driveData) {
        Log.d("Data", "Writing data");
        JSONObject jsonObjectToServer = driveData.toJsonServerStartOfDrive();
        JSONObject jsonObjectTOFile = driveData.toJsonSaveFile();
        JsonHandler jsonHandler = new JsonHandler(jsonObjectToServer);
        jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObjectToServer);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendToServer.sendDataTOExsistinDrive(jsonObjectToServer,driveData.getId());
                } catch (UnirestException | JSONException exception) {
                    exception.printStackTrace();
                }
            }
        });
        thread.start();
    }


}