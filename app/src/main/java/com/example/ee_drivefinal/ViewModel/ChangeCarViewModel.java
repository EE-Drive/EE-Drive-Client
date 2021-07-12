package com.example.ee_drivefinal.ViewModel;

import android.util.Log;

import com.example.ee_drivefinal.Model.CarType;
import com.example.ee_drivefinal.Repositories.Repository;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ChangeCarViewModel {

    //Variables
    private Repository repository;
    private Thread serverThread;
    private ArrayList<CarType> response;
    private String carId;


    public ChangeCarViewModel() throws IOException {
        repository = Repository.getInstance();
        response = new ArrayList<>();
    }


    public ArrayList<String> getAllCars() {
        ArrayList<String> arrayList = new ArrayList<>();
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArray = new JSONArray();
                    response = repository.getAllCarsFromServer();
                    for (CarType car : response
                    ) {
                        repository.insertCarDbOnly(car);
                    }
                } catch (IOException | UnirestException | JSONException exception) {
                    FileHandler.appendLog(exception.toString());
                    exception.printStackTrace();
                }
            }
        });
        serverThread.start();
        ArrayList<CarType> carArrayList = (ArrayList<CarType>) repository.getCarsFromDB();
        for (int i = 0; i < carArrayList.size(); i++) {
            arrayList.add(carArrayList.get(i).loadFullModelForShow());
        }
        return arrayList;
    }

    public String addCarTypeToServerReceiveId(JSONObject jsonObject) throws JSONException, UnirestException {
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    carId = repository.addCarToServerRecieveId(jsonObject);
                } catch (JSONException | UnirestException e) {
                    FileHandler.appendLog(e.toString());
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        return carId;
    }

    public void finish() {
        if (serverThread != null)
            serverThread.interrupt();
    }
}
