package com.example.ee_drive_client;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ee_drive_client.model.CarType;

import java.util.List;

@Dao
public interface ICarDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCar(CarType car);

    @Query("SELECT * FROM CarType")
    List<CarType> getAllCars();


}
