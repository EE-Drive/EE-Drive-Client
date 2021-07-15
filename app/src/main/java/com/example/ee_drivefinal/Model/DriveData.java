package com.example.ee_drivefinal.Model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drivefinal.Repositories.JsonHandler;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.example.ee_drivefinal.ViewModel.DrivingViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DriveData {

    //Variables
    private String id, timeAndDate;
    private Boolean driveInProcess = false,isSentToServer = false, driverAssist = false;
    private CarType carType;
    private static DriveData instance;
    private String obdType;
    private Thread thread;
    private Date date;
    private SimpleDateFormat formatter;
    private JsonHandler jsonHandler;
    private JSONObject jsonObjectToServer;
    private Integer mSpeed=0;
    private SharedPrefHelper sharedPrefHelper;
    private double mFuel=0,mMaf=0,mRpm=0,mIat,mMap;
    private JSONArray fuel;
    private JSONArray speed;
    private ArrayList<String[]> mObdData;
    public ArrayList<Point> points = new ArrayList<Point>();
    private MutableLiveData<Integer> currentRecommendedSpeed ;

    public DriveData(String id, boolean driverAssist, CarType carType) {
        this.id = id;
        this.driverAssist = driverAssist;
        this.carType = carType;
        jsonHandler = new JsonHandler();
        formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        date = new Date(System.currentTimeMillis());
        this.timeAndDate = formatter.format(date);
        this.currentRecommendedSpeed = new MutableLiveData<>();
    }
    public void resetData() {
        instance = new DriveData("", false, new CarType(SharedPrefHelper.getInstance().getBrand(), SharedPrefHelper.getInstance().getModel(), SharedPrefHelper.getInstance().getYear(), Integer.toString(SharedPrefHelper.getInstance().getEngine())));
    }

    public MutableLiveData<Integer> getCurrentRecommendedSpeed() {
        return currentRecommendedSpeed;
    }

    private int getPointsSize(){
        return this.points.size();
    }

    public void addInfoToLastPoint() {
        this.points.get(this.getPointsSize() - 1).appendSpeed(mSpeed);
        if (mFuel != 0)
            this.points.get(this.getPointsSize() - 1).appendFuel(mFuel);
        else {
            //TODO: A CAR DOES NOT SUPPORT getFuel
        }
    }
    public void addPoint(Point point) {
        this.points.add(point);
    }
    public static DriveData getInstance() {
        if (instance == null)
            instance = new DriveData("", false, new CarType(SharedPrefHelper.getInstance().getBrand(), SharedPrefHelper.getInstance().getModel(), SharedPrefHelper.getInstance().getYear(), Integer.toString(SharedPrefHelper.getInstance().getEngine())));
        return instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeAndDate() {
        return timeAndDate;
    }

    public void setTimeAndDate(String timeAndDate) {
        this.timeAndDate = timeAndDate;
    }

    public Boolean getDriveInProcess() {
        return driveInProcess;
    }

    public void setDriveInProcess(Boolean driveInProcess) {
        this.driveInProcess = driveInProcess;
    }

    public Boolean getSentToServer() {
        return isSentToServer;
    }

    public void setSentToServer(Boolean sentToServer) {
        isSentToServer = sentToServer;
    }

    public Boolean getDriverAssist() {
        return driverAssist;
    }

    public void setDriverAssist(Boolean driverAssist) {
        this.driverAssist = driverAssist;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public double getmFuel() {
        return mFuel;
    }

    public void setmFuel(double mFuel) {
        this.mFuel = mFuel;
    }


    public static void setInstance(DriveData instance) {
        DriveData.instance = instance;
    }

    public Thread getThread() {
        return thread;
    }

    public double getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
    }

    public double getmMaf() {
        return mMaf;
    }

    public void setmMaf(double mMaf) {
        this.mMaf = mMaf;
    }

    public double getmRpm() {
        return mRpm;
    }

    public void setmRpm(double mRpm) {
        this.mRpm = mRpm;
    }

    public double getmIat() {
        return mIat;
    }

    public void setmIat(double mIat) {
        this.mIat = mIat;
    }

    public double getmMap() {
        return mMap;
    }

    public void setmMap(double mMap) {
        this.mMap = mMap;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public JsonHandler getJsonHandler() {
        return jsonHandler;
    }

    public void setJsonHandler(JsonHandler jsonHandler) {
        this.jsonHandler = jsonHandler;
    }

    public JSONObject getJsonObjectToServer() {
        return jsonObjectToServer;
    }

    public void setJsonObjectToServer(JSONObject jsonObjectToServer) {
        this.jsonObjectToServer = jsonObjectToServer;
    }

    public SharedPrefHelper getSharedPrefHelper() {
        return sharedPrefHelper;
    }

    public void setSharedPrefHelper(SharedPrefHelper sharedPrefHelper) {
        this.sharedPrefHelper = sharedPrefHelper;
    }

    public String getObdType() {
        return obdType;
    }

    public void setObdType(String obdType) {
        this.obdType = obdType;
    }

    public JSONArray getFuel() {
        return fuel;
    }

    public void setFuel(JSONArray fuel) {
        this.fuel = fuel;
    }

    public JSONArray getSpeed() {
        return speed;
    }

    public void setSpeed(JSONArray speed) {
        this.speed = speed;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", this.id);
            json.put("isSentToServer", this.isSentToServer);
            json.put("timeAndDate", this.timeAndDate);
            json.put("driverAssist", this.driverAssist);
            json.put("carType", this.carType);
            json.put("driveRawData", jsonHandler.pointArrayToJson(this.points));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void writeData() throws IOException {
        Log.d("Data", "Writing data");
        jsonObjectToServer = jsonHandler.toJsonSaveFile(this);
        jsonHandler.saveToFile(getTimeAndDate(), jsonObjectToServer);
    }
    public void updateCurrentCar() {
        this.setCarType(new CarType(sharedPrefHelper.getBrand(), sharedPrefHelper.getModel(), sharedPrefHelper.getYear(), Integer.toString(sharedPrefHelper.getEngine())));
    }

    public void updateObd(String[] obdCall) {
        if(mObdData!=null)
            mObdData.add(obdCall);
        switch (obdCall[1].charAt(0)) {
            case 'V': //Vehicle Speed
                mSpeed = Integer.parseInt(obdCall[2]);
                Log.d("Speed",Double.toString(mSpeed));
                break;
            case 'F': //Fuel consumption rate
                mFuel = Double.parseDouble(obdCall[2]);
                Log.d("Fuel",Double.toString(mFuel));
                break;
            case 'M': //Mass Air Flow
                mMaf = Double.parseDouble(obdCall[2]);
                Log.d("Maf",Double.toString(mMaf));
                break;
            case 'E': //Engine RPM
                mRpm = Double.parseDouble(obdCall[2]);
                Log.d("Rpm",Double.toString(mRpm));
                break;
            case 'A': //Air Intake Temperature
                mIat = Double.parseDouble(obdCall[2]);
                Log.d("mIat",Double.toString(mIat));
                break;
            case 'I': //Intake Manifold Pressure
                mMap = Double.parseDouble(obdCall[2]);
                Log.d("mMap",Double.toString(mMap));
                break;
        }
    }

    public Point getLastPoint() {
        return this.points.get(getPointsSize() - 1);
    }

}
