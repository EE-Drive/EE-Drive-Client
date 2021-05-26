package com.example.ee_drive_client.controller;

import android.content.Context;

import com.example.ee_drive_client.Activities.MainActivity;

import java.io.IOException;

public class AppController {

    MainActivity mainView;
    DrivingController drivingController;
    String error="";

    public AppController(MainActivity mainView) throws IOException {
        drivingController=new DrivingController(mainView);
        this.mainView = mainView;
    }
    public void onStart(MainActivity view){
        drivingController.onStart(view);
        this.mainView=view;
    }
    public void onObdConnect(MainActivity view,Context context){
        drivingController.onConnect(view, context.getApplicationContext());
    }
    public void updateError(String error){
        this.error=error;
    }

    public String getError() {
        return error;
    }
}
