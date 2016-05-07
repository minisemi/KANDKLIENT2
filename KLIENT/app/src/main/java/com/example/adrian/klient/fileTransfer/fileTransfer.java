package com.example.adrian.klient.fileTransfer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.view.View;

import com.example.adrian.klient.R;

public class fileTransfer extends AppCompatActivity {

    private int internetLevel;
    private int internetFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer);

        internetLevel = getInternetPercentage();
        internetFlag = getInternetFlag();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String internetAcceptance = "";

                switch (internetFlag) {
                    case 0:
                        internetAcceptance = "Internet connection is strong.";
                        break;
                    case 1:
                        internetAcceptance = "Internet connection is moderate.";
                    case 2:
                        internetAcceptance = "Internet connection is weak.";
                        break;
                    default:
                        internetAcceptance = "";
                }

                Snackbar.make(view, internetAcceptance + " Internet level: " + internetLevel + "", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    /*@Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        int obj  = signalStrength.getGsmSignalStrength();
        int dBm = (2 * obj) -113;
    }*/





    public int getInternetPercentage() {



      //  internetLevel = CellSignalStrengthGsm.SIGNAL_STRENGTH_GOOD;


        Intent internetIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_TIME_CHANGED));

     //   internetLevel = internetIntent.getIntExtra(telephony.SignalStrength., -1);
        return internetLevel;

    }

    public int getInternetFlag() {

        if (internetLevel >= 3 ){
            internetFlag = 2;
        } else if (internetLevel < 3 && internetLevel>= 2){
            internetFlag = 1;
        } else internetFlag = 0;

        return internetFlag;
    }

}
