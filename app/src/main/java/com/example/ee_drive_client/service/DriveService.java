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
import com.example.ee_drive_client.controller.OptimalModelHandler;
import com.example.ee_drive_client.controller.SendToServer;
import com.example.ee_drive_client.model.DriveAssistant;
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

    private GPSHandler gpsHandler;
    private OBDHandler obdHandler;
    private DriveData driveData;
    private DriveAssistant driveAssistant = DriveAssistant.getInstance();
    private Calculator calculator;
    private SendToServer sendToServer = new SendToServer();
    private Thread thread, backgroundThread;
    private Notification notification;
    private Observer gpsObserver;
    private final Handler handler = new Handler();
    public int countForRecalcualte, countForInstructor;
    public boolean createdFirstModel = false;

    public DriveService() throws IOException {
        driveData = DriveData.getInstance();
        //obdHandler = new OBDHandler(GlobalContextApplication.getContext());
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(GlobalContextApplication.getContext()));
        //calculator = new Calculator();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent1 = new Intent(this, MainActivity.class);
        this.countForRecalcualte = 0;
        this.countForInstructor = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        notification = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("EE drive")
                .setContentText("EE drive is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1, notification);
        new Thread(() -> {
            //TODO: while drivedata.driveinprocess==true
            while (true) {
                try {
                    Thread.sleep(3000);
                    if (this.countForInstructor == 6 && createdFirstModel == false) {
                        OptimalModelHandler optimalModelHandler = new OptimalModelHandler();
                        createdFirstModel = optimalModelHandler.getStartingPointAndCreateModel();
                        Log.d("created", "Model");
                    }
                    if (this.countForInstructor == 8 && createdFirstModel == true) {
                        OptimalModelHandler optimalModelHandler2 = new OptimalModelHandler();
                        double x = DriveAssistant.getInstance().getCurrentX();
                        double y = DriveAssistant.getInstance().getCurrentY();
                        Log.d("id null", Double.toString(x));
                        Log.d("id null", Double.toString(y));
                        if (driveAssistant.isVertexFound()) {
                            driveAssistant.startInstructor();
                        }
                        this.countForInstructor = 6;
                    }
                    if (this.countForRecalcualte == 13) {
                        createdFirstModel = driveAssistant.reCalculatedVertex();
                        this.countForRecalcualte = 0;
                        if (createdFirstModel) {
                            driveData.setDriverAssist(true);
                            driveData.getDriveAssist().postValue(true);

                        }else{
                            driveAssistant.changeSpeedType().postValue("No Model");
                            driveData.setDriverAssist(false);
                            driveData.getDriveAssist().postValue(false);
                        }
                    }
                    if (driveData.driveInProcess == true)
                        driveData.writeData(driveData);
                    //        Toast.makeText(GlobalContextApplication.getContext(), "No drive in progress", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException | JSONException | IOException exception) {
                    exception.printStackTrace();
                }
                this.countForRecalcualte++;
                this.countForInstructor++;
            }
        }).start();
//        final int delay = 5000; // 1000 milliseconds == 1 second
//        handler.postDelayed(new Runnable() {
//            public void run() {
//
//                if (driveData.driveInProcess == false) {
//                    try {
//                        driveData.writeData(driveData);
//                    } catch (IOException exception) {
//                        exception.printStackTrace();
//                    }
//                } else {
//                    //        Toast.makeText(GlobalContextApplication.getContext(), "No drive in progress", Toast.LENGTH_SHORT).show();
//                }
//
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
        gpsHandler.startLocationUpdates();
        gpsHandler.gpsData.observeForever(new Observer<GPS>() {
            @Override
            public void onChanged(GPS gps) {
                String msg = "obsereved Location: " +
                        Double.toString(gps.getLatitude()) + "," +
                        Double.toString(gps.getLongitude());
                msg = Integer.toString(driveAssistant.getCurrentRecommendedSpeed());
                // Toast.makeText(GlobalContextApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent = new Point(gps.getLatitude(), gps.getLongitude());
                //   DriveAssistant.getInstance().setCurrentX(gps.getLatitude());
                // DriveAssistant.getInstance().setCurrentY(gps.getLongitude());
                if (driveData.driveInProcess == true) {
                    if (driveData.getPointsSize() == 0) {
                        driveData.addPoint(pointCurrent);
                    } else {
                        if (driveData.getLastPoint().getLang() != gps.getLongitude()) {
                            driveData.points.add(pointCurrent);
                            Toast.makeText(GlobalContextApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
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
            //Variables
            NotificationManager manager = getSystemService(NotificationManager.class);
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
//        thread.interrupt();
        stopSelf();
        super.onDestroy();
    }


}
