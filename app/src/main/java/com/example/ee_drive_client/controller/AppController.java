package com.example.ee_drive_client.controller;

import android.content.Context;
import android.util.Log;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class AppController {
    //Variables
    private MainActivity mainView;
    private DrivingController drivingController;
    private String error="";
    private SendToServer sendToServer;


    public AppController(MainActivity mainView) throws IOException {
        drivingController=new DrivingController(mainView);
        sendToServer=new SendToServer();
        this.mainView = mainView;
    }
    public void onStart(MainActivity view) throws JSONException, UnirestException, IOException {
        drivingController.onStart(view);
        this.mainView=view;
    }
    public void onObdConnect(MainActivity view,Context context)  {
        drivingController.onConnect(view, context.getApplicationContext());
    }
    public void updateError(String error){
        this.error=error;
    }

    public String getError() {
        return error;
    }

    public void initializeModelsAndRoutes() throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Route> routes = new ArrayList<>();
                ArrayList<OptimalModel> models=new ArrayList<>();
                try {
                    routes = sendToServer.getAllRoutesFromServer();
                    models = sendToServer.getAllOptimalModelsFromServer();
                } catch (IOException  | UnirestException  | JSONException e) {
                    e.printStackTrace();
                }
                for ( Route route:routes
                ) {
                    try {
                        RepositoryCar.getInstance().insertRoute(route);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                for (OptimalModel model:models
                     ) {
                    try {
                        RepositoryCar.getInstance().insertOptimalModel(model);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
