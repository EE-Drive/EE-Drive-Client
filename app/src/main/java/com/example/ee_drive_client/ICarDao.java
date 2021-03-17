package com.example.ee_drive_client;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ee_drive_client.model.Car;

import java.util.List;

@Dao
public interface ICarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCar(Car car);

    @Query("SELECT * FROM Car")
    List<Car> getAllCars();


}
