package com.example.ee_drivefinal.Repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drivefinal.Model.CarType;
import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.Model.DriveHistory;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ServerHandler {

    //Variables
    private URL url;
    private String id;
    private int attempt;
    boolean sent;
    private Thread serverThread, AssistThread;
    private MutableLiveData<String> sendToServerStatus;
    private JSONObject jsonObject = new JSONObject();


    public ServerHandler() throws IOException {
        this.url = new URL("https://eedrive.cs.colman.ac.il");
        sendToServerStatus = new MutableLiveData<>();
    }


    public MutableLiveData<String> getSendToServerStatus() {
        return sendToServerStatus;
    }

    public void setSendToServerStatus(MutableLiveData<String> sendToServerStatus) {
        this.sendToServerStatus = sendToServerStatus;
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
            Log.d("requestId", Integer.toString(requestId));
            Log.d("response", (response.getBody()));

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


    public String addCarTypeToServerRecieveId(JSONObject ct) throws UnirestException, JSONException {
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
            JSONObject carInfo = getAcarFromServer(str);
            String CarEngine = carInfo.getString("engineDisplacement");
            SharedPrefHelper.getInstance().storeModel(carInfo.getString("brandName"));
            Log.d("Model", carInfo.getString("brandName"));
            Log.d("Brand", carInfo.getString("companyName"));
            SharedPrefHelper.getInstance().storeBrand(carInfo.getString("companyName"));
            SharedPrefHelper.getInstance().storeEngine(CarEngine);
            SharedPrefHelper.getInstance().storeId(str);
            return str;
        }
        //if we get code 400 there is a problem
        Log.d("Error", "Fail to send to Server");
        return "fail to send car-type to server";
    }

    public JSONObject getAcarFromServer(String carId) throws UnirestException, JSONException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response;
        response = Unirest.get("http://eedrive.cs.colman.ac.il/api/car-type/" + carId).asString();
        int requestId = response.getCode();
        return new JSONObject(response.getBody().toString());
    }

    public ArrayList<DriveHistory> getDrivesHistory() throws JSONException, UnirestException, IOException {
        ArrayList<DriveHistory> driveHistories = new ArrayList<>();
        String driveId;
        String driveTime;
        String driveAssist;
        String id = SharedPrefHelper.getInstance().getId();
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
        Log.d("histories", driveHistories.toString());
        return driveHistories;
    }

    public JSONObject sendStartOfDriveToServerAndGetDriveId(JSONObject drive) throws UnirestException, JSONException {
        Unirest.setTimeouts(0, 0);
        String driveData = drive.toString();
        HttpResponse<String> response = Unirest
                .post("http://eedrive.cs.colman.ac.il/api/drive")
                .header("Content-Type", "application/json")
                .body(driveData)
                .asString();
        FileHandler.appendLog(response.getBody());
        FileHandler.appendLog(Integer.toString(response.getCode()));
        return new JSONObject(response.getBody());
    }


    public void sendEndOfDrive(DriveData driveData) {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("SendId", driveData.getId());
                try {
                    sendEndOfDriveFromFile(driveData.getTimeAndDate(), driveData.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                    FileHandler.appendLog(e.toString());
                } catch (UnirestException exception) {
                    sendToServerStatus.postValue("Failed");
                }
            }
        });
        serverThread.start();
    }

    private void sendEndOfDriveFromFile(String fileName, String id) throws JSONException, UnirestException {
        JSONObject drive = JsonHandler.readFromFile(fileName);
        sendDataTOExsistinDrive(drive, id);

    }

    public String sendDataTOExsistinDrive(JSONObject drive, String DriveId) throws UnirestException, JSONException {
        //     JSONObject driveTest=new JSONObject("{\"driveRawData\":[{\"fuelCons\":[],\"speeds\":[],\"lat\":\"32.0230815\",\"long\":\"34.7809234\"},{\"fuelCons\":[0.6380152825224644,0.5767793295416552,0.5767793295416552,0.5733420272201913,0.5733420272201913,0.5733420272201913,0.5733420272201913,0.580904092327412,0.580904092327412,0.580904092327412],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231131\",\"long\":\"34.7809351\"},{\"fuelCons\":[0.580904092327412,0.5699047248987275,0.5509079007354365,0.5509079007354365,0.5509079007354365,0.56486334816058,0.56486334816058,0.56486334816058,0.56486334816058,0.5542306263128517,0.5733420272201913,0.5733420272201913,0.5733420272201913,0.5644050411843852,0.5644050411843852,0.5644050411843852],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231216\",\"long\":\"34.780945\"},{\"fuelCons\":[0.5644050411843852,0.5850288551131688,0.5850288551131688,0.583099022175306,0.583099022175306,0.5817286366942829,0.5623376821378067,0.5623376821378067,0.5623376821378067,0.5504153284529063,0.5693951673650753,0.571279645827313,0.571279645827313,0.57059218536302,0.57059218536302,0.568709974624564,0.568709974624564,0.5824138294347943,0.5824138294347943,0.5824138294347943,0.5824138294347943],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.023134\",\"long\":\"34.7809531\"},{\"fuelCons\":[0.6111919245362782,1.3649952981310214,1.3649952981310214,1.3649952981310214,3.3436263749061457,2.5451484346300512,2.5451484346300512,2.5451484346300512,2.4240063581076146,1.3308270201375136,1.3308270201375136,1.3308270201375136,0.9157829374516673,0.8830764039712504,0.8830764039712504],\"speeds\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231468\",\"long\":\"34.7809539\"},{\"fuelCons\":[0.8830764039712504,0.6980743640331395,0.6722197579578378,0.6722197579578378,0.6722197579578378,0.5962090432770929,0.5962090432770929,0.5962090432770929,0.5962090432770929,0.564735856729597],\"speeds\":[0,0,0,0,0,0,0,0,0,0],\"lat\":\"32.0231534\",\"long\":\"34.7809535\"}]}}");
        sendToServerStatus.postValue("Working");
        String driveData = drive.getString("driveRawData");
      //  String driverAssist = drive.getString("driverAssist");
        if (DriveId == null || DriveId == "") {
            Log.d("driveToServerId", "is null");
        }
        Log.d("driveToServerId", DriveId.toString());
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest
                .post("http://eedrive.cs.colman.ac.il/api/drive/" + DriveId)
                .header("Content-Type", "application/json")
                .body("{" +
                        //"\"driverAssist\":"+ drive.getString("driverAssist")+","+
                        "\"driveRawData\":" + driveData

                        +"}")
                .asString();
        int code = response.getCode();
        Log.d("Response code", Integer.toString(code));
        if (code == 200 || code == 201) {
            Log.d("RESPONSE Code", Integer.toString(code));
            DriveData.getInstance().setSentToServer(true);
            sendToServerStatus.postValue("Connected");
        } else {
            Log.d("RESPONSE Code", Integer.toString(code));

            sendToServerStatus.postValue("Failed");
        }
        //if we get code 400 there is a problem
        return response.getBody();
    }

    public JSONObject getOptimalModel(String carTypeId , double lat,double _long) throws UnirestException, JSONException {
        Unirest.setTimeouts(0, 0);
        String body="{"
                +   "\"lat\": "+ lat +","
                +   "\"long\": "+ _long +","
                + "\"carTypeID\": " + "\"" + carTypeId + "\""
                +   "}";
        HttpResponse<String> response = Unirest
                .post("http://eedrive.cs.colman.ac.il/api/model-route/bring-Model")
                .header("Content-Type", "application/json")
                .body(body)
                .asString();
        int code= response.getCode();


        return  new JSONObject(response.getBody());
    }


}
