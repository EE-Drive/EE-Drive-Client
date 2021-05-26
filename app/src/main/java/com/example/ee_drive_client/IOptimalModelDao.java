package com.example.ee_drive_client;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;

import java.util.List;

@Dao

public interface  IOptimalModelDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    void insertModel(OptimalModel optimalModel);
//
//    @Query("SELECT * FROM OptimalModel")
//    List<Route> getAllOptimalModels();

}
