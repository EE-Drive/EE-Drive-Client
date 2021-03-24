package com.example.ee_drive_client.controller;

import android.os.Environment;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.model.Point;

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

public class JsonHandler {
    public JSONObject driveDataJson;

    public JsonHandler(JSONObject json) {
        this.driveDataJson = json;
    }


    public void appendPoints(JSONObject json) throws JSONException {
        ArrayList <Point> points= new ArrayList<Point> ((Collection<? extends Point>) this.driveDataJson.get("points"));
        ArrayList <Point> points2= new ArrayList<Point> ((Collection<? extends Point>) json.get("points"));
        points.addAll(points2);
        this.driveDataJson.put("points", points);

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
        File file = new File(folder4, fileName+ ".json");
        try {
            FileOutputStream fos;
            //fos =  cnc.openFileOutput(FILENAME,  Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(file);
            pw.println(json.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }

        File newFile = new File(Environment.getExternalStorageDirectory() + "/" + "EE-Drive" + "/"+ "Drives", fileName+ ".json");

        return newFile.exists();


    }


    public JSONObject readFromFile(String fileName) throws JSONException {
        File f1 = new File(Environment.getExternalStorageDirectory() + "/" + "EE-Drive" + "/" + "Drives", fileName + ".json" );
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(f1));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);

            }
        } catch (IOException e) {
            // do exception handling
        } finally {
            try { br.close(); } catch (Exception e) { }
        }

        return new JSONObject(text.toString());

    }
}
