package com.example.ee_drivefinal.ViewModel;

import android.util.Log;

import com.example.ee_drivefinal.Model.DriveHistory;
import com.example.ee_drivefinal.Repositories.Repository;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class DriveHistoryViewModel {

    //Variables
    private Repository repository;
    private Thread serverThread;
    private ArrayList<DriveHistory> driveHistoriesTemp;


    public DriveHistoryViewModel() throws IOException {
        repository = new Repository();
        driveHistoriesTemp = new ArrayList<>();
    }


    public void finish() {
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    public ArrayList<DriveHistory> getCarHistory() throws JSONException, UnirestException, IOException {
        return repository.getCarHistory();
    }
}
