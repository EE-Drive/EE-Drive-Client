package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "CarType")

public class CarType {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    String _id;
    String brand;
    String model;
    String year = "3000";



    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }



    @Ignore
    public CarType(String brand, String model, String year) {
        this._id=null;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }
    public CarType( String _id, String brand, String model, String year) {
        this._id=_id;
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public CarType(CarType ct) {
        this.brand = ct.brand;
        this.model = ct.model;
        this.year = ct.year;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "{" +
                "brand:'" + brand + '\'' +
                ", model:'" + model + '\'' +
                ", year:" + year +
                '}';
    }


    public JSONObject toJsonAddCarTypeToServer() {
        JSONObject json = new JSONObject();
        try {
            json.put("companyName", brand);
            json.put("brandName", model);
            json.put("year", year);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public String loadFullModelForShow() {
        return model+" "+brand+" " +year;
    }
}

