package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.Model.DriveHistory;
import com.example.ee_drivefinal.R;
import com.example.ee_drivefinal.Utils.FileHandler;
import com.example.ee_drivefinal.Utils.GlobalContextApplication;
import com.example.ee_drivefinal.Utils.SharedPrefHelper;
import com.example.ee_drivefinal.ViewModel.DriveHistoryViewModel;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveHistoryActivity extends AppCompatActivity implements CallBackFragment {

    //Variables
    private TableLayout tableLayout;
    private int PADDING = 10, TOP_PADDING = 10, FONT_SIZE = 3;
    private TableRow tableRow;
    private DriveHistoryViewModel driveHistoryViewModel;
    private TextView drivesNumber, countTxt;
    private Context context;
    private Button backBtn, tableBtn;
    private Thread thread;
    private ArrayList<DriveHistory> driveHistories;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_history);
        try {
            initializeVariables();
            initializeDynamicVariables();
        } catch (IOException | UnirestException | JSONException | ExecutionException | InterruptedException e) {
            FileHandler.appendLog(e.toString());
            e.printStackTrace();
        }
    }

    private void initializeDynamicVariables() throws JSONException, UnirestException, IOException, ExecutionException, InterruptedException {
        progressBar.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back Button", "Clicked");
                showActivity(MainActivity.class);

            }
        });
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Show Button", "Clicked");
                progressBar.setVisibility(View.VISIBLE);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    //Background work here
                    MyRunnable getHistory = new MyRunnable();
                    getHistory.run();
                    driveHistories = getHistory.getString();
                    handler.post(() -> {
                        //UI Thread work here
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            addHistoryTable();
                        } catch (JSONException | UnirestException | IOException | InterruptedException | ExecutionException e) {
                            FileHandler.appendLog(e.toString());
                            e.printStackTrace();
                        }
                    });
                });

                tableLayout.setVisibility(View.VISIBLE);
                tableBtn.setVisibility(View.INVISIBLE);

            }
        });

    }

    public void addHistoryTable() throws JSONException, UnirestException, IOException, ExecutionException, InterruptedException {
        tableLayout = findViewById(R.id.history_table);
        tableRow = new TableRow(context);
        //   countTxt.setText("Drives Number: " + driveHistories.size());
        //Table Headers
        TextView tv0 = new TextView(context);
        tv0.setText("Drive Id");
        tv0.setTextColor(Color.BLACK);
        tv0.setPadding(PADDING + 50, TOP_PADDING, PADDING, TOP_PADDING);
        tableRow.addView(tv0);
        TextView tv1 = new TextView(context);
        tv1.setText("Drive Time");
        tv1.setTextColor(Color.BLACK);
        tv1.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        tv1.setPadding(PADDING, TOP_PADDING, PADDING, TOP_PADDING);
        tableRow.addView(tv1);
        tableRow.setBackground(new ColorDrawable(Color.parseColor("#80485CCF")));
        tableLayout.addView(tableRow);
        //Table Body
        for (DriveHistory driveHistory : driveHistories
        ) {
            TableRow driveRow = new TableRow(context);
            TextView idText = new TextView(context);
            TextView timeText = new TextView(context);
            idText.setText(driveHistory.getDriveId());
            timeText.setText(driveHistory.getDriveTime());
            idText.setTextColor(Color.BLACK);
            timeText.setTextColor(Color.BLACK);
            idText.setPadding(PADDING + 50, TOP_PADDING, PADDING, TOP_PADDING);
            timeText.setPadding(PADDING, TOP_PADDING, PADDING, TOP_PADDING);
            driveRow.addView(idText);
            driveRow.addView(timeText);
            tableLayout.addView(driveRow);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }


    private void initializeVariables() throws IOException {
        driveHistoryViewModel = new DriveHistoryViewModel();
        tableLayout = findViewById(R.id.history_table);
        context = GlobalContextApplication.getContext();
        backBtn = findViewById(R.id.history_back_btn);
        countTxt = findViewById(R.id.history_number_text);
        tableLayout.setVisibility(View.GONE);
        tableBtn = findViewById(R.id.history_showtable_btn);
        progressBar = DriveHistoryActivity.this.findViewById(R.id.history_progress);

        countTxt.setText("Click To show drives for your " + SharedPrefHelper.getInstance().getBrand() + " " + SharedPrefHelper.getInstance().getModel());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("History.Life.Method", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("History.Life.Method", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("History.Life.Method", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("History.Life.Method", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  changeCarActivityViewModel.finish();
        driveHistoryViewModel.finish();
        if (thread != null)
            thread.interrupt();
        Log.d("ADD.LifeCycle.Method", "onDestroy");
    }


    @Override
    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }


    class MyRunnable implements Runnable {
        private ArrayList<DriveHistory> driveHistoryArrayList;

        @Override
        public void run() {
            try {
                driveHistoryArrayList = driveHistoryViewModel.getCarHistory();
            } catch (JSONException | UnirestException | IOException e) {
                FileHandler.appendLog(e.toString());
                e.printStackTrace();
            }
        }

        public ArrayList<DriveHistory> getString() {
            return driveHistoryArrayList;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(GlobalContextApplication.getContext(),"Please Navigate Through Screen",Toast.LENGTH_SHORT).show();

    }

}