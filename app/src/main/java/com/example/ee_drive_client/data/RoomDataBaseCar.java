package com.example.ee_drive_client.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.model.CarType;

@Database(entities = CarType.class, version = 3, exportSchema = false)
public abstract class RoomDataBaseCar extends RoomDatabase {

    private static RoomDataBaseCar roomDatabase;

    public abstract ICarDao getCarDao();

    public static RoomDataBaseCar getInstance(Context context) {
        if (roomDatabase == null) {
            roomDatabase = Room.databaseBuilder(context, RoomDataBaseCar.class, "MyDataBase").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return roomDatabase;
    }

}
