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

    private TextView modelTxt;
    private TextView brandTxt;
    private TextView yearTxt;
    private TextView engineTxt;
    private TextView countTxt;
    SharedPrefHelper sharedPrefHelper;
    private RepositoryCar repositoryCar;
    ArrayList<DriveHistory> driveHistories;



    public CarDetailsFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            repositoryCar = new RepositoryCar(getContext());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        View view = inflater.inflate(R.layout.fragment_car_details, container, false);
        sharedPrefHelper = SharedPrefHelper.getInstance(GlobalContextApplication.getContext());
        driveHistories = new ArrayList<>();
        modelTxt = view.findViewById(R.id.details_txt_model);
        brandTxt = view.findViewById(R.id.details_txt_brand);
        yearTxt = view.findViewById(R.id.details_txt_year);
        engineTxt = view.findViewById(R.id.details_txt_engine);
        countTxt=view.findViewById(R.id.details_txt_count);
        modelTxt.setText("Model: " + sharedPrefHelper.getModel());
        brandTxt.setText("Brand: " + sharedPrefHelper.getBrand());
        yearTxt.setText("Year: " + sharedPrefHelper.getYear());
        engineTxt.setText("Engine: " + sharedPrefHelper.getEngine());

        try {
            addHistoryTable(view);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return view;
    }


    public void addHistoryTable(View view) throws JSONException, UnirestException, IOException {
        TableLayout tableLayout = view.findViewById(R.id.details_table);
        int PADDING = 10, TOP_PADDING = 10, FONT_SIZE = 3;
        TableRow tableRow = new TableRow(getContext());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    driveHistories = repositoryCar.getCarHistory();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnirestException e) {
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
//        TextView tv2 = new TextView(getContext());
//        tv2.setText("Drive Assist");
//        tv2.setTextColor(Color.BLACK);
//        tv2.setPadding(PADDING, TOP_PADDING, PADDING, TOP_PADDING);
//        tableRow.addView(tv2);
        tableRow.setBackground(new ColorDrawable(Color.parseColor("#80485CCF")));
        tableLayout.addView(tableRow);
        countTxt.setText("Drives Number: " + DriveHistory.getInstance().getDriveHistories().size());
        //Table Body
        for (DriveHistory driveHistory : DriveHistory.getInstance().getDriveHistories()
        ) {
            TableRow driveRow = new TableRow(getContext());
            TextView idText = new TextView(getContext());
            TextView timeText = new TextView(getContext());
        //    TextView assistText = new TextView(getContext());
            idText.setText(driveHistory.getDriveId());
            timeText.setText(driveHistory.getDriveTime());
         //   assistText.setText(driveHistory.getDriveAssist());
            idText.setTextColor(Color.BLACK);
            timeText.setTextColor(Color.BLACK);
          //  assistText.setTextColor(Color.BLACK);
            idText.setPadding(PADDING + 50, TOP_PADDING, PADDING, TOP_PADDING);
            timeText.setPadding(PADDING, TOP_PADDING, PADDING, TOP_PADDING);
          //  assistText.setPadding(PADDING, TOP_PADDING, PADDING, TOP_PADDING);
            driveRow.addView(idText);
            driveRow.addView(timeText);
        //    driveRow.addView(assistText);
            tableLayout.addView(driveRow);


        }


    }
}