package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@Entity(tableName = "CarType")
public class CarType {
    @NonNull
    @PrimaryKey
    String _id;
    String brand;
    String model;
    String year = "3000";
    String engineDisplacement;


    @Ignore
    public CarType(String id){
        this._id=id;
    }
    public String getEngineDisplacement() {
        return engineDisplacement;
    }

    public void setEngineDisplacement(String engineDisplacement) {
        this.engineDisplacement = engineDisplacement;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    @Ignore
    public CarType(String brand, String model, String year,String  engineDisplacement) {
        this._id=null;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.engineDisplacement=engineDisplacement;
    }
    public CarType( String _id, String brand, String model, String year,String  engineDisplacement) {
        this._id=_id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.engineDisplacement=engineDisplacement;
    }

    public CarType(CarType ct) {
        this.brand = ct.brand;
        this.model = ct.model;
        this.year = ct.year;
        this.engineDisplacement=ct.engineDisplacement;
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
                ", year:" + year + '\'' +
                ", engineDisplacement:" + engineDisplacement+
                '}';
    }


    public JSONObject toJsonAddCarTypeToServer() {
        JSONObject json = new JSONObject();
        try {
            json.put("companyName", brand);
            json.put("brandName", model);
            json.put("year", year);
            json.put("engineDisplacement",engineDisplacement);


        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String loadFullModelForShow() {
        return brand+" "+model+" "+year+" "+engineDisplacement;
    }

}