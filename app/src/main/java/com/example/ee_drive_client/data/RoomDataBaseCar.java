package com.example.ee_drive_client.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.IOptimalModelDao;
import com.example.ee_drive_client.IRouteDao;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;
import com.example.ee_drive_client.model.RoutePoint;

@Database(entities = {CarType.class, OptimalModel.class, Route.class}, version = 15, exportSchema = false)
@TypeConverters(Converters.class)

public abstract class RoomDataBaseCar extends RoomDatabase {

    //Variables
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
