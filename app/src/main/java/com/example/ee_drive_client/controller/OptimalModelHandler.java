package com.example.ee_drive_client.controller;

import android.util.Log;
import android.widget.Toast;

import com.example.ee_drive_client.model.DriveAssistant;
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.model.OptimalModel;
import com.example.ee_drive_client.model.Route;
import com.example.ee_drive_client.model.Vertex;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.example.ee_drive_client.repositories.SharedPrefHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class OptimalModelHandler {

    //Variables
    private double x, y;
    private int startIndex;
    private SharedPrefHelper sharedPrefHelper;
    private DriveData driveData;
    private DriveAssistant driveAssistant;
    private String routeId = "0", vertices = "empty", edges = "empty", modelId = "empty";
    private JSONObject verticesJson = null;
    private JSONObject edgesJson = null;

    public boolean isPointInRectangle(double bLx, double bLy, double bRx, double bRy, double tLx, double tLy, double tRx, double tRy, double x, double y) {
        double APDSum = calculateTriangleAreaSum(bLx, bLy, x, y, bRx, bRy);
        double DPCSum = calculateTriangleAreaSum(bRx, bRy, x, y, tRx, tRy);
        double CPBSum = calculateTriangleAreaSum(tRx, tRy, x, y, tLx, tLy);
        double PBASum = calculateTriangleAreaSum(x, y, tLx, tLy, bLx, bLy);
        double rectangleSum = calculateRectangleAreaSum(bLx, bLy, tLx, tLy, bRx, bRy);
        return (APDSum + DPCSum + CPBSum + PBASum) <= rectangleSum;
    }

    public double calculateTriangleAreaSum(double bLx, double bLy, double tLx, double tLy, double tRx, double tRy) {
        return Math.abs((tLx * bLy - bLx * tLy) + (tRx * tLy - tLx * tRy) + (bLx * tRy - tRx * bLy)) / 2;
    }

    public double calculateRectangleAreaSum(double bLx, double bLy, double bRx, double bRy, double tLx, double tLy) {
        double width = Math.sqrt(Math.pow((bLx - tLx), 2) + Math.pow((bLy - tLy), 2));
        double height = Math.sqrt(Math.pow((bLx - bRx), 2) + Math.pow((bLy - bRy), 2));
        return (width * height);
    }

    public String getRouteId(double x, double y) throws JSONException, IOException {
        //TODO get all routes from db
        List<Route> routes = RepositoryCar.getInstance().getAllRoutes();
        for (Route route : routes) {
            JSONObject bl = new JSONObject(route.bl);
            JSONObject br = new JSONObject(route.br);
            JSONObject tl = new JSONObject(route.tl);
            JSONObject tr = new JSONObject(route.tr);
            double bLx = bl.getDouble("lat");
            double bLy = bl.getDouble("long");
            double bRx = br.getDouble("lat");
            double bRy = br.getDouble("long");
            double tLx = tl.getDouble("lat");
            double tLy = tl.getDouble("long");
            double tRx = tr.getDouble("lat");
            double tRy = tr.getDouble("long");
            boolean isPointInRoot = isPointInRectangle(bLx, bLy, bRx, bRy, tLx, tLy, tRx, tRy, x, y);
            if (isPointInRoot) {
                return route.get_id();
            }
        }
        return null;
    }

    public String getOptimalModelVerticesById(String routeId, String carTypeId) throws IOException {
        //TODO select * from OptimalModel where carTypeId=carTypeId and routeId=routeId
        return RepositoryCar.getInstance().getOptimalModelVerticesById(routeId, carTypeId);
    }

    public int getStartVertix(double x, double y, OptimalModel model) throws JSONException {
        ArrayList<Vertex> vertices = model.getVertices();
        double dist = 9999;
        int verindex = 0;
        int len = vertices.size();
        for (int i = 0; i < len; i++) {
            Vertex vertix = vertices.get(i);
            double verX = vertix.get_lat();
            double verY = vertix.get_long();
            double disTemp = Math.sqrt((x - verX) * (x - verX) + (y - verY) * (y - verY));
            if (disTemp < dist) {
                dist = disTemp;
                verindex = vertix.getIndex();
            }
        }
        return verindex;
    }

    public String getOptimalModelEdgesById(String routeId, String id) throws IOException {
        return RepositoryCar.getInstance().getOptimalModelEdgesById(routeId, id);
    }

    public String getOptimalModelId(String routeId, String carTypeId) throws IOException {
        return RepositoryCar.getInstance().getOptimalModelId(routeId, carTypeId);
    }

    public void getNextVertex() {
    }

    public boolean getStartingPointAndCreateModel() throws IOException, JSONException {
        try{
            sharedPrefHelper = SharedPrefHelper.getInstance(GlobalContextApplication.getContext());
            driveData = DriveData.getInstance();
            driveAssistant = DriveAssistant.getInstance();
            String carId = sharedPrefHelper.getId();
            JSONObject optimalModel = new JSONObject();
            Log.d("Drive Data", driveData.getPoints().toString());
            x = driveAssistant.getCurrentX();
            y = driveAssistant.getCurrentY();
            Log.d("drive x ", Double.toString(x));
            routeId = getRouteId(x, y);
            if (routeId == null) {
                driveAssistant.setVertexFound(false);
                return false;
            } else {
                driveAssistant.setVertexFound(true);
            }
            vertices = "{\"vertices\":" + getOptimalModelVerticesById(routeId, carId) + "}";
            if (vertices != null) {
                verticesJson = new JSONObject(vertices);
            }
            edges = "{\"edges\":" + getOptimalModelEdgesById(routeId, carId) + "}";
            if (edges != null) {
                edgesJson = new JSONObject(edges);
            }
            modelId = "{\"_id\":" + getOptimalModelId(routeId, carId) + "}";
            optimalModel.put("vertices", new JSONObject(vertices).getJSONArray("vertices"));
            optimalModel.put("edges", new JSONObject(edges).getJSONArray("edges"));
            optimalModel.put("_id", modelId);
            optimalModel.put("carTypeID", carId);
            optimalModel.put("routeID", routeId);
            OptimalModel newOptimalModel = new OptimalModel(optimalModel);
            startIndex = getStartVertix(x, y, newOptimalModel);
            driveAssistant.setCurrentVertex(newOptimalModel.getVertices().get(startIndex));
            driveAssistant.setCurrentOptimalModel(newOptimalModel);
            Log.d("current vertex", Integer.toString(driveAssistant.getCurrentVertex().getIndex()));
            driveData.getDriveAssist().postValue(true);
            return true;
            //TODO move SELF to onConnect
        }catch (ConcurrentModificationException exception){
            Log.d("exception" , exception.toString());
        }
        return true;
    }
}
