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
import com.example.ee_drive_client.model.DriveData;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;


public class RecordingScreenFragment extends Fragment {
    AppController mainController;
    DrivingController drivingController;
    DriveData driveData;

    TextView txtSpeed;
    TextView txtRecording;
    TextView txtFuel;
    TextView txtConnected;
    public RecordingScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            mainController = new AppController((MainActivity) getActivity());
            drivingController = new DrivingController((MainActivity) getActivity());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        startLocationUpdates();
        driveData=DriveData.getInstance();
        View view= inflater.inflate(R.layout.fragment_recording_screen, container, false);
        FloatingActionButton endBtn= view.findViewById(R.id.recording_stop_btn);
        txtSpeed = view.findViewById(R.id.recording_speed_dynmTxt);
        txtFuel=view.findViewById(R.id.recording_fuelCons_dynmTxt);
        txtRecording=view.findViewById(R.id.recording_record_txt);
        txtConnected=view.findViewById(R.id.recording_connected_txt);
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
        ImageButton obdBtn = view.findViewById(R.id.recording_obd_btn);
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


        driveData.getFuel().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                txtFuel.setText(Double.toString(aDouble));
            }
        });
       driveData.getRecordingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean==true){
                    txtRecording.setText("Recording");
                    txtConnected.setText("Connected");
                }
                if(aBoolean==false){
                    txtRecording.setText("Please choose an Obd");
                    txtConnected.setText("Not connected");
                }

            }
        });

        //Recieve Paremeters: String id= StudentInfoFragm.fromBundle(getAguments().getStudentId();
        return view;
    }
    private void startLocationUpdates() {
        mainController.onStart((MainActivity) getActivity());
    }

}