package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.Model.CarType;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Repositories.Repository;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements CallBackFragment {

    //Variables
    Repository repository;
    private ArrayList<CarType> response;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showScreen();
        try {
            repository = new Repository();
            updateDatabase();
        } catch (JSONException | UnirestException | IOException e) {
            e.printStackTrace();
        }
        //TODO: UPDATE DB
    }

    private void updateDatabase() throws JSONException, UnirestException, IOException {
        response = repository.getAllCarsFromServer();
        for (CarType car : response
        ) {
            repository.insertCarDbOnly(car);
        }
    }

    private void showScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showActivity(MainActivity.class);
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Splash.LifeCycle.Method", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Splash.LifeCycle.Method", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Splash.LifeCycle.Method", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Splash.LifeCycle.Method", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Splash.LifeCycle.Method", "onDestroy");
    }

    @Override
    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();

    }


}