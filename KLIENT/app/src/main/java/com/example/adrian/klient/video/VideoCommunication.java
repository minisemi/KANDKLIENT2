package com.example.adrian.klient.video;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.adrian.klient.R;

public class VideoCommunication extends AppCompatActivity {

    private int batteryPercentage;
    private int batteryFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_communication);

        batteryPercentage = getBatteryPercentage();
        batteryFlag = getBatteryFlag();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String videoAcceptance = "";

                switch (batteryFlag) {
                    case 0: videoAcceptance = "Video communication not accepted.";
                        break;
                    case 1: case 2: videoAcceptance = "Video communication accepted.";
                        break;
                    default: videoAcceptance = "";
                }

                Snackbar.make(view, videoAcceptance + " Battery level: " + batteryPercentage + "%", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public int getBatteryPercentage() {
        //Skapar en intention (intent), dvs vad vi vill göra, genom att registrera (registerReceiver) att intentionen ska vara en mottagare
        // från ACTION_BATTERY_CHANGED.
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        //Hämtar extended data (getIntExtra) från intentntionen genom att använda BatteryManager som hjälper en att tolka dess värden.
        //EXTRA_LEVEL hämtar den nuvarande batterinivån.
        batteryPercentage = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        return batteryPercentage;

        /*int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }
        return ((float)level / (float)scale) * 100.0f;*/
    }

    public int getBatteryFlag() {

        if (batteryPercentage >= 40 ){
            batteryFlag = 2;
        } else if (batteryPercentage < 40 && batteryPercentage>= 25){
            batteryFlag = 1;
        } else
            batteryFlag = 0;

        return batteryFlag;
    }
}
