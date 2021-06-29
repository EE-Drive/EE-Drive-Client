package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "Route")

public class Route {

    //Variables
    @NonNull
    @PrimaryKey
    private String _id;
    public String bl, br, tl, tr;

    public Route() {
    }

    public Route(@NonNull JSONObject route) throws JSONException {
        this._id = route.getString("_id");
        this.bl = route.getString("bL");
        this.br = route.getString("bR");
        this.tl = route.getString("tL");
        this.tr = route.getString("tR");

    }


    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }
}
