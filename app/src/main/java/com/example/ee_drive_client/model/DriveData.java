package com.example.ee_drive_client.model;


import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.example.ee_drive_client.view.MainScreenFragment;

import org.json.JSONArray;
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
    private static DriveData instance;

    public ArrayList<Point> points = new ArrayList<Point>();

    public DriveData(String id, boolean driverAssist, CarType carType) {
        this.id = id;
        this.driverAssist = driverAssist;
        this.carType = carType;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        this.timeAndDate = formatter.format(date);
    }

    public void resetData(){
        setId("");
        for (int i=0;i<this.points.size();i++){
            this.points.remove(i);
        }
        Log.d("Array removed",this.points.toString());

    }
    public String getTimeAndDate() {
        return timeAndDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimeAndDate(String timeAndDate) {
        this.timeAndDate = timeAndDate;
    }

    public boolean isSentToServer() {
        return isSentToServer;
    }

    public void setSentToServer(boolean sentToServer) {
        isSentToServer = sentToServer;
    }

    public boolean isDriverAssist() {
        return driverAssist;
    }

    public void setDriverAssist(boolean driverAssist) {
        this.driverAssist = driverAssist;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public int getPointsSize() {
        return this.points.size();
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public void addInfoToLastPoint(OBDData obdData) {
        double mFuel = obdData.getFuel();
        this.points.get(this.getPointsSize() - 1).append(obdData.getSpeed());
        if (mFuel != 0)
            this.points.get(this.getPointsSize() - 1).appendFuel(mFuel);
        else {
            //TODO: A CAR DOES NOT SUPPORT getFuel
        }
    }

    public Point getLastPoint() {
        return this.points.get(getPointsSize() - 1);
    }

    MutableLiveData<Integer> getLiveSpeed = new MutableLiveData<Integer>();

    public MutableLiveData<Integer> getSpeed() {
        return this.getLiveSpeed;
    }

    MutableLiveData<Boolean> getRecording = new MutableLiveData<Boolean>();
    MutableLiveData<Double> getLiveFuel = new MutableLiveData<Double>();
    public MutableLiveData<Double> getFuel(){ return this.getLiveFuel;}

    public MutableLiveData<Boolean> getRecordingData() {
        return getRecording;
    }

    public static DriveData getInstance() {
        if (instance == null)
            instance = new DriveData("", false, new CarType("Mazda", "Three", "2004","1300"));
        return instance;
    }

    public void append(Point point) {
        this.points.add(point);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("isSentToServer", this.isSentToServer);
            json.put("timeAndDate", this.timeAndDate);
            json.put("driverAssist", this.driverAssist);
            json.put("carType", this.carType);
            json.put("driveRawData", pointArrayToJson(this.points));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject toJsonServerStartOfDrive() {
        JSONObject json = new JSONObject();
        try {

            json.put("driverAssist", this.driverAssist);
            json.put("carTypeId", SharedPrefHelper.getInstance(getApplication()).getId());
            json.put("driveRawData", pointArrayToJson(this.points));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public JSONObject toJsonServerAppendDrive() {
        JSONObject json = new JSONObject();
        try {
            json.put("driveRawData", this.points);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public JSONObject toJsonSaveFile() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("isSentToServer", this.isSentToServer);
            json.put("timeAndDate", this.timeAndDate);
            json.put("driverAssist", this.driverAssist);
            json.put("carType", this.carType);
            json.put("carTypeId", this.carType.get_id());
            json.put("driveRawData", pointArrayToJson(this.points));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONArray arrayListToJson(ArrayList<Point> points2) throws JSONException {

        JSONArray jsonArray = new JSONArray(points2.toString());


        return jsonArray;


    }

    public static JSONArray pointArrayToJson(ArrayList<Point> points2) throws JSONException {
        JSONObject json=new JSONObject();
        JSONArray jsonArray= new JSONArray();
        for (int i = 0; i < points2.size();i++) {
            json=new JSONObject();
            JSONArray fuel=new JSONArray(points2.get(i).fuelCons);
            JSONArray speed=new JSONArray(points2.get(i).speeds);
            json.put("fuelCons",fuel);
            json.put("speeds",speed);
            json.put("lat", Double.toString(points2.get(i).lat));
            json.put("long",Double.toString(points2.get(i).lang));
            jsonArray.put(json);



        }
        return jsonArray;
    }
}



