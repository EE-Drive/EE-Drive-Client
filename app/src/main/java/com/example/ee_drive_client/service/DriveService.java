package com.example.ee_drive_client.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.R;
import com.example.ee_drive_client.controller.GPSHandler;
import com.example.ee_drive_client.controller.JsonHandler;
import com.example.ee_drive_client.controller.OBDHandler;
import com.example.ee_drive_client.controller.SendToServer;
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.model.GPS;
import com.example.ee_drive_client.model.Point;
import com.example.ee_drive_client.repositories.Calculator;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.google.android.gms.location.LocationServices;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DriveService extends Service {
    NotificationManager manager;
    GPSHandler gpsHandler;
    OBDHandler obdHandler;
    DriveData driveData;
    Calculator calculator;
    SendToServer sendToServer = new SendToServer();
    Thread thread;
    Thread backgroudThread;
    Notification notification;
    Observer gpsObserver;
    final Handler handler = new Handler();

    public DriveService() throws IOException {
        driveData = DriveData.getInstance();
//        obdHandler = new OBDHandler(GlobalContextApplication.getContext());
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(GlobalContextApplication.getContext()));
        //       calculator = new Calculator();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent1 = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        notification = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("EE drive")
                .setContentText("EE drive is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1, notification);
        final int delay = 60000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
//                try {
//                    driveData.writeData(driveData);
//                } catch (IOException exception) {
//                    exception.printStackTrace();
//                }
                if (driveData.driveInProcess == true) {
                    try {
                        driveData.writeData(driveData);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } else {
                    //        Toast.makeText(GlobalContextApplication.getContext(), "No drive in progress", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        gpsHandler.startLocationUpdates();

        gpsHandler.gpsData.observeForever(new Observer<GPS>() {
            @Override
            public void onChanged(GPS gps) {
                String msg = "obsereved Location: " +
                        Double.toString(gps.getLatitude()) + "," +
                        Double.toString(gps.getLongitude());
                //     Toast.makeText(GlobalContextApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent = new Point(gps.getLatitude(), gps.getLongitude());
                if (driveData.driveInProcess == true
                ) {
                    if (driveData.getPointsSize() == 0) {
                        driveData.addPoint(pointCurrent);
                    } else {
                        if (driveData.getLastPoint().getLang() != gps.getLongitude()) {
                            driveData.points.add(pointCurrent);
                        }
                    }
                } else {
                    gpsHandler.gpsData.removeObserver(this);
                }
            }

        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();


        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "ChannelId1", "Foregound notification", NotificationManager.IMPORTANCE_DEFAULT);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //   gpsHandler.gpsData.removeObserver(gpsObserver);
        stopForeground(true);
        gpsHandler.stopLocationChanged();

        stopSelf();
        super.onDestroy();
    }

//    public void writeData(DriveData driveData) {
//        Log.d("Data", "Writing data");
//        JSONObject jsonObjectToServer = driveData.toJsonSaveFile();
//        JSONObject jsonObjectTOFile = driveData.toJsonSaveFile();
//        JsonHandler jsonHandler = new JsonHandler(jsonObjectToServer);
//        jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObjectToServer);
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    sendToServer.sendDataTOExsistinDrive(jsonObjectToServer, driveData.getId());
//                } catch (UnirestException | JSONException exception) {
//                    exception.printStackTrace();
//                }
//            }
//        });
//        thread.start();
//    }

}
