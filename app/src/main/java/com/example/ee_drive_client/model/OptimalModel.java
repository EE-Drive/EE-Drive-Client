package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@Entity(tableName = "OptimalModel")

public class OptimalModel {
    @NonNull
    @PrimaryKey
    String _id;
    String carTypeId;
    String routeId;
//    ArrayList<Vertex> vertices;
//    ArrayList<Edge> edges;


    public OptimalModel(@NonNull String _id) {
        this._id = _id;
    }

    public OptimalModel(JSONObject model) throws JSONException {
        this._id=model.getString("_id");
        this.routeId=model.getString("routeID");
        this.carTypeId=model.getString("carTypeID");
        JSONArray edgesArray=model.getJSONArray("edges");
        JSONArray vertexArray=model.getJSONArray("vertices");
//        for(int i=0;i<vertexArray.length();i++){
//            vertices.add(new Vertex((JSONObject) vertexArray.get(i)));
//        }
//        for(int i=0;i<edgesArray.length();i++){
//            edges.add(new Edge((JSONObject) vertexArray.get(i)));
//        }

    }




    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }


    private class Vertex{
        int index;
        double _lat;
        double _long;
        int speed,currentWeight,fatherIndex;
        ArrayList<Integer> neighborsList,transposeNeighborsList;

        public Vertex(JSONObject vertex) throws JSONException {
            this.index=Integer.parseInt(vertex.getString("fromIndex"));
            this._lat=Double.parseDouble(vertex.getString("lat"));
            this._long=Double.parseDouble(vertex.getString("long"));
            this.speed=Integer.parseInt(vertex.getString("speed"));
            this.currentWeight=Integer.parseInt(vertex.getString("currentWeight"));
            this.fatherIndex=Integer.parseInt(vertex.getString("fatherIndex"));
            this.neighborsList= (ArrayList<Integer>) vertex.get("neighborsList");
            this.transposeNeighborsList= (ArrayList<Integer>) vertex.get("transposeNeighborsList");



        }


        public Vertex(int index, double _lat, double _long, int speed, int currentWeight, int fatherIndex, ArrayList<Integer> neighborsList,ArrayList<Integer> transposeNeighborsList) {
            this.index = index;
            this._lat = _lat;
            this._long = _long;
            this.speed = speed;
            this.currentWeight = currentWeight;
            this.fatherIndex = fatherIndex;
            this.neighborsList = neighborsList;
            this.transposeNeighborsList=transposeNeighborsList;
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


    private class Edge {
        int fromIndex,toIndex;
        double weight;

        public Edge(JSONObject edge) throws JSONException {
            this.fromIndex=Integer.parseInt(edge.getString("fromIndex"));
            this.toIndex=Integer.parseInt(edge.getString("toIndex"));
            this.weight=Integer.parseInt(edge.getString("weight"));
        }


        public Edge(int fromIndex, int toIndex, double weight) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.weight = weight;
        }

        public int getFromIndex() {
            return fromIndex;
        }

        public void setFromIndex(int fromIndex) {
            this.fromIndex = fromIndex;
        }

        public int getToIndex() {
            return toIndex;
        }

        public void setToIndex(int toIndex) {
            this.toIndex = toIndex;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }

}
