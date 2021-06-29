package com.example.ee_drive_client.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ee_drive_client.R;
import com.example.ee_drive_client.model.DriveAssistant;
import com.example.ee_drive_client.model.DriveData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import androidx.lifecycle.Observer;


public class AssistFragment extends Fragment {

    //Variables
    private DriveData driveData;
    private DriveAssistant driveAssistant;
    private FloatingActionButton endBtn;
    private TextView speedText;
    private ImageView speedImage;

    public AssistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assist, container, false);
        initializeVariables(view);
        initializeEventListeners();
        updateRecommendedSpeed();
        return view;
    }


    private void initializeVariables(View view) {
        driveAssistant = DriveAssistant.getInstance();
        driveData = DriveData.getInstance();
        endBtn = view.findViewById(R.id.assist_btn_stop);
        speedText = view.findViewById(R.id.assist_txt_speed);
        speedImage = view.findViewById(R.id.assist_image_speed);
    }

    private void initializeEventListeners() {
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_assist_to_recording);

            }
        });

        DriveAssistant.getInstance().increaseSpeed(0).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                speedText.setText(s);
            }
        });
        DriveAssistant.getInstance().changeSpeedType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s=="Higher"){
                    speedImage.setImageResource(R.drawable.speed_up_foreground);
                }else if(s=="Lower"){
                    speedImage.setImageResource(R.drawable.speed_down_foreground);
                }else if(s=="Stay"){
                    speedImage.setImageResource(R.drawable.speed_stay_foreground);
                }else{
                    Navigation.findNavController(getView()).navigate(R.id.action_assist_to_recording);

                }
            }
        });
    }

    private void updateRecommendedSpeed() {


    }
}