package com.example.ee_drive_client.model;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Point {
    double lat = 0f;
    double lang = 0f;
    public ArrayList<Double> fuelCons = new ArrayList<Double>();
    public ArrayList<Integer> speeds = new ArrayList<Integer>();

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }

    public ArrayList<Double> getFuelCons() {
        return fuelCons;
    }

    public void setFuelCons(ArrayList<Double> fuelCons) {
        this.fuelCons = fuelCons;
    }

    public ArrayList<Integer> getSpeeds() {
        return speeds;
    }

    public void setSpeeds(ArrayList<Integer> speeds) {
        this.speeds = speeds;
    }

    public Point(double lat, double lang) {
        this.lat = lat;
        this.lang = lang;
    }

    public void append(double speed) {
        this.fuelCons.add(speed);
    }

    public void append(int speed) {
        this.speeds.add(speed);
    }

    @Override
    public String toString() {
        return "{"+
                "lat:" + lat +
                ", long:" + lang +
                ", fuelCons:" + fuelCons +
                ", speeds:" + speeds +
                '}';
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {

            json.put("lat", Double.toString(lat));
            json.put("long",Double.toString(lang));
            json.put("fuelCons", fuelCons);
            json.put("speeds", speeds);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public void appendFuel(double speed) {
        this.fuelCons.add(speed);
    }
}