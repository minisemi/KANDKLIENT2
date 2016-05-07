package com.example.adrian.klient.VidCom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.adrian.klient.QoSManager.QoSManager;
import com.example.adrian.klient.R;

public class AppRtcGo extends AppCompatActivity {

    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_rtc_go);

        wv = new WebView(this);
        setContentView(wv);
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        QoSManager qoSManager = new QoSManager(this);

        int batteryLevel = qoSManager.getBatteryLevel();
        int networkLevel = qoSManager.getConnectivityLevel();
        String roomName = getIntent().getExtras().getString("ROOM");

        String hd = "";
        String br = "";
        if ( (batteryLevel > 1) && (networkLevel > 1) ) {
            hd = "hd=true";
            br = "vrbr=40&vsbr=40";
        } else {
            hd = "hd=false";
            br = "vrbr=720&vsbr=720";
        }

        wv.loadUrl("https://appr.tc/r/" + roomName + "?" + hd + "&" + br);
    }

    @Override
    public void onBackPressed() {
        wv.loadUrl("a");
        wv.destroy();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        wv.loadUrl("a");
        wv.destroy();
        super.onDestroy();
    }
}