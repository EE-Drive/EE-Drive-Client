package com.example.ee_drive_client.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.IOptimalModelDao;
import com.example.ee_drive_client.IRouteDao;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;

@Database(entities = {CarType.class}, version = 9, exportSchema = false)
public abstract class RoomDataBaseCar extends RoomDatabase {

    private static RoomDataBaseCar roomDatabase;

    public abstract ICarDao getCarDao();
    public abstract IRouteDao getRouteDao();
    public abstract IOptimalModelDao getOptimalModelDao();


    public static RoomDataBaseCar getInstance(Context context) {
        if (roomDatabase == null) {
            roomDatabase = Room.databaseBuilder(context, RoomDataBaseCar.class, "MyDataBase").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return roomDatabase;
    }

}
