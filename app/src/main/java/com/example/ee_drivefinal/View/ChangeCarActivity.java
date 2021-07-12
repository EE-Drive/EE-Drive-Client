package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.Model.CarType;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.example.ee_drivefinal.ViewModel.ChangeCarViewModel;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;

public class ChangeCarActivity extends AppCompatActivity implements CallBackFragment {

    //Variables
    private Spinner carSpinner;
    private Button manualBtn;
    private Button backBtn;
    private Button listButton;
    private ChangeCarViewModel changeCarActivityViewModel;
    private SharedPrefHelper sharedPrefHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_car);
        try {
            initializeVariables();
        } catch (IOException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }
        initializeDynamicVariables();
    }

    private void initializeVariables() throws IOException {
        changeCarActivityViewModel = new ChangeCarViewModel();
        carSpinner = findViewById(R.id.change_car_spinner);
        manualBtn = findViewById(R.id.change_btn_manual);
        backBtn = findViewById(R.id.change_btn_back);
        sharedPrefHelper = SharedPrefHelper.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Change.LifeCycle.Method", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Change.LifeCycle.Method", "onResume");
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(GlobalContextApplication.getContext(),"Please Navigate Through Screen",Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Change.LifeCycle.Method", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Change.LifeCycle.Method", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        changeCarActivityViewModel.finish();
        Log.d("Change.LifeCycle.Method", "onDestroy");
    }

    private void initializeDynamicVariables() {
        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Manal Button", "Clicked");
                showActivity(AddCarActivity.class);

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back Button", "Clicked");
                changeCarActivityViewModel.finish();
                showActivity(MainActivity.class);
            }
        });
        carSpinner.setAdapter(new ArrayAdapter<String>(GlobalContextApplication.getContext(), R.layout.support_simple_spinner_dropdown_item, changeCarActivityViewModel.getAllCars()));
        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) parent.getChildAt(0)).setTextSize(15);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                String arr[] = parent.getItemAtPosition(position).toString().split(" ");
                String brand, model = "", year = " ", engineD;
                brand = arr[0];
                sharedPrefHelper.storeBrand(brand);
                if (arr.length > 1) {
                    model = arr[1];
                    sharedPrefHelper.storeModel(model);
                }
                if (arr.length > 2) {
                    year = arr[2];
                    sharedPrefHelper.storeYear(year);
                }
                engineD = "1300";
                if (arr.length > 3) {
                    engineD = arr[3];
                }
                try {
                    String request = changeCarActivityViewModel.addCarTypeToServerReceiveId(new CarType(brand, model, year, engineD).toJsonAddCarTypeToServer());
                    Toast.makeText(getApplicationContext(), "Car selected!!!", Toast.LENGTH_SHORT).show();
                } catch (JSONException | UnirestException e) {
                    FileHandler.appendLog(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();

    }

}