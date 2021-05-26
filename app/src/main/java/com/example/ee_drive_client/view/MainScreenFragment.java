package com.example.ee_drive_client.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.room.RoomDatabase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drive_client.Activities.MainActivity;
import com.example.ee_drive_client.R;
import com.example.ee_drive_client.controller.AppController;
import com.example.ee_drive_client.controller.DrivingController;
import com.example.ee_drive_client.controller.SendToServer;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.model.DriveHistory;
import com.example.ee_drive_client.model.OBDData;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainScreenFragment extends Fragment {
    AppController mainController;
    DrivingController drivingController;
    UUID id = UUID.randomUUID();
    String ID = id.toString();
    ArrayList<CarType> response = new ArrayList<>();
    private TextView isConnected, textViewDebug;
    private RepositoryCar repo;
    SendToServer sendToServer = new SendToServer();
    Thread serverThread;
    String year;
    String brand;
    String model;
    String engineD;
    DriveHistory driveHistory = DriveHistory.getInstance();

    public MainScreenFragment() throws IOException, JSONException, UnirestException {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        try {
            repo = new RepositoryCar(getContext());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            drivingController = new DrivingController((MainActivity) getActivity());
            mainController = new AppController((MainActivity) getActivity());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
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

        Button carDetailsBtn = view.findViewById(R.id.main_carsDetails_btn);
        carDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_MainScreenFragment_to_carDetailsFragment);
            }
        });

        Spinner mainSpinner = (Spinner) view.findViewById(R.id.main_spinner);

        mainSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getAllCarsFromServer()));
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String arr[] = parent.getItemAtPosition(position).toString().split(" ");
                brand = arr[0];
                SharedPrefHelper.getInstance(getContext()).storeBrand(brand);

                if (arr.length > 1) {
                    model = arr[1];
                    SharedPrefHelper.getInstance(getContext()).storeModel(model);
                }
                if (arr.length > 2) {
                    year = arr[2];
                    SharedPrefHelper.getInstance(getContext()).storeYear(year);
                }
                engineD = "1300";
                if (arr.length > 3) {
                    engineD = arr[3];
                }
                Toast.makeText(GlobalContextApplication.getContext(), "Car selected :" + brand, Toast.LENGTH_LONG).show();

                serverThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String request = sendToServer.addCarTypeToServerReceiveId(new CarType(brand, model, year, engineD).toJsonAddCarTypeToServer());
                            driveHistory.updateDriveHistory();
                            driveHistory.setDriveHistories(driveHistory.getDriveHistories());
                        } catch (UnirestException e) {
                            //repo.insertCarDbOnly(new CarType(brand,model,year,engineD));
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                serverThread.start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private ArrayList<String> getAllCarsFromServer() {


        ArrayList<String> arrayListSpinner = new ArrayList<String>();
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray jsonArray = new JSONArray();
                    response = sendToServer.getAllCarTypesFromServer();
                    Log.d("Car", sendToServer.getAllCarTypesFromServer().toString());
                    for (CarType car : response
                    ) {
                        repo.insertCarDbOnly(car);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (UnirestException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        ArrayList<CarType> carArrayList = (ArrayList<CarType>) repo.getCars();
        for (int i = 0; i < carArrayList.size(); i++) {
            arrayListSpinner.add(carArrayList.get(i).loadFullModelForShow());
        }
        return arrayListSpinner;
    }


}