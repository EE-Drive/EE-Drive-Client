package com.example.ee_drive_client.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Car")
public class Car {

    private String brand;
    private String model;
    private int year;

    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String uniqueModel;

    public Car() {
    }

    public Car(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        initUniqueModel();
    }

    public void setUniqueModel(String uniqueModel) {
        this.uniqueModel = uniqueModel;
    }

    public String getUniqueModel() {
        return uniqueModel;
    }

    private void initUniqueModel() {
        this.uniqueModel = brand + "_" + model + "_" + year;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String loadFullModelForShow() {
        return model+" "+brand+" " +year;
    }
}
