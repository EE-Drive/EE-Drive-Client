package com.example.ee_drive_client.repositories;

import android.content.Context;

import com.example.ee_drive_client.ICarDao;
import com.example.ee_drive_client.IOptimalModelDao;
import com.example.ee_drive_client.IRouteDao;
import com.example.ee_drive_client.controller.SendToServer;
import com.example.ee_drive_client.data.RoomDataBaseCar;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.DriveHistory;
import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryCar {

    //Variables
    public ICarDao iCarDao;
    public IOptimalModelDao iOptimalModelDao;
    public IRouteDao iRouteDao;
    private SendToServer sendToServer;
    private String response;
    private static RepositoryCar instance;


    public RepositoryCar(Context context) throws IOException {
        initializeVariables(context);
    }


    public static RepositoryCar getInstance() throws IOException {
        if (instance == null)
            instance = new RepositoryCar(GlobalContextApplication.getContext());
        return instance;
    }

    public ArrayList<DriveHistory> getCarHistory() throws JSONException, UnirestException {
        return sendToServer.getDrivesHistory();
    }


    public void insertCar(CarType car) throws JSONException, UnirestException {

        response = sendToServer.addCarTypeToServerReceiveId(car.toJsonAddCarTypeToServer());
        car.set_id(response);
        iCarDao.insertCar(car);

    }

    public void insertCarDbOnly(CarType car) {
        iCarDao.insertCar(car);
    }

    public List<CarType> getCars() {
        return iCarDao.getAllCars();
    }

    public RepositoryCar.ERROR_INPUT isValid(String model, String brand, String year, String engine) {
        if (model.isEmpty() || brand.isEmpty() || year.isEmpty() || engine.isEmpty()) {
            return RepositoryCar.ERROR_INPUT.MISS_FIELDS;
        } else if (model.length() < 2 || brand.length() < 2 || year.length() < 2 || engine.length() < 2) {
            return RepositoryCar.ERROR_INPUT.SHORT;
        }
        return RepositoryCar.ERROR_INPUT.VALID;
    }

    public List<Route> getAllRoutes() {
        return iRouteDao.getAllRoutes();
    }

    public String getOptimalModelVerticesById(String routeId, String carTypeId) {
        return iOptimalModelDao.getOptimalModelVerticesById(routeId, carTypeId);
    }

    public String getOptimalModelEdgesById(String routeId, String carTypeId) {
        return iOptimalModelDao.getOptimalModelEdgesById(routeId, carTypeId);
    }

    public void insertRoute(Route route) {
        iRouteDao.insertRoute(route);
    }

    public void insertOptimalModel(OptimalModel model) {
        iOptimalModelDao.insertModel(model);
    }

    public String getOptimalModelId(String routeId, String carTypeId) {
        return iOptimalModelDao.getOptimalModelId(routeId, carTypeId);

    }

    private void initializeVariables(Context context) throws IOException {
        RoomDataBaseCar roomDatabase = RoomDataBaseCar.getInstance(context);
        iCarDao = roomDatabase.getCarDao();
        iOptimalModelDao = roomDatabase.getOptimalModelDao();
        iRouteDao = roomDatabase.getRouteDao();
        sendToServer = new SendToServer();
    }


    public enum ERROR_INPUT {
        MISS_FIELDS,
        SHORT,
        VALID
    }


}
