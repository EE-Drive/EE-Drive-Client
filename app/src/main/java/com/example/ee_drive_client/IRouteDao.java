package com.example.ee_drive_client;

import android.annotation.SuppressLint;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.Route;

import java.util.List;

@Dao

public interface IRouteDao {


//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    void insertRoute(Route route);
//
//    @Query("SELECT * FROM Route")
//    List<Route> getAllRoutes();



}
