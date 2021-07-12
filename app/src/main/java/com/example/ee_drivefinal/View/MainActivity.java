package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.example.ee_drivefinal.ViewModel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity implements CallBackFragment {

    //Variables
    private MainActivityViewModel viewModel;
    private TextView carModel;
    private TextView carBrand;
    private Button exitButton;
    private TextView carYear;
    private SharedPrefHelper sharedPrefHelper;
    private Button startBtn;
    private Button historyBtn;
    private Button changeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
        initializeVariables();
        initializeDynamicVariables();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Main.LifeCycle.Method", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Main.LifeCycle.Method", "onResume");
        carYear.setText(sharedPrefHelper.getYear());
        carModel.setText(sharedPrefHelper.getModel());
        carBrand.setText(sharedPrefHelper.getBrand());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Main.LifeCycle.Method", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Main.LifeCycle.Method", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Main.LifeCycle.Method", "onDestroy");
    }

    private void initializeVariables() {
        sharedPrefHelper = SharedPrefHelper.getInstance();
        viewModel = new MainActivityViewModel();
        carBrand = findViewById(R.id.main_bar_brand);
        carModel = findViewById(R.id.main_bar_model);
        carYear = findViewById(R.id.main_bar_year);
        startBtn = findViewById(R.id.main_start_btn);
        changeBtn = findViewById(R.id.main_change_btn);
        historyBtn = findViewById(R.id.main_history_btn);
        exitButton = findViewById(R.id.main_exit_btn);
    }

    private void initializeDynamicVariables() {
        carBrand.setText(sharedPrefHelper.getBrand());
        carModel.setText(sharedPrefHelper.getModel());
        carYear.setText(sharedPrefHelper.getYear());
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Start Button", "Clicked");
                showActivity(DrivingActivity.class);
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Change Button", "Clicked");
                showActivity(ChangeCarActivity.class);
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("History Button", "Clicked");
                showActivity(DriveHistoryActivity.class);
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN},
                101);
    }


}