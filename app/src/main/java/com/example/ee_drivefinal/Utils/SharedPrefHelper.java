package com.example.ee_drivefinal.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefHelper {

    //Variables
    private static final String ID_KEY = "ID_KEY",ENGINE_KEY = "ENGINE_KEY",BRAND_KEY = "BRAND_KEY",MODEL_KEY = "MODEL_KEY_KEY",YEAR_KEY = "YEAR_KEY",OBD_TYPE="TYPE";
    private boolean firstTime = false;
    private static SharedPrefHelper instance;
    private final SharedPreferences preferences;

    private SharedPrefHelper(Context context) {
        preferences = context.getSharedPreferences("APP_SETTINGS", Context.MODE_PRIVATE);
    }

    public SharedPrefHelper(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static SharedPrefHelper getInstance() {
        if (instance == null)
            instance = new SharedPrefHelper(GlobalContextApplication.getContext());
        return instance;
    }

    public void storeId(String id) {
        preferences.edit().putString(ID_KEY, id).apply();
        Log.d("Current car id", id);
    }

    public void storeEngine(String engine) {
        preferences.edit().putInt(ENGINE_KEY, Integer.parseInt(engine)).apply();
        Log.d("Current engine", engine);
    }

    public void storeBrand(String brand) {
        preferences.edit().putString(BRAND_KEY, brand).apply();
        Log.d("Current Brand", brand);

    }

    public void storeModel(String model) {
        preferences.edit().putString(MODEL_KEY, model).apply();
        Log.d("Current model", model);

    }

    public void setObdType(String value){
        preferences.edit().putString("obdType",value).commit();
    }

    public void storeYear(String year) {
        preferences.edit().putString(YEAR_KEY, year).apply();
        Log.d("Current year", year);

    }
    public String getObdType(){
        return preferences.getString("obdType",null);
    }

    public String getId() {
        return preferences.getString(ID_KEY, null);
    }
    public int getEngine() {
        return preferences.getInt(ENGINE_KEY, 0);
    }
    public String getBrand() {
        return preferences.getString(BRAND_KEY, null);
    }
    public String getModel() {
        return preferences.getString(MODEL_KEY, null);
    }
    public String getYear() {
        return preferences.getString(YEAR_KEY, null);
    }


}
