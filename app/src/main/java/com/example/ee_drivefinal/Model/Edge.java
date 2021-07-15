package com.example.ee_drivefinal.Model;

import org.json.JSONException;
import org.json.JSONObject;

public class Edge {

    //Variables
    private int fromIndex, toIndex;
    private double weight;

    public Edge(JSONObject edge) throws JSONException {
        this.fromIndex = Integer.parseInt(edge.getString("fromIndex"));
        this.toIndex = Integer.parseInt(edge.getString("toIndex"));
        this.weight = Double.parseDouble(edge.getString("weight"));
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

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("fromIndex", this.fromIndex);
            json.put("toIndex", this.toIndex);
            json.put("weight", this.weight);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
