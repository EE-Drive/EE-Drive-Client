package com.example.ee_drive_client.model;

import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class DriveHistory {

    String driveId;
    String driveTime;
    String driveAssist;
    private static DriveHistory instance;
    ArrayList<DriveHistory> driveHistories;

    public static DriveHistory getInstance() throws IOException, UnirestException, JSONException {
        if (instance == null)
            instance = new DriveHistory("", "", "");
        return instance;
    }


    public DriveHistory(String driveId, String driveTime, String driveAssist)  {
        this.driveId = driveId;
        this.driveTime = driveTime;
        this.driveAssist = driveAssist;
    }

    public void updateDriveHistory() throws IOException, JSONException, UnirestException {
        setDriveHistories(RepositoryCar.getInstance().getCarHistory());
    }


    public ArrayList<DriveHistory> getDriveHistories() throws JSONException, UnirestException, IOException {
        return driveHistories;
    }

    public void setDriveHistories(ArrayList<DriveHistory> driveHistories) {
        this.driveHistories = driveHistories;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getDriveTime() {
        return driveTime;
    }

    public void setDriveTime(String driveTime) {
        this.driveTime = driveTime;
    }

    public String getDriveAssist() {
        return driveAssist;
    }

    public void setDriveAssist(String driveAssist) {
        this.driveAssist = driveAssist;
    }
}
