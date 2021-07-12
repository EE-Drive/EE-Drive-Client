package com.example.ee_drivefinal.Model;

public class GPS {
    //Variables
    private double altitude = 0f, longitude = 0f, latitude = 0f;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "GPS{" +
                "altitude=" + altitude +
                ", longitude=" + longitude +
                '}';
    }

    public GPS get() {
        return this;
    }
}
