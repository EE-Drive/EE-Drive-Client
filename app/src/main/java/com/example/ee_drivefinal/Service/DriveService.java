package com.example.ee_drivefinal.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Model.GPS;
import com.example.ee_drivefinal.Model.OptimalModel;
import com.example.ee_drivefinal.Model.Point;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Repositories.GPSHandler;
import com.example.ee_drivefinal.Repositories.ServerHandler;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.example.ee_drivefinal.View.DrivingActivity;
import com.google.android.gms.location.LocationServices;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;

public class DriveService extends Service {

    //Variables
    private GPSHandler gpsHandler;
    private DriveData driveData;
    private OptimalModel optimalModel;
    private Notification notification;
    private ServerHandler serverHandler;
    private Thread serverThread;
    private Thread modelThread;
    final Handler handler = new Handler();


    public DriveService() throws IOException {
        driveData = DriveData.getInstance();
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(GlobalContextApplication.getContext()));
        serverHandler = new ServerHandler();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent1 = new Intent(this, DrivingActivity.class);
        //TODO: Test line
        createNotificationChannel();
        notification = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("EE drive")
                .setContentText("EE drive is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(1, notification);
        gpsHandler.startLocationUpdates();
        gpsHandler.gpsData.observeForever(new Observer<GPS>() {
            @Override
            public void onChanged(GPS gps) {
                String msg = "obsereved Location: " +
                        Double.toString(gps.getLatitude()) + "," +
                        Double.toString(gps.getLongitude());
                Point pointCurrent = new Point(gps.getLatitude(), gps.getLongitude());
                if (optimalModel != null && driveData.getDriverAssist()) {
                    Log.d("model2", "true");
                    if (optimalModel.isInModel()) {
                        optimalModel.setCurrentVertexIndex(gps.getLatitude(), gps.getLongitude());
                        Log.d("Vertex", Integer.toString(optimalModel.getCurrentVertex()));
                        if (optimalModel.getCurrentVertex() != -1) {
                            Log.d("Speed!", Integer.toString(optimalModel.getCurrentSpeed()));
                            driveData.getCurrentRecommendedSpeed().postValue(optimalModel.getCurrentSpeed());
                        } else {
                            driveData.getCurrentRecommendedSpeed().postValue(-1);
                        }
                    }
                }
                if (DriveData.getInstance().getDriveInProcess()) {
                    Log.d("Drive in process2", "true");
                    if (driveData.getPoints().size() == 0) {
                        driveData.addPoint(pointCurrent);
                    } else {
                        if (driveData.getLastPoint().getLang() != gps.getLongitude()) {
                            driveData.points.add(pointCurrent);
                        }
                    }
                } else {
                    //TODO: Change drive in process to beginning of the window
                    gpsHandler.gpsData.removeObserver(this);
                }
            }
        });
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        serverThread.start();

        //TODO:FIX here
        final int delay = 20000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d("DriveAssist", Boolean.toString(DriveData.getInstance().getDriverAssist()));
                if (DriveData.getInstance().getDriverAssist()) {
                    modelThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (driveData.getPoints() != null && driveData.getPoints().size() > 1) {
                                    optimalModel = new OptimalModel(serverHandler.getOptimalModel(SharedPrefHelper.getInstance().getId(), driveData.getLastPoint().getLat(), driveData.getLastPoint().getLang()));
                                    Log.d("In Model", Boolean.toString(optimalModel.isInModel()));
                                }
                            } catch (UnirestException | JSONException exception) {
                                FileHandler.appendLog(exception.toString());
                                exception.printStackTrace();
                            }
                        }
                    });
                    modelThread.start();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    @Override
    public void onDestroy() {
        stopForeground(true);
        gpsHandler.stopLocationChanged();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        stopSelf();
        super.onDestroy();
    }

}
