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
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class RecordingScreenFragment extends Fragment {
    AppController mainController;
    DrivingController drivingController;
    DriveData driveData;

    TextView txtSpeed;
    TextView txtRecording;
    TextView txtConnected;
    public RecordingScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainController = new AppController((MainActivity) getActivity());
        startLocationUpdates();
driveData=DriveData.getInstance();
        drivingController = new DrivingController((MainActivity) getActivity());
        View view= inflater.inflate(R.layout.fragment_recording_screen, container, false);
        FloatingActionButton endBtn= view.findViewById(R.id.recording_stop_btn);
        txtSpeed = view.findViewById(R.id.recording_speed_dynmTxt);
        txtRecording=view.findViewById(R.id.recording_record_txt);
        txtConnected=view.findViewById(R.id.recording_connected_txt);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_recording_to_main);
            }

        });
        ImageButton obdBtn = view.findViewById(R.id.recording_obd_btn);
        obdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "btn");
                mainController.onObdConnect((MainActivity) getActivity(), getContext());
            }
        });
        driveData.getSpeed().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
            txtSpeed.setText(integer.toString());
            }
        });

       driveData.getRecordingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean==true){
                    txtRecording.setText("Recording");
                    txtConnected.setText("Connected");
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