package com.example.ee_drive_client.repositories;

import android.content.Context;
import android.util.Log;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.controller.SendToServer;
import com.example.ee_drive_client.data.RoomDataBaseCar;
import com.example.ee_drive_client.model.CarType;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class RepositoryCar {


    private ICarDao iCarDao;
    SendToServer sendToServer = new SendToServer();
    Thread thread;
    public RepositoryCar(Context context) throws IOException {
        RoomDataBaseCar roomDatabase = RoomDataBaseCar.getInstance(context);
        iCarDao = roomDatabase.getCarDao();
    }

    public void insertCar(CarType car) throws JSONException, UnirestException {
//      String id= sendToServer.addCarTypeToServerReceiveId(car);
//      car.set_id(id);
//        Log.d("id",id);
        String response=sendToServer.addCarTypeToServerReceiveId(car.toJsonAddCarTypeToServer());
        car.set_id(response);
      iCarDao.insertCar(car);

    }
    public void insertCarDbOnly(CarType car){
        iCarDao.insertCar(car);
    }
    public List<CarType> getCars(){
        return iCarDao.getAllCars();
    }

}
