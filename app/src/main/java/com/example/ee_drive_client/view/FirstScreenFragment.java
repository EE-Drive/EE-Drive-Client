package com.example.ee_drive_client.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ee_drive_client.R;

public class FirstScreenFragment extends Fragment {

    //Variables
    private Button registerBtn;

    public FirstScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_screen, container, false);
        initializeVariables(view);
        initializeEventListeners(view);
        return view;
    }

    private void initializeVariables(View view) {
        registerBtn = view.findViewById(R.id.first_register_btn);
    }

    private void initializeEventListeners(View view) {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.actiom_first_to_add);
            }
        });
    }
}