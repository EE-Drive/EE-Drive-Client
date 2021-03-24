package com.example.ee_drive_client.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ee_drive_client.R;
import com.example.ee_drive_client.controller.AppController;
import com.example.ee_drive_client.controller.SendToServer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {
    NavController navController;
    AppController mainController;
    Context context;
    Thread serverThread;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationRequest mLocationRequest;

    public MainActivity() throws IOException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainController=new AppController(this);
        setContentView(R.layout.activity_main);
        //Manifest Permissions
        requestPermissions();
        //Keeps Screen On
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        navController = Navigation.findNavController(this, R.id.mainactivity_navHost);
        NavigationUI.setupActionBarWithNavController(this, navController);

    }

    private void startLocationUpdates() {
       mainController.onStart(this);
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH_ADMIN},
                101);
    }
    public Context getContext(){
        return this.getApplicationContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
           navController.navigateUp();
           return true;
        }
         return NavigationUI.onNavDestinationSelected(item,navController);


    }
}