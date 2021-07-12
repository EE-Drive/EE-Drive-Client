package com.example.ee_drivefinal.ViewModel;

import android.widget.Toast;

import com.example.ee_drivefinal.Model.CarType;
import com.example.ee_drivefinal.Repositories.Repository;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;

public class AddCarViewModel {

    //Variables
    private Repository repository;
    private Thread serverThread;

    public AddCarViewModel() throws IOException {
        repository = new Repository();
    }


    public void addCar(String brandValue, String modelValue, String yearValue, String engineValue) throws JSONException, UnirestException {
        CarType carType = new CarType(brandValue,modelValue,yearValue,engineValue);
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    repository.insercar(carType);
                } catch (JSONException|UnirestException e) {
                    FileHandler.appendLog(e.toString());
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        Toast.makeText(GlobalContextApplication.getContext(), "Added car", Toast.LENGTH_SHORT).show();
    }

    public ERROR_INPUT checkValidation(String brandValue, String modelValue, String yearValue, String engineValue) {
        if (modelValue.isEmpty() || brandValue.isEmpty() || yearValue.isEmpty() || engineValue.isEmpty()) {
            return ERROR_INPUT.MISS_FIELDS;
        } else if (modelValue.length() < 2 || brandValue.length() < 2 || yearValue.length() < 2 || engineValue.length() < 2) {
            return ERROR_INPUT.SHORT;
        }
        return ERROR_INPUT.VALID;

    }

    public enum ERROR_INPUT {
        MISS_FIELDS,
        SHORT,
        VALID
    }
    public void finish(){
        if (serverThread!=null){
            serverThread.interrupt();
        }
    }

}
