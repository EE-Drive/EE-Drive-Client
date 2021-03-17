package com.example.ee_drive_client.model;


import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//import com.example.ee_drive_client.MainActivity;

public class DriveData extends Activity {
    String id;
    public String timeAndDate;
    boolean isSentToServer = false;
    boolean driverAssist = false;
    CarType carType;
    public ArrayList<Point> points = new ArrayList<Point>();

    public DriveData(String id, boolean driverAssist, CarType carType) {
        this.id = id;

        this.driverAssist = driverAssist;
        this.carType = carType;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        this.timeAndDate = formatter.format(date);
    }

    public String getTimeAndDate() {
        return timeAndDate;
    }

    public void append(Point point) {
        this.points.add(point);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("timeAndDate", this.timeAndDate);
            json.put("driverAssist", this.driverAssist);
            json.put("carType", this.carType);
            json.put("points", this.points);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


}



