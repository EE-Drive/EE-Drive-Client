package com.example.ee_drive_client.repositories;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class GlobalContextApplication extends Application {
    public static GlobalContextApplication instance;
    public static GlobalContextApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
