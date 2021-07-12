package com.example.ee_drivefinal.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.ee_drivefinal.Interface.CallBackFragment;
import com.example.ee_drivefinal.R;

import pl.droidsonroids.gif.GifImageView;

public class ConfirmationActivity extends AppCompatActivity implements CallBackFragment {

    //Variables
    private TextView textView;
    private GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        textView = findViewById(R.id.confirm_text_positive);
        gifImageView = findViewById(R.id.confirm_gifImageView);
        textView.setText("Drive Has been Successfully sent to our server!");
        if (!(getIntent().getExtras().getBoolean("Success"))) {
            gifImageView.setImageResource(R.drawable.falied);
            textView.setText("Failed to sent! File saved at /EE-Drive/Drives");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showActivity(MainActivity.class);
            }
        }, 4000);
    }

    @Override
    public void showActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();

    }
}