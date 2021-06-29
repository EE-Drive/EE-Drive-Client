package com.example.ee_drive_client.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drive_client.R;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;


public class AddNewCarFragment extends Fragment {

    //Variables
    private RepositoryCar repositoryCar;
    private Thread serverThread;
    private EditText editTextYear, editTextModel, editTextBrand, editTextEngine;
    private TextView errorTxt;
    private Button addBtn;
    private String model, brand, year, engine;
    private RepositoryCar.ERROR_INPUT input;

    //Constructors
    public AddNewCarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeVariables(view);
        initializeEventListeners();

    }


    private void initializeVariables(View view) {
        try {
            repositoryCar = new RepositoryCar(getContext());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        editTextYear = view.findViewById(R.id.add_year_EditTxt);
        editTextModel = view.findViewById(R.id.add_model_editTxt);
        editTextBrand = view.findViewById(R.id.add_brand_editTxt);
        editTextEngine = view.findViewById(R.id.add_engine_editTxt);
        errorTxt = view.findViewById(R.id.add_error_txt);
        errorTxt.setVisibility(View.INVISIBLE);
        addBtn = view.findViewById(R.id.add_add_btn);


    }

    private void initializeEventListeners() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorTxt.setVisibility(View.INVISIBLE);
                model = editTextModel.getText().toString();
                brand = editTextBrand.getText().toString();
                year = editTextYear.getText().toString();
                engine = editTextEngine.getText().toString();
                input = repositoryCar.isValid(model, brand, year, engine);
                switch (input) {
                    case VALID:
                        CarType car = new CarType(brand, model, year, engine);
                        serverThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    repositoryCar.insertCar(car);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnirestException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        serverThread.start();
                        Toast.makeText(getContext(), "Added car", Toast.LENGTH_SHORT).show();
                        break;
                    case MISS_FIELDS:
                        errorTxt.setText("Fill all fields");
                        errorTxt.setVisibility(View.VISIBLE);
                        break;
                    case SHORT:
                        errorTxt.setText("All fields must have more than 1 char");
                        errorTxt.setVisibility(View.VISIBLE);
                        break;
                }
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_new_car, container, false);
    }
}