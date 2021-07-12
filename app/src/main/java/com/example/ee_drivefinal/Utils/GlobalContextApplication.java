package com.example.ee_drivefinal.Utils;

import android.app.Application;
import android.content.Context;

public class GlobalContextApplication extends Application {

    //Variables
    public static GlobalContextApplication instance;

    public static GlobalContextApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
