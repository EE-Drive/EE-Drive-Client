package com.example.ee_drive_client.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ee_drive_client.R;
import com.example.ee_drive_client.model.CarType;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;


public class AddNewCarFragment extends Fragment {


    private RepositoryCar repositoryCar;
    Thread serverThread;
    public AddNewCarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            repositoryCar = new RepositoryCar(getContext());
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        EditText editTextYear = view.findViewById(R.id.add_year_EditTxt);
        EditText editTextModel = view.findViewById(R.id.add_model_editTxt);
        EditText editTextBrand = view.findViewById(R.id.add_brand_editTxt);
        EditText editTextEngine=view.findViewById(R.id.add_engine_editTxt);
        Button addBtn = view.findViewById(R.id.add_add_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model = editTextModel.getText().toString();
                String brand = editTextBrand.getText().toString();
                String year = editTextYear.getText().toString();
                String engine=editTextEngine.getText().toString();
                CarType car =new CarType(brand,model,year,engine);

                    serverThread=new Thread(new Runnable() {
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
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_new_car, container, false);
    }
}