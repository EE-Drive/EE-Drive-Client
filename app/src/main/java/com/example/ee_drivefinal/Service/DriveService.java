package com.example.ee_drivefinal.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;

import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Model.GPS;
import com.example.ee_drivefinal.Model.Point;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Repositories.GPSHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.View.DrivingActivity;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

public class DriveService extends Service {

    //Variables
    private GPSHandler gpsHandler;
    private DriveData driveData;
    private Notification notification;


    public DriveService() throws IOException {
        driveData = DriveData.getInstance();
        gpsHandler = new GPSHandler(LocationServices.getSettingsClient(GlobalContextApplication.getContext()));
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
                // Toast.makeText(GlobalContextApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
                Point pointCurrent = new Point(gps.getLatitude(), gps.getLongitude());
                //   DriveAssistant.getInstance().setCurrentX(gps.getLatitude());
                // DriveAssistant.getInstance().setCurrentY(gps.getLongitude());
                if (driveData.getDriveInProcess() == true) {
                    if (driveData.getPoints().size() == 0) {
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
        stopSelf();
        super.onDestroy();
    }

}
