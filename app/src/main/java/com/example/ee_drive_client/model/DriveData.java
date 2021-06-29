package com.example.ee_drive_client.model;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drive_client.controller.JsonHandler;
import com.example.ee_drive_client.controller.SendToServer;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.example.ee_drive_client.view.MainScreenFragment;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;

//import com.example.ee_drive_client.MainActivity;

public class DriveData extends Activity {

    //Variables
    private String id, timeAndDate;
    public Boolean driveInProcess = false;
    private boolean isSentToServer = false, driverAssist = false;
    private CarType carType;
    private double mFuel;
    private static DriveData instance;
    private Thread thread;
    private SimpleDateFormat formatter;
    private Date date;
    private JSONObject jsonObjectToServer;
    private JsonHandler jsonHandler;
    private SharedPrefHelper sharedPrefHelper = SharedPrefHelper.getInstance(GlobalContextApplication.getContext());
    private SendToServer sendToServer;
    private JSONArray fuel;
    private JSONArray speed;
    public ArrayList<Point> points = new ArrayList<Point>();

    public Boolean getDriveInProcess() {
        return driveInProcess;
    }

    public void setDriveInProcess(Boolean driveInProcess) {
        this.driveInProcess = driveInProcess;
    }

    public DriveData(String id, boolean driverAssist, CarType carType) {
        this.id = id;
        this.driverAssist = driverAssist;
        this.carType = carType;
        formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        date = new Date(System.currentTimeMillis());
        this.timeAndDate = formatter.format(date);
    }

    public void resetData() {
        setId("");
        for (int i = 0; i < this.points.size(); i++) {
            this.points.remove(i);
        }
        Log.d("Array removed", this.points.toString());

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
        mFuel = obdData.getFuel();
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

    public MutableLiveData<Double> getFuel() {
        return this.getLiveFuel;
    }

    public MutableLiveData<Boolean> getRecordingData() {
        return getRecording;
    }

    public static DriveData getInstance() {
        if (instance == null)
            instance = new DriveData("", false, new CarType(SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).getBrand(), SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).getModel(), SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).getYear(), Integer.toString(SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).getEngine())));
        return instance;
    }
    MutableLiveData<Boolean> getDriveAssist = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> getDriveAssist() {
        return getDriveAssist;
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


    public void writeData(DriveData driveData) throws IOException {
        Log.d("Data", "Writing data");
        jsonObjectToServer = driveData.toJsonSaveFile();
        jsonHandler = new JsonHandler(jsonObjectToServer);
        jsonHandler.saveToFile(driveData.getTimeAndDate(), jsonObjectToServer);
    }

    public void EndDrive() throws IOException {
        sendToServer = new SendToServer();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("SendId", getId());
                    sendToServer.sendEndOfDriveFromFile(getTimeAndDate(), getId());
                    resetData();
                } catch (UnirestException | JSONException exception) {
                    exception.printStackTrace();
                }
            }
        });
        thread.start();
        Toast.makeText(GlobalContextApplication.getContext(), "Drive has been ended!", Toast.LENGTH_SHORT).show();

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

    public JSONArray pointArrayToJson(ArrayList<Point> points2) throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try{
            for (Point point :
                    points2) {
                json = new JSONObject();
                fuel = new JSONArray(point.getFuelCons());
                speed = new JSONArray(point.getSpeeds());
                json.put("fuelCons", fuel);
                json.put("speeds", speed);
                json.put("lat", Double.toString(point.getLat()));
                json.put("long", Double.toString(point.getLang()));
                jsonArray.put(json);
            }
        }catch (ConcurrentModificationException exception){
            Log.d("exception" , exception.toString());
        }

        return jsonArray;
    }

    public void updateCurrentCar() {
        this.setCarType(new CarType(sharedPrefHelper.getBrand(), sharedPrefHelper.getModel(), sharedPrefHelper.getYear(), Integer.toString(sharedPrefHelper.getEngine())));
    }
}



