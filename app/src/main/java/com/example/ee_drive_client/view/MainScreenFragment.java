package com.example.ee_drive_client.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.R;
import com.example.ee_drive_client.controller.AppController;
import com.example.ee_drive_client.controller.DrivingController;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.repositories.RepositoryCar;

import java.util.ArrayList;
import java.util.UUID;

public class MainScreenFragment extends Fragment {
    AppController mainController;
    DrivingController drivingController;
    UUID id = UUID.randomUUID();
    String ID = id.toString();
    private TextView isConnected, textViewDebug;
    private RepositoryCar repo;

    public MainScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        repo = new RepositoryCar(getContext());
        mainController = new AppController((MainActivity) getActivity());
        drivingController = new DrivingController((MainActivity) getActivity());

        Button startBtn = view.findViewById(R.id.main_start_btn);
        textViewDebug = view.findViewById(R.id.textViewDebug);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Send parameters :action= MainScreenFragmentDirections.actionMainToRecording(String exmaple);
                Navigation.findNavController(view).navigate(R.id.action_main_to_recording);

            }
        });
        Button endBtn = view.findViewById(R.id.main_EndDrive_btn);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_main_to_first);

            }
        });



        Spinner mainSpinner = (Spinner) view.findViewById(R.id.main_spinner);
        mainSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getAllCarsFromDB()));

        return view;
    }


    private ArrayList<String> getAllCarsFromDB() {

        ArrayList<CarType> carArrayList = (ArrayList<CarType>) repo.getCars();
        ArrayList<String> arrayListSpinner = new ArrayList<String>();


        for (int i = 0; i < carArrayList.size(); i++) {
            arrayListSpinner.add(carArrayList.get(i).loadFullModelForShow());
        }

        return arrayListSpinner;
    }


}