package com.example.ee_drive_client.repositories;

import android.content.Context;
import android.util.Log;

public class Calculator {
    //Most recent drive data

    private int mSpeed = 0;
    private double mFuel = 0;
    private double mMaf = 0;
    private double mRpm = 0;
    private double mMap = 0;
    private double mIat = 0;
    double R = 8.314;
    double MMAir = 28.97;
    double MILLIS_IN_HOUR = 3600000;
    double DEFAULT_VOLUMETRIC_EFFICIENCY = 80; //percent



    private double[] mFuelInfo;
    private double mEngineDisp = 0;

    public Calculator(int mSpeed, double mFuel, double mMaf, double mRpm, double mMap, double mIat) {
        this.mSpeed = mSpeed;
        this.mFuel = mFuel;
        this.mMaf = mMaf;
        this.mRpm = mRpm;
        this.mMap = mMap;
        this.mIat = mIat;
    }

    public Calculator() {

    }

    public double calcFuel(double maf) {
        return (maf * 3600) / (14.7 * 820);
    }


    public double calcMaf(double rpm, double getmMap, double getmIat, Context context) {
        mIat=getmIat+273.15; //converting celsius to kelvin
        mMap=((rpm*getmMap)/mIat)/2;
        Log.d("Engine",Integer.toString(SharedPrefHelper.getInstance(context).getEngine()));
        return ((mMap/60)*(DEFAULT_VOLUMETRIC_EFFICIENCY/100)*SharedPrefHelper.getInstance(context).getEngine()*(MMAir/R))/1000;
    }
}


