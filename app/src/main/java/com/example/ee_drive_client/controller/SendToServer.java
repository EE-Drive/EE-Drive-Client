package com.example.ee_drive_client.controller;

import android.os.Environment;

import com.example.ee_drive_client.model.CarType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;

import org.json.JSONArray;
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

        this.url = new URL(this.url.getPath().toString() + "/api/car-type");
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

    public ArrayList<CarType> getAllCarTypesFromServer() throws IOException, UnirestException, JSONException {
        ArrayList<CarType> carArr= new ArrayList<CarType>();
        Unirest.setTimeouts(0, 0);
        int attempt =0;
        int requestId=0;
        HttpResponse<String> response= null;
        while(attempt<5 && requestId!=200) {

            response = Unirest.get("http://eedrive.cs.colman.ac.il/api/car-type").asString();
            requestId=response.getCode();
            attempt++;


        }
        if(requestId==200){
            JSONArray sa = new JSONArray(response.getBody());
            for(int i=0 ;i<sa.length();i++) {
                JSONObject drive = new JSONObject(sa.get(i).toString());
                String id=drive.getString("_id");
                String companyName=drive.getString("companyName");
                String brandName=drive.getString("brandName");
                String year=drive.getString("year");
                carArr.add(new CarType(id,companyName,brandName,year));


            }
            return carArr;

        }

        //the function that call this method should put the result in JSON Array!!!!!!!
        return null;
    }

    public String sendStartOfDriveToServerAndGetDriveId(JSONObject drive) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        String driveData=drive.toString();
        HttpResponse<String> response = Unirest
                .post("http://eedrive.cs.colman.ac.il/api/drive")
                .header("Content-Type", "application/json")

                .body(driveData)
                .asString();
        return response.getBody();
    }


    public String sendDataTOExsistinDrive(JSONObject drive,String DriveId) throws UnirestException {
        String driveData=drive.toString();
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response= Unirest
                .patch("http://eedrive.cs.colman.ac.il/api/drive/"+DriveId)
                .header("Content-Type", "application/json")
                .body(driveData)
                .asString();
        //if we get code 400 there is a problem
        return response.getBody();
    }

    public String addCarTypeToServerReceiveId(CarType ct) throws UnirestException, JSONException {
        HttpResponse<String> response= null;
        int attempt =0;
        int requestId=0;
        Unirest.setTimeouts(0, 0);
        String str=ct.toString();
        while(attempt<5 && requestId!=200) {
            response = Unirest
                    .post("http://eedrive.cs.colman.ac.il/api/car-type")
                    .header("Content-Type", "application/json")
                    .body(str)
                    .asString();

            attempt++;
        }
        JSONObject jsonResponse =new JSONObject(response.getBody().toString());

        if(response.getCode()==200||response.getCode()==201)
        {
            str=jsonResponse.getString("createdItemId");
            return str;
        }


        //if we get code 400 there is a problem
        return "fail to send car-type to server";
    }


}
