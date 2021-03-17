package com.example.ee_drive_client.model;

public class CarType {

    String brand;
    String model;
    int year = 0;

    public CarType(String brand, String model, int year) {
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
}

