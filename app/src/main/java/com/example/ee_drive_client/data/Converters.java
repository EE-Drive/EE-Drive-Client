package com.example.ee_drive_client.data;

import androidx.room.TypeConverter;

import com.example.ee_drive_client.model.Edge;
import com.example.ee_drive_client.model.RoutePoint;
import com.example.ee_drive_client.model.Vertex;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {

    @TypeConverter
    public static String fromVertexArrayList(ArrayList<Vertex> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ArrayList<Vertex> fromVertexString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    //Edge
    @TypeConverter
    public static String fromEdgeArrayList(ArrayList<Edge> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ArrayList<Edge> fromEdgeString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    //RoutePoint
    @TypeConverter
    public static String fromRoutPoint(RoutePoint routePoint) {
        Gson gson = new Gson();
        String json = gson.toJson(routePoint);
        return json;
    }

    @TypeConverter
    public static RoutePoint toRoutePoint(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }


    @TypeConverter
    public JSONArray fromIntArray(ArrayList<Integer> integers) {
        return new JSONArray(integers);
    }

    @TypeConverter
    public ArrayList<Integer> toIntArray(JSONArray json) throws JSONException {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        for (int i = 0; i < json.length(); i++) {
            integers.add(json.getInt(i));
        }
        return integers;
    }

}
