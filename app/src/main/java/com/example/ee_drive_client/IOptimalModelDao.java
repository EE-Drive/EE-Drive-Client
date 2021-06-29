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

    @Query("SELECT vertices FROM OptimalModel where `carTypeId`=:carTypeId and `routeId`=:routeId")
    String getOptimalModelVerticesById(String routeId, String carTypeId) ;

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertModel(OptimalModel optimalModel);

    @Query("SELECT * FROM OptimalModel")
    List<Route> getAllOptimalModels();

    @Query("SELECT edges FROM OptimalModel where `carTypeId`=:carTypeId and `routeId`=:routeId")
    String getOptimalModelEdgesById(String routeId, String carTypeId);

    @Query("SELECT _id FROM OptimalModel where `carTypeId`=:carTypeId and `routeId`=:routeId")
    String getOptimalModelId(String routeId, String carTypeId);
}
