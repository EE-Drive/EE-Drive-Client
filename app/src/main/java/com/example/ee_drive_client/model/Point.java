package com.example.ee_drive_client.model;
import java.util.ArrayList;

public class Point {
    double lat = 0f;
    double lang = 0f;
    public ArrayList<Double> fuelCons = new ArrayList<Double>();
    public ArrayList<Integer> speeds = new ArrayList<Integer>();

    public Point(double lat, double lang) {
        this.lat = lat;
        this.lang = lang;
    }

    public Point() {
        this.lat=0;
        this.lang=0;
    }

    public double getLat() {
        return lat;
    }

    public double getLang() {
        return lang;
    }

    public void appendFuel(double speed) {
        this.fuelCons.add(speed);
    }

    public void append(int speed) {
        this.speeds.add(speed);
    }

    @Override
    public String toString() {
        return "{"+
                "lat:" + lat +
                ", lang:" + lang +
                ", fuelCons:" + fuelCons +
                ", speeds:" + speeds +
                '}';
    }
}
