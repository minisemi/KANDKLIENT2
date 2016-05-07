package com.example.adrian.klient.QoSManager;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

/**
 * Created by Adrian on 4/26/2016.
 */
public class QoSManager {

    Context context;
    ConnectivityManager connectivityManager;

    public QoSManager (Context _context) {
        this.context = _context;
        connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
    }

    public int getConnectivityLevel () {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
            return 1;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return 2;
        }

        return 0;
    }

    public int getBatteryLevel () {
        int batteryPercentage;

        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryPercentage = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        /*int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }
        return ((float)level / (float)scale) * 100.0f;*/

        if (batteryPercentage >= 50 ){
            return 2;
        } else if (batteryPercentage < 50 && batteryPercentage>= 30){
            return 1;
        }

        return 0;
    }
}