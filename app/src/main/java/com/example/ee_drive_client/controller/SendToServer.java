package com.example.ee_drive_client.controller;

import android.os.Environment;

import com.example.ee_drive_client.model.CarType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SendToServer {
    public URL url;


    public SendToServer() throws IOException {
        this.url = new URL("https://eedrive.cs.colman.ac.il");
    }

    /*
        public String send(JSONObject drive) throws IOException {
             //this.url = new URL(url.getPath().toString()+ "/api/drive/:id");
            //this.url = new URL("https://eedrive.cs.colman.ac.il/api/drive");
            this.url = new URL("https://google.com");

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            //conn.setSSLSocketFactory(PinnedPublicKeySocketFactory.createSocketFactory());
            conn.setDoOutput(true);
            conn.setDoInput(true);
           // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1 * 2 * 1000);


            //System.out.println(conn.getResponseCode()) ;

            StringBuilder respond= new StringBuilder();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(new CarType("Test", "test",  666).toString());
            conn.connect();
            out.flush();

            if (conn.getResponseCode() == 200) {
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    respond.append(line);
                }
            }
            else
                return "fu";

            out.flush();
            out.close();
            return respond.toString();

        }
    */
    public String getId(CarType CT) throws IOException {

        this.url = new URL(this.url.getPath().toString() + "/api/drive");
        String id = "";
        int attempt = 0;
        boolean sent = false;
        while (attempt < 5 && !sent) {


            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            //conn.setSSLSocketFactory(PinnedPublicKeySocketFactory.createSocketFactory());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1 * 60 * 1000);


            StringBuilder respond = new StringBuilder();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(CT.toString());
            conn.connect();

            if (conn.getResponseCode() == 200) {
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    respond.append(line);
                }
            }

            out.flush();
            out.close();
            id = respond.toString();
            if (id != new StringBuilder().toString())
                sent = true;

        }
        return id;
    }

    public boolean sendToServer(JSONObject drive) throws IOException {
        this.url = new URL(this.url.getPath().toString() + "/api/drive");
        int attempt = 0;
        boolean sent = false;
        while (attempt < 5 && !sent) {


            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1 * 60 * 1000);

            StringBuilder respond = new StringBuilder();
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(drive.toString());
            conn.connect();

            if (conn.getResponseCode() == 200) {
                InputStream input = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    respond.append(line);
                }
            }

            out.flush();
            out.close();
            if (respond.toString().contains("true"))
                sent = true;
            attempt++;
        }
        return sent;
    }

    public boolean sendEndDriveToServer(JSONObject drive) throws IOException, JSONException {
        if (sendToServer(drive)) {
            JsonHandler drive2 = new JsonHandler(drive);
            String date = drive.getString("timeAndDate");
            JSONObject update = drive2.readFromFile(date);
            update.put("isSentToServer", true);
            drive2.saveToFile(date, update);
            return true;

        }
        return false;
    }

    public ArrayList<String> sendUnsentToServer() throws JSONException, IOException {
        ArrayList<String> didentSent = new ArrayList<>();
        SendToServer severSender = new SendToServer();
        File drives = Environment.getExternalStorageDirectory();
        File yourDir = new File(drives, "EE-Drive/Drives");
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                String name = f.getName();
                JsonHandler drive = new JsonHandler(new JSONObject());
                drive.readFromFile(name);
                if (drive.driveDataJson.get("isSentToServer").toString() == "false") {
                    if (!severSender.sendEndDriveToServer(drive.driveDataJson))
                        didentSent.add(name);
                }
            }
        }

        return didentSent;
    }
}
