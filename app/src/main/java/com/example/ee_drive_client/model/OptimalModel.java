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

    //Variables
    @NonNull
    @PrimaryKey
    public String _id;
    public String carTypeId,routeId;
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;


    public OptimalModel(@NonNull String _id) {
        this._id = _id;
    }

    public OptimalModel(JSONObject model) throws JSONException {
        this._id=model.getString("_id");
        this.routeId=model.getString("routeID");
        this.carTypeId=model.getString("carTypeID");
        this.vertices=new ArrayList<>();
        this.edges=new ArrayList<>();
        JSONArray edgesArray=model.getJSONArray("edges");
        JSONArray vertexArray=model.getJSONArray("vertices");
        for(int i=0;i<vertexArray.length();i++){
            vertices.add(new Vertex((JSONObject) vertexArray.get(i)));
        }
        for(int i=0;i<edgesArray.length();i++){
            edges.add(new Edge((JSONObject) edgesArray.get(i)));
        }

    }

    public OptimalModel() {

    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "OptimalModel{" +
                "_id='" + _id + '\'' +
                ", carTypeId='" + carTypeId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", vertices=" + vertices +
                ", edges=" + edges +
                '}';
    }
}
