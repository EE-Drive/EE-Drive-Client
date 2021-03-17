package com.example.ee_drive_client.repositories;

import android.content.Context;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.data.RoomDataBaseCar;
import com.example.ee_drive_client.model.Car;

import java.util.List;

public class RepositoryCar {


    private ICarDao iCarDao;

    public RepositoryCar(Context context) {
        RoomDataBaseCar roomDatabase = RoomDataBaseCar.getInstance(context);
        iCarDao = roomDatabase.getCarDao();
    }

    public void insertCar(Car car) {
        iCarDao.insertCar(car);
    }
    public List<Car> getCars(){
        return iCarDao.getAllCars();
    }

}
