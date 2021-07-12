package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.Model.DriveData;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Repositories.ServerHandler;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.ViewModel.DrivingViewModel;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrivingActivity extends AppCompatActivity implements CallBackFragment {


    //Variables
    private Button obdBtn, stopBtn, startBtn, fullSModelScreenBtn, backBtn;
    private TextView instructText, speedText, fuelText, obdText, statusText, mainText;
    private DrivingViewModel drivingViewModel;
    private ProgressBar progressBar;
    private ConstraintLayout assistBar;
    private CheckBox checkBox;
    private AppCompatActivity thisActivity;
    private AlertDialog backDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        thisActivity = this;
        try {
            initialVariables();
            initialDynamicVariables();
        } catch (IOException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }


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
        drivingViewModel.finish();
        Log.d("ADD.LifeCycle.Method", "onDestroy");
    }

    private void initialDynamicVariables() {
        //TODO:VISIBLE
     //   checkBox.setVisibility(View.INVISIBLE);
        drivingViewModel.startListeners();
        assistBar.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.INVISIBLE);
        instructText.setText("No Model Found Yet");
        fullSModelScreenBtn.setVisibility(View.INVISIBLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("Checked", "Use Model");
                } else {
                    Log.d("Checked", "Dont Use Model");
                }
                drivingViewModel.updateAssist(isChecked);
            }
        });
        statusText.setText("Not Connected,No Model");
        obdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Start Button", "Clicked");
                drivingViewModel.chooseDevice();
                updateStatus("Not Connected,No Model");

            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Stop Button", "Clicked");
                try {
                    drivingViewModel.endDrive();
                    speedText.setText("No data");
                    mainText.setText("Stopping");
                    fuelText.setText("No data");
                    //TODO: put extra to intent + if failed - put extra

                } catch (IOException exception) {
                    FileHandler.appendLog(exception.toString());
                    exception.printStackTrace();
                }
            }
        });
        stopBtn.setEnabled(false);
        stopBtn.setTextColor(Color.GRAY);
        stopBtn.setBackgroundColor(Color.LTGRAY);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Start Button", "Clicked");
                try {
                    drivingViewModel.startDriveRecord();
                    mainText.setText("Drive Safe ");
                    stopBtn.setEnabled(true);
                    stopBtn.setTextColor(Color.WHITE);
                    stopBtn.setBackgroundColor(Color.RED);
                    startBtn.setEnabled(false);
                    startBtn.setTextColor(Color.GRAY);
                    startBtn.setBackgroundColor(Color.LTGRAY);
                    backBtn.setEnabled(false);
                    backBtn.setTextColor(Color.GRAY);
                    backBtn.setBackgroundColor(Color.LTGRAY);

                } catch (JSONException | UnirestException e) {
                    FileHandler.appendLog(e.toString());
                    e.printStackTrace();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(MainActivity.class);
                drivingViewModel.stopService();
                finish();
            }
        });
        fullSModelScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Full Screen Button", "Clicked");
            }
        });
    }

    public void showConfirmationActivity(Class<ConfirmationActivity> confirmationActivityClass) {
        Intent intent = new Intent(this, confirmationActivityClass);
        if (DriveData.getInstance().getSentToServer()) {
            intent.putExtra("Success", true);
        } else {
            intent.putExtra("Success", false);
        }
        startActivity(intent);
        //TODO: Put extra for drive brief summary
        finish();
    }

    public void showFailedConfirmationActivity(Class<ConfirmationActivity> confirmationActivityClass) {
        Intent intent = new Intent(this, confirmationActivityClass);
        intent.putExtra("Failed", true);
        startActivity(intent);
        //TODO: Put extra for drive brief summary
        finish();
    }


    private void initialVariables() throws IOException {
        drivingViewModel = new DrivingViewModel(this);
        obdBtn = findViewById(R.id.driving_connect_btn);
        mainText = findViewById(R.id.driving_obd_Text);
        stopBtn = findViewById(R.id.driving_stop_btn);
        stopBtn.setVisibility(View.INVISIBLE);
        obdText = findViewById(R.id.driving_obd_Text);
        speedText = findViewById(R.id.driving_speed_dynamic);
        fuelText = findViewById(R.id.driving_fuel_dynamic);
        instructText = findViewById(R.id.driving_bar_instructions);
        statusText = findViewById(R.id.driving_status_text);
        progressBar = findViewById(R.id.driving_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        checkBox = findViewById(R.id.driving_checkBox);
        assistBar = findViewById(R.id.driving_barLayout);
        startBtn = findViewById(R.id.driving_start_btn);
        fullSModelScreenBtn = findViewById(R.id.driving_model_btn);
        backBtn = findViewById(R.id.driving_back_btn);
    }

    @Override
    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        //TODO: Put extra for drive brief summary
        finish();
    }

    public void updateStatus(String s) {
        statusText.setText(s);
    }

    public void showSpinner() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideSpinner() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void updateBar(boolean isChecked) {
        if (isChecked) {
            assistBar.setVisibility(View.VISIBLE);
        } else {
            assistBar.setVisibility(View.INVISIBLE);
        }
    }

    public void updateScreen(double getmFuel, double getmSpeed) {
        speedText.setText(Double.toString(getmSpeed));
        fuelText.setText(Double.toString(getmFuel));
    }

    public void updateButtons(boolean b) {
        if (b) {
            startBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.VISIBLE);
            obdBtn.setVisibility(View.INVISIBLE);
            mainText.setText("Connected");

        } else {
            startBtn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.INVISIBLE);
            obdBtn.setVisibility(View.VISIBLE);
            mainText.setText("Please Connect To Obd");

        }
    }

    public void showProgressBar(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            obdBtn.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void showProgressBarEnd(boolean b) {
        if (b) {
            progressBar.setVisibility(View.VISIBLE);
            obdBtn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.INVISIBLE);
            startBtn.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void setStatus(String s) {
        statusText.setText(s);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(GlobalContextApplication.getContext(),"Please Navigate Through Screen",Toast.LENGTH_SHORT).show();

    }


}