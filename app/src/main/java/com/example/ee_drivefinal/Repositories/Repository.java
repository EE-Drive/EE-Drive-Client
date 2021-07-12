package com.example.ee_drivefinal.Repositories;

import android.widget.Toast;

import com.example.ee_drivefinal.Data.RoomDataBase;
import com.example.ee_drivefinal.Interface.ICarDao;
import com.example.ee_drivefinal.Model.CarType;
import com.example.ee_drivefinal.Model.DriveHistory;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Repository {

    //Variables
    private static Repository instance;
    private ICarDao iCarDao;
    private ServerHandler serverHandler;


    public Repository() throws IOException {
        initializeVariables();
    }

    public static Repository getInstance() throws IOException {
        if (instance == null)
            instance = new Repository();
        return instance;
    }

    private void initializeVariables() throws IOException {
        serverHandler=new ServerHandler();
        RoomDataBase roomDatabase = RoomDataBase.getInstance(GlobalContextApplication.getContext());
        iCarDao = roomDatabase.getCarDao();
    }

    public List<CarType> getCarsFromDB() {return iCarDao.getAllCars(); }


    public ArrayList<CarType> getAllCarsFromServer() throws IOException, JSONException, UnirestException {
       return serverHandler.getAllCarTypesFromServer();
    }

    public void insertCarDbOnly(CarType car) {
        iCarDao.insertCar(car);
    }


    public String addCarToServerRecieveId(JSONObject jsonObject) throws JSONException, UnirestException {
        return serverHandler.addCarTypeToServerRecieveId(jsonObject);
    }

    public void insercar(CarType carType) throws JSONException, UnirestException {
        String response = serverHandler.addCarTypeToServerRecieveId(carType.toJsonAddCarTypeToServer());
        carType.set_id(response);
        iCarDao.insertCar(carType);
    }

    public ArrayList<DriveHistory> getCarHistory() throws JSONException, UnirestException, IOException {
        return serverHandler.getDrivesHistory();
    }
}
