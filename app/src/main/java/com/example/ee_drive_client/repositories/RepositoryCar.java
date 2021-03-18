package com.example.ee_drive_client.repositories;

import android.content.Context;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.data.RoomDataBaseCar;
import com.example.ee_drive_client.model.CarType;

import java.util.List;

public class RepositoryCar {


    private ICarDao iCarDao;

    public RepositoryCar(Context context) {
        RoomDataBaseCar roomDatabase = RoomDataBaseCar.getInstance(context);
        iCarDao = roomDatabase.getCarDao();
    }

    public void insertCar(CarType car) {
        iCarDao.insertCar(car);
    }
    public List<CarType> getCars(){
        return iCarDao.getAllCars();
    }

}
