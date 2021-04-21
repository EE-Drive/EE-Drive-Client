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
    DriveData driveData = DriveData.getInstance();
    SendToServer sendToServer = new SendToServer();
    Thread thread;
    final Handler handler = new Handler();
    Calculator calculator = new Calculator();
    Double currentFuel;
    Boolean driveInProcess=false;
    JSONObject response;
    String driveId;
    public DrivingController(MainActivity activity) throws IOException {

        this.obdHandler = new OBDHandler(activity);
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
                //      Toast.makeText(view, msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent = new Point(gps.getLatitude(), gps.getLongitude());
                if (driveData.getPointsSize() == 0) {
                    driveData.addPoint(pointCurrent);
                } else {
                    if (driveData.getLastPoint().getLat() != gps.getLongitude()) {
                        driveData.points.add(pointCurrent);
                    }
                }
            }
        });


        final int delay = 10000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                //   writeData(driveData);
                JSONObject jsonObjectToServer = driveData.toJsonServerStartOfDrive();
                JSONObject jsonObjectTOFile = driveData.toJsonSaveFile();
                JsonHandler jsonHandler = new JsonHandler(jsonObjectToServer);
                jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObjectToServer);
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(driveInProcess==false){
                               response= sendToServer.sendStartOfDriveToServerAndGetDriveId(jsonObjectToServer);
                                driveInProcess=true;
                                driveId = (String) response.get("driveId");
                            }else{
                                Log.d("here","here");

                                sendToServer.sendDataTOExsistinDrive(jsonObjectToServer,driveId);
                            }
                        } catch (UnirestException | JSONException exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                thread.start();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }


    public void onConnect(MainActivity view, Context context) {
        obdHandler.connect(view, context);
        obdHandler.obdLiveData.observeForever(new Observer<OBDData>() {
            @Override
            public void onChanged(OBDData obdData) {
    //            Log.d("Type",obdData.getmType());
//                if (obdData.getFuel() == 0 && obdData.getmMaf() == 0) {  //IF obd is rpm
//                    obdData.setFuel(calculator.calcFuel(calculator.calcMaf(obdData.getRpm(), obdData.getmMap(), obdData.getmIat())));
//                } else if (obdData.getFuel() == 0 && obdData.getmMaf() != 0) { //If obd is maf
//                    obdData.setFuel(calculator.calcFuel(obdData.getmMaf()));
//
//                }
                Double fuel;
                fuel = calculator.calcFuel(calculator.calcMaf(obdData.getRpm(), obdData.getmMap(), obdData.getmIat(),view));
                if (currentFuel != fuel) {
                    obdData.setFuel(fuel);
                    currentFuel = fuel;
                }


                driveData.getRecordingData().postValue(true);
                if (driveData.getPointsSize() != 0) {
                    driveData.addInfoToLastPoint(obdData);
                    driveData.getSpeed().postValue(obdData.getSpeed());
                    driveData.getFuel().postValue(obdData.getFuel());
                }
            }
        });
    }

    public void onStop() {
        //      writeData(driveData);
        driveData.getRecordingData().postValue(false);
        //TODO: obdhandler.disconnect
        //TODO: write remaining data to the json file. Reset drive data info (because he is a singelton)
        //TODO: remove observers
        obdHandler.obdLiveData.removeObserver(new Observer<OBDData>() {

            @Override
            public void onChanged(OBDData obdData) {
                Log.d("Disconnect", "No longer fetting obd information");
            }
        });

    }

//    public void writeData(DriveData driveData){
//        JSONObject jsonObjectToServer = driveData.toJsonServerStartOfDrive();
//        JSONObject jsonObjectTOFile = driveData.toJsonSaveFile();
//        JsonHandler jsonHandler = new JsonHandler(jsonObjectToServer);
//        jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObjectToServer);
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    sendToServer.sendStartOfDriveToServerAndGetDriveId(jsonObjectToServer);
//                } catch (UnirestException exception) {
//                    exception.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }


}