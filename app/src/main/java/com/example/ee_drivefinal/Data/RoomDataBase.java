package com.example.ee_drivefinal.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ee_drivefinal.Interface.ICarDao;
import com.example.ee_drivefinal.Model.CarType;

@Database(entities = {CarType.class}, version = 1, exportSchema = false)

public abstract class RoomDataBase extends RoomDatabase {

    //Variables
    private static RoomDataBase roomDatabase;

    public abstract ICarDao getCarDao();


    public static RoomDataBase getInstance(Context context) {
        if (roomDatabase == null) {
            roomDatabase = Room.databaseBuilder(context, RoomDataBase.class, "MyDataBase").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }

        return roomDatabase;
    }

}
