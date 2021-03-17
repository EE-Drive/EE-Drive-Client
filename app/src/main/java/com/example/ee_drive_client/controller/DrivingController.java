package com.example.ee_drive_client.controller;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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

public class DrivingController {
    public DrivingController(MainActivity activity) {

        this.obdHandler = new OBDHandler(activity);
      //  this.pointsArray=new ArrayList<>();
    }

    GPSHandler gpsHandler;
    OBDHandler obdHandler;
  //  DriveData driveData = new DriveData("", false, new CarType("Mazda", "Three", 2004));
  //  ArrayList<Point> pointsArray;
    final Handler handler = new Handler();

    public void onStart(MainActivity view) {
        //location manager
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(view), view);
        gpsHandler.startLocationUpdates();
        gpsHandler.gpsData.observeForever(new Observer<GPS>() {
            @Override
            public void onChanged(GPS gps) {
//                String msg = "obsereved Location: " +
//                        Double.toString(gps.getAltitude()) + "," +
//                        Double.toString(gps.getLongitude());
//                 Toast.makeText(view, msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent=new Point(gps.getLongitude(),gps.getAltitude());
                if(DriveData.getInstance().getPointsSize()==0){

                    DriveData.getInstance().points.add(pointCurrent);
                }
                else{
                    if(DriveData.getInstance().points.get(DriveData.getInstance().points.size()-1).getLang()!=gps.getLongitude()){
                        DriveData.getInstance().points.add(pointCurrent);
                    }
                }
            }
        });



        final int delay = 30000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                JSONObject jsonObject = jsonObject = DriveData.getInstance().toJson();
                JsonHandler jsonHandler = new JsonHandler(jsonObject);
                jsonHandler.saveToFile(DriveData.getInstance().getTimeAndDate(), jsonObject);
                Toast.makeText(view, "Created", Toast.LENGTH_SHORT).show();
                handler.postDelayed(this, delay);
            }
        }, delay);


    }
    public void onConnect(MainActivity view,Context context){

      //  this.obdHandler=new OBDHandler(context.getApplicationContext());
      obdHandler.connect(view,context);
        obdHandler.obdLiveData.observeForever(new Observer<OBDData>() {
            @Override
            public void onChanged(OBDData obdData) {
//                OBDData obdDataCurrent = new OBDData();
//
//                obdDataCurrent.setSpeed(obdData.getSpeed());
//                obdDataCurrent.setFuel(obdData.getFuel());
//                obdDataCurrent.setmIat(obdData.getmIat());
//                obdDataCurrent.setRpm(obdData.getRpm());
//                obdDataCurrent.setmMap(obdData.getmMap());
         //      Toast.makeText(view, Double.toString(obdData.getmIat()), Toast.LENGTH_SHORT).show();

                if(DriveData.getInstance().getPointsSize()!=0){
                  Toast.makeText(view,Double.toString(obdData.getFuel()), Toast.LENGTH_SHORT).show();
                    DriveData.getInstance().points.get(DriveData.getInstance().points.size()-1).speeds.add(obdData.getSpeed());
                    DriveData.getInstance().points.get(DriveData.getInstance().points.size()-1).fuelCons.add(obdData.getFuel());

                }


            }
        });
    }
}