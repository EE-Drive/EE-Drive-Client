package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.ViewModel.AddCarViewModel;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;


public class AddCarActivity extends AppCompatActivity implements CallBackFragment {

    //Variables
    private AddCarViewModel addCarViewModel;
    private Button confirmBtn;
    private Button backBtn;
    private EditText brand;
    private EditText model;
    private EditText year;
    private EditText engine;
    private TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        try {
            initializeVariables();
        } catch (IOException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }
        initializeDynamicVariables();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ADD.LifeCycle.Method", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ADD.LifeCycle.Method", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ADD.LifeCycle.Method", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ADD.LifeCycle.Method", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  changeCarActivityViewModel.finish();
        addCarViewModel.finish();
        Log.d("ADD.LifeCycle.Method", "onDestroy");
    }


    private void initializeDynamicVariables() {
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Confirm Button", "Clicked");
                String modelValue = model.getText().toString();
                String brandValue = brand.getText().toString();
                String yearValue = year.getText().toString();
                String engineValue = engine.getText().toString();
                AddCarViewModel.ERROR_INPUT input = addCarViewModel.checkValidation(brandValue, modelValue, yearValue, engineValue);
                switch (input) {
                    case VALID:
                        try {
                            addCarViewModel.addCar(brandValue, modelValue, yearValue, engineValue);
                            showActivity(MainActivity.class);
                        } catch (JSONException | UnirestException e) {
                            FileHandler.appendLog(e.toString());
                            e.printStackTrace();
                        }
                        break;
                    case SHORT:
                        error.setText("All fields must have more than 1 char");
                        error.setVisibility(View.VISIBLE);
                        break;
                    case MISS_FIELDS:
                        error.setText("Fill all fields");
                        error.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back Button", "Clicked");
                showActivity(ChangeCarActivity.class);
            }
        });
        error.setVisibility(View.INVISIBLE);
    }

    private void initializeVariables() throws IOException {
        addCarViewModel = new AddCarViewModel();
        confirmBtn = findViewById(R.id.add_confrim_btn);
        backBtn = findViewById(R.id.add_back_btn);
        brand = findViewById(R.id.add_editText_Brand);
        model = findViewById(R.id.add_editText_model2);
        year = findViewById(R.id.add_editText_year);
        engine = findViewById(R.id.add_editText_engine);
        error = findViewById(R.id.add_text_error);

    }

    @Override
    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(GlobalContextApplication.getContext(),"Please Navigate Through Screen",Toast.LENGTH_SHORT).show();

    }

}