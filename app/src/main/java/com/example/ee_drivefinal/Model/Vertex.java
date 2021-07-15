package com.example.ee_drivefinal.Model;

import com.example.ee_drivefinal.Utils.FileHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Vertex {

    //Variables
    private int index;
    private double _lat;
    private double _long;
    private int speed, currentWeight, fatherIndex;
    private ArrayList<Integer> neighborsList, transposeNeighborsList;

    public Vertex(JSONObject vertex)  {
        String vertixStr= vertex.toString();

        try {
            vertex=new JSONObject(vertixStr);

            this.index = Integer.parseInt(vertex.getString("index"));
            this._lat = Double.parseDouble(vertex.getString("lat"));
            this._long = Double.parseDouble(vertex.getString("long"));
            this.speed = Integer.parseInt(vertex.getString("speed"));
            if(!vertex.getString("currentWeight").equals("null")){
                this.currentWeight = Integer.parseInt(vertex.getString("currentWeight"));
            }else{
                this.currentWeight = -1;
            }
            if(!vertex.getString("fatherIndex").equals("null")){
                this.fatherIndex = Integer.parseInt(vertex.getString("fatherIndex"));
            }else{
                this.fatherIndex=-1;
            }
            this.neighborsList=new ArrayList<>();
            this.transposeNeighborsList=new ArrayList<>();
            //ArrayList <Integer> points= new ArrayList<Integer> ((Collection<? extends ArrayList<Integer>>) vertex.get("neighborsList"));
            JSONArray neighbors = vertex.getJSONArray("neighborsList");
            for(int i=0; i<neighbors.length(); i++) {
                //Printing each element of ArrayList
                this.neighborsList.add(neighbors.getInt(i));
            }
            JSONArray transNeighbors = vertex.getJSONArray("transposeNeighborsList");
            for(int i=0; i<transNeighbors.length(); i++) {
                //Printing each element of ArrayList
                this.transposeNeighborsList.add(transNeighbors.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            FileHandler.appendLog(e.toString());
        }

        //this.neighborsList = (ArrayList<Integer>) vertex.get("neighborsList");
        //      this.transposeNeighborsList = (ArrayList<Integer>) vertex.get("transposeNeighborsList");


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


}
