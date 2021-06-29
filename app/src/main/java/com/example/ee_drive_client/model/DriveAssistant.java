package com.example.ee_drive_client.model;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ee_drive_client.controller.OptimalModelHandler;
import com.example.ee_drive_client.repositories.GlobalContextApplication;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class DriveAssistant {

    //Variables
    private int currentRecommendedSpeed, nextRecommendedSpeed, currentSpeed, index, displaySpeed;
    private Vertex currentVertex;
    private double currentX, currentY;
    private ArrayList<Integer> upcomingRecommendedSpeeds;
    private OptimalModel currentOptimalModel;
    private static DriveAssistant instance;
    private OptimalModelHandler optimalModelHandler;
    private boolean vertexFound;

    public DriveAssistant(int currentRecommendedSpeed, int nextRecommendedSpeed, ArrayList<Integer> upcomingRecommendedSpeeds, OptimalModel currentOptimalModel) {
        this.currentRecommendedSpeed = currentRecommendedSpeed;
        this.nextRecommendedSpeed = nextRecommendedSpeed;
        this.upcomingRecommendedSpeeds = upcomingRecommendedSpeeds;
        this.currentOptimalModel = currentOptimalModel;
        this.optimalModelHandler = new OptimalModelHandler();
    }

    public DriveAssistant() {
        this.optimalModelHandler = new OptimalModelHandler();
    }

    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    public void setCurrentVertex(Vertex currentVertex) {
        this.currentVertex = currentVertex;
    }

    public static DriveAssistant getInstance() {
        if (instance == null)
            instance = new DriveAssistant();
        return instance;
    }

    public int getCurrentRecommendedSpeed() {
        return currentRecommendedSpeed;
    }

    public void setCurrentRecommendedSpeed(int currentRecommendedSpeed) {
        this.currentRecommendedSpeed = currentRecommendedSpeed;
    }

    public ArrayList<Integer> getUpcomingRecommendedSpeeds() {
        return upcomingRecommendedSpeeds;
    }

    public void setUpcomingRecommendedSpeeds(ArrayList<Integer> upcomingRecommendedSpeeds) {
        this.upcomingRecommendedSpeeds = upcomingRecommendedSpeeds;
    }

    public double getCurrentX() {
        return currentX;
    }

    public void setCurrentX(double currentX) {
        this.currentX = currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public void setCurrentY(double currentY) {
        this.currentY = currentY;
    }

    public OptimalModel getCurrentOptimalModel() {
        return currentOptimalModel;
    }

    public void setCurrentOptimalModel(OptimalModel currentOptimalModel) {
        this.currentOptimalModel = currentOptimalModel;
    }

    public int getNextSpeed() {
        int current = this.currentVertex.getIndex();
        int next = (int) (((DriveAssistant.getInstance().getCurrentVertex().getSpeed() / 3.6) * 5) / 50);
        return current + next;
    }

    public void startInstructor() {
        currentSpeed = instance.getCurrentVertex().getSpeed();
        index = getNextSpeed();
        instance.setCurrentVertex(instance.getCurrentOptimalModel().getVertices().get(index));
        displaySpeed = instance.getCurrentVertex().getSpeed();
        if (displaySpeed > currentSpeed) {
            this.setCurrentRecommendedSpeed(displaySpeed);
            instance.getLiveIncrease.postValue( Integer.toString(displaySpeed));
            instance.changeSpeedType.postValue("Higher");
            increaseSpeed(displaySpeed);
        } else if (displaySpeed < currentSpeed) {
            this.setCurrentRecommendedSpeed(displaySpeed);
            instance.getLiveIncrease.postValue( Integer.toString(displaySpeed));
            instance.changeSpeedType.postValue("Lower");
            decreaseSpeed(displaySpeed);
        } else {
            this.setCurrentRecommendedSpeed(displaySpeed);
            instance.getLiveIncrease.postValue( Integer.toString(displaySpeed));
            instance.changeSpeedType.postValue("Stay");

            staySpeed(displaySpeed);
        }
        Log.d("new speed", Integer.toString(displaySpeed));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //TODO: while driveData.driveAssist==true
//                while (true) {
//                    Log.d("start of thread","start of instruct");
//                    try {
//                        Thread.sleep(3000);
//                        int currentSpeed=DriveAssistant.getInstance().getCurrentVertex().getSpeed();
//                        int index = getNextSpeed();
//                        DriveAssistant.getInstance().setCurrentVertex(DriveAssistant.getInstance().getCurrentOptimalModel().getVertices().get(index));
//                        int displaySpeed=DriveAssistant.getInstance().getCurrentVertex().getSpeed();
//                        if(displaySpeed>currentSpeed){
//                            increaseSpeed(displaySpeed);
//                        }else if(displaySpeed<currentSpeed){
//                            decreaseSpeed(displaySpeed);
//                        }else{
//                            staySpeed(displaySpeed);
//                        }
//                        Log.d("new speed",Integer.toString(displaySpeed));
//                    }catch (InterruptedException exception){
//                    }
//                }
//            }
//        }).start();


    }

    public boolean reCalculatedVertex() throws IOException, JSONException {
        return optimalModelHandler.getStartingPointAndCreateModel();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        try {
//                            Thread.sleep(20000);
//
//                            optimalModelHandler.getStartingPointAndCreateModel();
//                        } catch (IOException exception) {
//                            exception.printStackTrace();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (InterruptedException exception) {
//                            exception.printStackTrace();
//                        }
//                    }
//                }).start();
    }

    MutableLiveData<String> getLiveIncrease = new MutableLiveData<String>();
    public MutableLiveData<String> increaseSpeed(int displaySpeed) {
        return this.getLiveIncrease;
        //TODO: update live data in recording fragment. Voice commends.
    }

    MutableLiveData<String> changeSpeedType = new MutableLiveData<String>();
    public MutableLiveData<String> changeSpeedType() {
        return this.changeSpeedType;
        //TODO: update live data in recording fragment. Voice commends.
    }


    public void decreaseSpeed(int displaySpeed) {

        //TODO: update live data in recording fragment. Voice commends.
    }

    public void staySpeed(int displaySpeed) {

        //TODO: update live data in recording fragment. Voice commends.
    }

    public boolean isVertexFound() {
        return vertexFound;
    }

    public void setVertexFound(boolean vertexFound) {
        this.vertexFound = vertexFound;
    }
}
