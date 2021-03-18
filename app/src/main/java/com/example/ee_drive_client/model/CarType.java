package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CarType")

public class CarType {

    String brand;
    String model;
    int year = 0;
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String uniqueModel;

    @NonNull
    public String getUniqueModel() {
        return uniqueModel;
    }

    public void setUniqueModel(@NonNull String uniqueModel) {
        this.uniqueModel = uniqueModel;
    }

    public CarType(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        initUniqueModel();
    }

    public CarType(CarType ct) {
        this.brand = ct.brand;
        this.model = ct.model;
        this.year = ct.year;
    }

    public String getBrand() {
        return brand;
    }

    private void initUniqueModel() {
        this.uniqueModel = brand + "_" + model + "_" + year;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
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
    public String loadFullModelForShow() {
        return model+" "+brand+" " +year;
    }

}

