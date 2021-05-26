package com.example.ee_drive_client.controller;

import android.os.Environment;
import android.util.Log;

import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.DriveHistory;
import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

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
        ArrayList<CarType> carArr = new ArrayList<CarType>();
        Unirest.setTimeouts(0, 0);
        int attempt = 0;
        int requestId = 0;
        HttpResponse<String> response = null;
        while (attempt < 5 && requestId != 200) {

            response = Unirest.get("http://eedrive.cs.colman.ac.il/api/car-type").asString();
            requestId = response.getCode();
            attempt++;


        }
        if (requestId == 200) {
            JSONArray sa = new JSONArray(response.getBody());
            for (int i = 0; i < sa.length(); i++) {
                JSONObject drive = new JSONObject(sa.get(i).toString());
                String id = drive.getString("_id");
                String companyName = drive.getString("companyName");
                String brandName = drive.getString("brandName");
                String year = drive.getString("year");
                if (drive.has("engineDisplacement")) {
                    String engineDisplacement = drive.getString("engineDisplacement");
                    carArr.add(new CarType(id, companyName, brandName, year, engineDisplacement));
                }
            }
            return carArr;

        }

        //the function that call this method should put the result in JSON Array!!!!!!!
        return null;
    }

    public JSONObject sendStartOfDriveToServerAndGetDriveId(JSONObject drive) throws UnirestException, JSONException {
        Unirest.setTimeouts(0, 0);
        String driveData = drive.toString();
        HttpResponse<String> response = Unirest
                .post("http://eedrive.cs.colman.ac.il/api/drive")
                .header("Content-Type", "application/json")

                .body(driveData)
                .asString();

        return new JSONObject(response.getBody());
    }

    public JSONObject getAcarFromServer(String carId) throws UnirestException, JSONException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response;
        response = Unirest.get("http://eedrive.cs.colman.ac.il/api/car-type/" + carId).asString();
        int requestId = response.getCode();


        return new JSONObject(response.getBody().toString());
    }


    public String sendDataTOExsistinDrive(JSONObject drive, String DriveId) throws UnirestException, JSONException {
        //     JSONObject driveTest=new JSONObject("{\"driveRawData\":[{\"fuelCons\":[],\"speeds\":[],\"lat\":\"32.0230815\",\"long\":\"34.7809234\"},{\"fuelCons\":[0.6380152825224644,0.5767793295416552,0.5767793295416552,0.5733420272201913,0.5733420272201913,0.5733420272201913,0.5733420272201913,0.580904092327412,0.580904092327412,0.580904092327412],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231131\",\"long\":\"34.7809351\"},{\"fuelCons\":[0.580904092327412,0.5699047248987275,0.5509079007354365,0.5509079007354365,0.5509079007354365,0.56486334816058,0.56486334816058,0.56486334816058,0.56486334816058,0.5542306263128517,0.5733420272201913,0.5733420272201913,0.5733420272201913,0.5644050411843852,0.5644050411843852,0.5644050411843852],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231216\",\"long\":\"34.780945\"},{\"fuelCons\":[0.5644050411843852,0.5850288551131688,0.5850288551131688,0.583099022175306,0.583099022175306,0.5817286366942829,0.5623376821378067,0.5623376821378067,0.5623376821378067,0.5504153284529063,0.5693951673650753,0.571279645827313,0.571279645827313,0.57059218536302,0.57059218536302,0.568709974624564,0.568709974624564,0.5824138294347943,0.5824138294347943,0.5824138294347943,0.5824138294347943],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.023134\",\"long\":\"34.7809531\"},{\"fuelCons\":[0.6111919245362782,1.3649952981310214,1.3649952981310214,1.3649952981310214,3.3436263749061457,2.5451484346300512,2.5451484346300512,2.5451484346300512,2.4240063581076146,1.3308270201375136,1.3308270201375136,1.3308270201375136,0.9157829374516673,0.8830764039712504,0.8830764039712504],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231468\",\"long\":\"34.7809539\"},{\"fuelCons\":[0.8830764039712504,0.6980743640331395,0.6722197579578378,0.6722197579578378,0.6722197579578378,0.5962090432770929,0.5962090432770929,0.5962090432770929,0.5962090432770929,0.564735856729597],\"speeds\":[0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231534\",\"long\":\"34.7809535\"}]}}");
        String driveData = drive.getString("driveRawData");
        Log.d("driveToServer",driveData.toString());
        if(DriveId==null || DriveId==""){
            Log.d("driveToServerId","is null");

        }
        Log.d("driveToServerId",DriveId.toString());

        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest
                .post("http://eedrive.cs.colman.ac.il/api/drive/" + DriveId)
                .header("Content-Type", "application/json")
                .body("{\"driveRawData\":" + driveData + "}")
                .asString();
        Log.d("Response code",Integer.toString(response.getCode()));
        //if we get code 400 there is a problem
        return response.getBody();
    }

    public ArrayList<DriveHistory> getDrivesHistory() throws UnirestException, JSONException {
        ArrayList<DriveHistory> driveHistories = new ArrayList<>();
        String driveId;
        String driveTime;
        String driveAssist;
        String id = SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).getId();
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        response = Unirest.get("http://eedrive.cs.colman.ac.il/api/drive/from-car-type/" + id).asString();
        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONArray jsonArray = new JSONArray();
        jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            driveId = (String) jsonArray.getJSONObject(i).get("_id");
            driveTime = (String) jsonArray.getJSONObject(i).get("createdAt");
            driveAssist = Boolean.toString((Boolean) jsonArray.getJSONObject(i).get("driverAssist"));
            driveHistories.add(new DriveHistory(driveId, driveTime, driveAssist));

        }
        Log.d("history",driveHistories.toString());
        return driveHistories;


    }


    public void sendEndOfDriveFromFile(String fileName, String id) throws JSONException, UnirestException {

        JSONObject drive=JsonHandler.readFromFile(fileName);
        sendDataTOExsistinDrive(drive, id);
    }

    public String addCarTypeToServerReceiveId(JSONObject ct) throws UnirestException, JSONException {
        HttpResponse<String> response = null;
        int attempt = 0;
        int requestId = 0;
        Unirest.setTimeouts(0, 0);
        String str = ct.toString();
        while (attempt < 5 && requestId != 200) {
            response = Unirest
                    .post("http://eedrive.cs.colman.ac.il/api/car-type/")
                    .header("Content-Type", "application/json")
                    .body(str)
                    .asString();
            requestId = response.getCode();
            attempt++;
        }
        JSONObject jsonResponse = new JSONObject(response.getBody().toString());

        if (response.getCode() == 200 || response.getCode() == 201) {

            str = jsonResponse.getString("createdItemId");
            //TODO: saving engingD to sharedpref
            //getcartype
            //     int engineD=Integer.parseInt(jsonResponse.getString("engineDisplacement"));
            JSONObject carInfo = getAcarFromServer(str);
            String CarEngine = carInfo.getString("engineDisplacement");
            SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).storeEngine(CarEngine);
            SharedPrefHelper.getInstance(GlobalContextApplication.getContext()).storeId(str);
            return str;
        }


        //if we get code 400 there is a problem
        return "fail to send car-type to server";
    }

    public ArrayList<OptimalModel> getAllOptimalModelsFromServer() throws IOException, UnirestException, JSONException {
        ArrayList<com.example.ee_drive_client.model.OptimalModel> modelArr = new ArrayList<>();
        Unirest.setTimeouts(0, 0);
        int attempt = 0;
        int requestId = 0;
        HttpResponse<String> response = null;
        while (attempt < 5 && requestId != 200) {

            response = Unirest.get("http://eedrive.cs.colman.ac.il/api/optimal-model").asString();
            requestId = response.getCode();
            attempt++;


        }
        if (requestId == 200) {
            JSONArray sa = new JSONArray(response.getBody());
            for (int i = 0; i < sa.length(); i++) {
                JSONObject model = new JSONObject(sa.get(i).toString());

                modelArr.add(new com.example.ee_drive_client.model.OptimalModel(model));

            }
//            for (OptimalModel optimalModel:modelArr
//                 ) {
//                Log.d("model:", optimalModel.getModel().toString());
//            }

            return modelArr;
        }
        return modelArr;
    }


    public ArrayList<Route> getAllRoutesFromServer() throws IOException, UnirestException, JSONException {
        ArrayList<com.example.ee_drive_client.model.Route> routeArr = new ArrayList<>();
        Unirest.setTimeouts(0, 0);
        int attempt = 0;
        int requestId = 0;
        HttpResponse<String> response = null;
        while (attempt < 5 && requestId != 200) {

            response = Unirest.get("http://eedrive.cs.colman.ac.il/api/model-route").asString();
            requestId = response.getCode();
            attempt++;


        }
        if (requestId == 200) {
            JSONArray sa = new JSONArray(response.getBody());
            for (int i = 0; i < sa.length(); i++) {
                JSONObject route = new JSONObject(sa.get(i).toString());

       //         routeArr.add(new com.example.ee_drive_client.model.Route(route));

            }



//            for (Route route:routeArr
//                 ) {
//                Log.d("route",route.getRoute().toString());
//            }

            return routeArr;
        }

        return routeArr;
    }




}