package com.example.ee_drivefinal.Repositories;

import android.os.Environment;
import android.util.Log;

import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Model.Point;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;

public class JsonHandler {

    //Variables
    public JSONObject driveDataJson;

    public JsonHandler() {
        this.driveDataJson = new JSONObject();
    }

    public void appendPoints(JSONObject json) throws JSONException {
        ArrayList<Point> points = new ArrayList<Point>((Collection<? extends Point>) this.driveDataJson.get("points"));
        ArrayList<Point> points2 = new ArrayList<Point>((Collection<? extends Point>) json.get("points"));
        points.addAll(points2);
        this.driveDataJson.put("points", points);
    }
    public JSONObject toJsonSaveFile(DriveData driveData) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", DriveData.getInstance().getId());
            json.put("isSentToServer", DriveData.getInstance().getSentToServer());
            json.put("timeAndDate", DriveData.getInstance().getTimeAndDate());
            json.put("driverAssist", DriveData.getInstance().getDriverAssist());
            json.put("carType", DriveData.getInstance().getInstance().getCarType());
            json.put("carTypeId", DriveData.getInstance().getCarType().get_id());
            json.put("driveRawData", pointArrayToJson(DriveData.getInstance().getPoints()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONArray pointArrayToJson(ArrayList<Point> points2) throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try{
            for (Point point :
                    points2) {
                json = new JSONObject();
                JSONArray fuel = new JSONArray(point.getFuelCons());
                JSONArray speed = new JSONArray(point.getSpeeds());
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

    public boolean saveToFile(String fileName, JSONObject json) {
        //Requesting Permission to access External Storage
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
        //     101);
        File f = new File(Environment.getExternalStorageDirectory(), "EE-Drive");
        System.out.println(f);
        if (!f.exists()) {
            System.out.println("don't exist i will create");
            f.mkdirs();
            System.out.println(" i  create");
        }
        if (!f.exists()) {
            System.out.println("Still no exist");
        }
        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "EE-Drive", "Drives");
        if (!f1.exists()) {
            f1.mkdirs();
        }
        File folder4 = new File(Environment.getExternalStorageDirectory(), "EE-Drive/Drives");
        File file = new File(folder4, fileName + ".json");
        try {
            FileOutputStream fos;
            //fos =  cnc.openFileOutput(FILENAME,  Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(file);
            pw.println(json.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }
        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + "EE-Drive" + "/" + "Drives", fileName + ".json");
        return newFile.exists();
    }

    public static JSONObject readFromFile(String fileName) throws JSONException {
        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "EE-Drive" + "/" + "Drives", fileName + ".json");
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f1));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                FileHandler.appendLog(e.toString());
                e.printStackTrace();
            }
        }
        return new JSONObject(text.toString());
    }

    public JSONObject toJsonServerStartOfDrive() {
        JSONObject json = new JSONObject();
        try {

            json.put("driverAssist", DriveData.getInstance().getDriverAssist());
            json.put("carTypeId", SharedPrefHelper.getInstance().getId());
            json.put("driveRawData", pointArrayToJson(DriveData.getInstance().getPoints()));
        } catch (JSONException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }
        return json;
    }

}
