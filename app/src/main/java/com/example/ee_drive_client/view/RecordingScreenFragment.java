package com.example.ee_drive_client.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.R;
import com.example.ee_drive_client.controller.AppController;
import com.example.ee_drive_client.controller.DrivingController;
import com.example.ee_drive_client.model.DriveAssistant;
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;


public class RecordingScreenFragment extends Fragment {

    //Variables
    private AppController mainController;
    private DrivingController drivingController;
    private DriveData driveData;
    private TextView txtSpeed, txtRecording, txtFuel, txtConnected, recommendedSpeed, endDriveText;
    private FloatingActionButton endBtn,assistBtn;
    private ImageButton obdBtn;

    public RecordingScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recording_screen, container, false);

        initializeVariables(view);
        initializeEventListeners(view);
        initializeData();
        return view;
    }

    private void initializeData() {
        try {
            startLocationUpdates();
        } catch (IOException | UnirestException | JSONException exception) {
            exception.printStackTrace();
        }
    }

    private void initializeVariables(View view) {
        driveData = DriveData.getInstance();
        //TODO:Only with drive assist button on
        endBtn = view.findViewById(R.id.recording_stop_btn);
        txtSpeed = view.findViewById(R.id.recording_speed_dynmTxt);
        txtFuel = view.findViewById(R.id.recording_fuelCons_dynmTxt);
        txtRecording = view.findViewById(R.id.recording_record_txt);
        txtConnected = view.findViewById(R.id.recording_connected_txt);
      //  recommendedSpeed = view.findViewById(R.id.recording_speedRecommanded_txt);
        //recommendedSpeed.setVisibility(View.INVISIBLE);
        endDriveText = view.findViewById(R.id.recording_end_text);
        assistBtn=view.findViewById(R.id.recording_assist_btn);
        assistBtn.setVisibility(View.INVISIBLE);
        endDriveText.setVisibility(View.INVISIBLE);
        obdBtn = view.findViewById(R.id.recording_obd_btn);

        try {
            mainController = new AppController((MainActivity) getActivity());
            drivingController = new DrivingController((MainActivity) getActivity());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }


    private void initializeEventListeners(View view) {
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: remove observers
                driveData.getSpeed().removeObservers(getViewLifecycleOwner());
                try {
                    drivingController.onStop();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                Navigation.findNavController(view).navigate(R.id.action_recording_to_main);
            }

        });
        obdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "btn");
                Log.d("Stored id", SharedPrefHelper.getInstance(getContext()).getId());
                mainController.onObdConnect((MainActivity) getActivity(), getContext());

            }
        });

        driveData.getSpeed().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                txtSpeed.setText(integer.toString());
            }
        });
        assistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_recording_to_assist);
            }
        });
 //       DriveAssistant.getInstance().increaseSpeed(0).observe(getViewLifecycleOwner(), new Observer<String>() {
   //         @Override
     //       public void onChanged(String s) {
       //         recommendedSpeed.setVisibility(View.VISIBLE);
         //       recommendedSpeed.setText(s);
         //   }
       // });

        driveData.getDriveAssist().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                assistBtn.setVisibility(View.VISIBLE);
                else
                    assistBtn.setVisibility(View.INVISIBLE);
            }
        });

        driveData.getFuel().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                txtFuel.setText(Double.toString(aDouble));
            }
        });
        driveData.getRecordingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == true) {
                    txtRecording.setText("Recording");
                    endDriveText.setVisibility(View.VISIBLE);
                    txtConnected.setText("Connected");
                }
                if (aBoolean == false) {
                    txtRecording.setText("Please choose an Obd");
                    txtConnected.setText("Not connected");
                }
            }
        });
    }


    private void startLocationUpdates() throws IOException, UnirestException, JSONException {
        mainController.onStart((MainActivity) getActivity());
    }

}