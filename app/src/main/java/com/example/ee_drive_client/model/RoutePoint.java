package com.example.ee_drive_client.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RoutePoint {

    //Variables
    private double _lat, _long;

    public RoutePoint(String lat, String _long) {
        this._lat = Double.parseDouble(lat);
        this._long = Double.parseDouble(_long);
    }

    public RoutePoint(JSONObject jsonObject) throws JSONException {
        this._lat = Double.parseDouble(jsonObject.getString("fromIndex"));
        this._long = Double.parseDouble(jsonObject.getString("toIndex"));
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

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("lat", this._lat);
            json.put("long", this._long);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


}
