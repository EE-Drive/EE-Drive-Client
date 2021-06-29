package com.example.ee_drive_client.model;

public class OBDData {

    //Variables
    public final static OBDData instance = new OBDData();
    private double latitude = 0, longitude = 0, fuel = 0, mMaf = 0, rpm = 0, mMap = 0, mIat = 0;
    private int speed = 0;
    private String mType = "RPM";

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public double getmMaf() {
        return mMaf;
    }

    public void setmMaf(double mMaf) {
        this.mMaf = mMaf;
    }

    public double getRpm() {
        return rpm;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public double getmMap() {
        return mMap;
    }

    public void setmMap(double mMap) {
        this.mMap = mMap;
    }

    public double getmIat() {
        return mIat;
    }

    public void setmIat(double mIat) {
        this.mIat = mIat;
    }
}
