package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "Route")

public class Route {
    @NonNull
    @PrimaryKey
    String _id;
//    routePoint bl, br, tl, tr;


//    public Route(@NonNull JSONObject route) throws JSONException {
//        this._id = route.getString("_id");
//        this.bl = new routePoint(route.getJSONObject("bL").getString("lat"), route.getJSONObject("bL").getString("long"));
//        this.br = new routePoint(route.getJSONObject("bR").getString("lat"), route.getJSONObject("bR").getString("long"));
//        this.tl = new routePoint(route.getJSONObject("tL").getString("lat"), route.getJSONObject("tL").getString("long"));
//        this.tr = new routePoint(route.getJSONObject("tR").getString("lat"), route.getJSONObject("tR").getString("long"));
//
//    }

    private class routePoint {

        double _lat;
        double _long;

        public routePoint(String lat, String _long) {
            this._lat = Double.parseDouble(lat);
            ;
            this._long = Double.parseDouble(_long);
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


    }


    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }
}
