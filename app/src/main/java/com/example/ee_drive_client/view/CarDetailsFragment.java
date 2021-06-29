package com.example.ee_drive_client.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.ee_drive_client.R;
import com.example.ee_drive_client.model.DriveHistory;
import com.example.ee_drive_client.repositories.GlobalContextApplication;
import com.example.ee_drive_client.repositories.RepositoryCar;
import com.example.ee_drive_client.repositories.SharedPrefHelper;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


public class CarDetailsFragment extends Fragment {

    //Variables
    private TextView modelTxt, brandTxt, yearTxt, engineTxt, countTxt;
    private SharedPrefHelper sharedPrefHelper;
    private RepositoryCar repositoryCar;
    private ArrayList<DriveHistory> driveHistories;
    private TableLayout tableLayout;
    private int PADDING = 10, TOP_PADDING = 10, FONT_SIZE = 3;
    private Thread thread;
    private TableRow tableRow;

    public CarDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_car_details, container, false);
        initializeVariables(view);
        initializeData(view);
        return view;
    }


    private void initializeVariables(View view) {
        sharedPrefHelper = SharedPrefHelper.getInstance(GlobalContextApplication.getContext());
        driveHistories = new ArrayList<>();
        modelTxt = view.findViewById(R.id.details_txt_model);
        brandTxt = view.findViewById(R.id.details_txt_brand);
        yearTxt = view.findViewById(R.id.details_txt_year);
        engineTxt = view.findViewById(R.id.details_txt_engine);
        countTxt = view.findViewById(R.id.details_txt_count);
        modelTxt.setText("Model: " + sharedPrefHelper.getModel());
        brandTxt.setText("Brand: " + sharedPrefHelper.getBrand());
        yearTxt.setText("Year: " + sharedPrefHelper.getYear());
        engineTxt.setText("Engine: " + sharedPrefHelper.getEngine());
        try {
            repositoryCar = new RepositoryCar(getContext());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initializeData(View view) {
        try {
            addHistoryTable(view);
        } catch (UnirestException | IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public void addHistoryTable(View view) throws JSONException, UnirestException, IOException {
        tableLayout = view.findViewById(R.id.details_table);
        tableRow = new TableRow(getContext());
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    driveHistories = repositoryCar.getCarHistory();
                } catch (UnirestException | JSONException e) {
                    e.printStackTrace();
                }
                Log.d("response", driveHistories.toString());
            }
        });
        thread.start();

        //Table Headers
        TextView tv0 = new TextView(getContext());
        tv0.setText("Drive Id");
        tv0.setTextColor(Color.BLACK);
        tv0.setPadding(PADDING + 50, TOP_PADDING, PADDING, TOP_PADDING);
        tableRow.addView(tv0);
        TextView tv1 = new TextView(getContext());
        tv1.setText("Drive Time");
        tv1.setTextColor(Color.BLACK);
        tv1.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        tv1.setPadding(PADDING, TOP_PADDING, PADDING, TOP_PADDING);
        tableRow.addView(tv1);
        tableRow.setBackground(new ColorDrawable(Color.parseColor("#80485CCF")));
        tableLayout.addView(tableRow);
        countTxt.setText("Drives Number: " + DriveHistory.getInstance().getDriveHistories().size());
        //Table Body
        for (DriveHistory driveHistory : DriveHistory.getInstance().getDriveHistories()
        ) {
            TableRow driveRow = new TableRow(getContext());
            TextView idText = new TextView(getContext());
            TextView timeText = new TextView(getContext());
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

    }
}