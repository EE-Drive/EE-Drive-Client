package com.example.ee_drivefinal.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OptimalModel {

    //Variables
    private boolean inModel;
    public String _id;
    public String carTypeId;
    public String routeId;
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    private int CurrentVertex;

    public OptimalModel(String _id) {
        this._id = _id;
    }

    public OptimalModel(JSONObject model) throws JSONException {
        String currentFieldValue = model.get("model")==null ? "" : model.get("model").toString();



        if(!currentFieldValue.equals("")) {
            JSONArray modelArr = new JSONArray(model.get("model").toString());
            model= (JSONObject) modelArr.get(0);
            this.inModel=true;
            this._id = model.getString("_id");
            this.routeId = model.getString("routeID");
            this.carTypeId = model.getString("carTypeID");
            this.vertices = new ArrayList<>();
            this.edges = new ArrayList<>();
            JSONArray edgesArray = model.getJSONArray("edges");
            JSONArray vertexArray = model.getJSONArray("vertices");
            for (int i = 0; i < vertexArray.length(); i++) {
                vertices.add(new Vertex((JSONObject) vertexArray.get(i)));
            }
            for (int i = 0; i < edgesArray.length(); i++) {
                edges.add(new Edge((JSONObject) edgesArray.get(i)));
            }
        }
        else{
            this.inModel=false;
            this.setCurrentVertex(-1);
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
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

    public boolean isInModel() {
        return inModel;
    }

    public void setInModel(boolean inModel) {
        this.inModel = inModel;
    }

    public int getCurrentVertex() {
        return CurrentVertex;
    }

    public void setCurrentVertex(int currentVertex) {
        CurrentVertex = currentVertex;
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


    public void setCurrentVertexIndex(double x, double y) {
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
        if (dist < 0.0007) {
            this.inModel = true;
            setCurrentVertex(verindex);
        } else {
            this.inModel = false;
            setCurrentVertex(-1);
        }
    }

    public int getCurrentSpeed() {

        return vertices.get(CurrentVertex).getSpeed();


    }

    public int getCurrentSpeedForTTS() {
        int father = vertices.get(CurrentVertex).getFatherIndex();
        father = vertices.get(father).getFatherIndex();
        father = vertices.get(father).getFatherIndex();
        return vertices.get(father).getSpeed();


    }
}