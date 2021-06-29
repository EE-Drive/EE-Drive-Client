package com.example.ee_drive_client.model;

import android.util.Log;

import com.example.ee_drive_client.data.Converters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Vertex {

    //Variables
    private double _lat, _long;
    private int index, speed, currentWeight, fatherIndex;
    private String vertixStr;
    private ArrayList<Integer> neighborsList, transposeNeighborsList;

    public Vertex(JSONObject vertex) {
        vertixStr = vertex.toString();
        if (vertixStr.contains("lat") && !vertixStr.contains("_lat")) {
            vertixStr = vertixStr.replace("lat", "_lat");
            vertixStr = vertixStr.replace("long", "_long");
        }
        try {
            vertex = new JSONObject(vertixStr);
            initializeVertexFromJsonObject(vertex);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initializeVertexFromJsonObject(JSONObject vertex) throws JSONException {
        this.index = Integer.parseInt(vertex.getString("index"));
        this._lat = Double.parseDouble(vertex.getString("_lat"));
        this._long = Double.parseDouble(vertex.getString("_long"));
        this.speed = Integer.parseInt(vertex.getString("speed"));
        if (vertex.getString("currentWeight") != "null") {
            this.currentWeight = Integer.parseInt(vertex.getString("currentWeight"));
        } else {
            this.currentWeight = -1;
        }
        if (vertex.getString("fatherIndex") != "null") {
            this.fatherIndex = Integer.parseInt(vertex.getString("fatherIndex"));
        } else {
            this.fatherIndex = -1;
        }
        this.neighborsList = new ArrayList<>();
        this.transposeNeighborsList = new ArrayList<>();
        JSONArray neighbors = vertex.getJSONArray("neighborsList");
        for (int i = 0; i < neighbors.length(); i++) {
            this.neighborsList.add(neighbors.getInt(i));
        }
        JSONArray transNeighbors = vertex.getJSONArray("transposeNeighborsList");
        for (int i = 0; i < transNeighbors.length(); i++) {
            this.transposeNeighborsList.add(transNeighbors.getInt(i));
        }
    }


    public Vertex(int index, double _lat, double _long, int speed, int currentWeight, int fatherIndex, ArrayList<Integer> neighborsList, ArrayList<Integer> transposeNeighborsList) {
        this.index = index;
        this._lat = _lat;
        this._long = _long;
        this.speed = speed;
        this.currentWeight = currentWeight;
        this.fatherIndex = fatherIndex;
        this.neighborsList = neighborsList;
        this.transposeNeighborsList = transposeNeighborsList;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public ArrayList<Integer> getTransposeNeighborsList() {
        return transposeNeighborsList;
    }

    public void setTransposeNeighborsList(ArrayList<Integer> transposeNeighborsList) {
        this.transposeNeighborsList = transposeNeighborsList;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public int getFatherIndex() {
        return fatherIndex;
    }

    public void setFatherIndex(int fatherIndex) {
        this.fatherIndex = fatherIndex;
    }

    public ArrayList<Integer> getNeighborsList() {
        return neighborsList;
    }

    public void setNeighborsList(ArrayList<Integer> neighborsList) {
        this.neighborsList = neighborsList;
    }

    public double get_lat() {
        return _lat;
    }

    public void set_lat(double _lat) {
        this._lat = _lat;
    }

    public double get_long() {
        return _long;
    }

    public void set_long(double _long) {
        this._long = _long;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public JSONObject toJson() {
        Converters converters = new Converters();
        JSONObject json = new JSONObject();
        JSONArray jsonNeighborsList = new JSONArray();
        jsonNeighborsList = converters.fromIntArray(this.neighborsList);
        JSONArray jsonTranNeighborsList = new JSONArray();
        jsonNeighborsList = converters.fromIntArray(this.transposeNeighborsList);

        try {
            json.put("_lat", this._lat);
            json.put("_long", this._long);
            json.put("currentWeight", this.currentWeight);
            json.put("speed", this.speed);
            json.put("fatherIndex", this.fatherIndex);
            json.put("index", this.index);
            json.put("neighborsList", jsonNeighborsList);
            json.put("transposeNeighborsList", jsonTranNeighborsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}