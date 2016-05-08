package com.example.adrian.klient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adrian.klient.QoSManager.QoSManager;
import com.example.adrian.klient.ServerConnection.ServerActivity;
import com.example.adrian.klient.VidCom.AppRtcGo;
import com.example.adrian.klient.contactList.ContactList;
import com.example.adrian.klient.contactList.ContactListAsyncTask;
import com.example.adrian.klient.maps.MapsActivity;
import com.example.adrian.klient.receiveVote.ReceiveVoteActivity;
import com.example.adrian.klient.video.VideoCommunication;
import com.example.adrian.klient.vote.VoteActivity;

/**
 * Created by dennisdufback on 2016-03-17.
 */
public class MainMenuActivity extends Activity implements View.OnClickListener{

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        context = this;

        SharedPreferences userInfo = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        String name = userInfo.getString("USER_NAME", "Anon");

        TextView hello = (TextView) findViewById(R.id.Hello___);
        Button vidComButton = (Button) findViewById(R.id.vidComButton);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        Button serverButton = (Button) findViewById(R.id.serverButton);
        Button contactButton = (Button) findViewById(R.id.contactButton);
        Button appRtcBut = (Button) findViewById(R.id.appRtcBut);
        Button vote = (Button) findViewById(R.id.voteButton);
        Button receiveVote = (Button) findViewById(R.id.receiveVoteButton);
        final Button getLevelsButton = (Button) findViewById(R.id.getLevelsButton);

        hello.setText("Hello, " + name);
        vote.setOnClickListener(this);
        receiveVote.setOnClickListener(this);
        vidComButton.setOnClickListener(this);
        mapButton.setOnClickListener(this);
        serverButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        appRtcBut.setOnClickListener(this);
        getLevelsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                QoSManager qOSM = new QoSManager(context);
                int bL = qOSM.getBatteryLevel();
                int nL = qOSM.getConnectivityLevel();

                getLevelsButton.setText("bL: " + bL + "\n" + "nL: " + nL);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.voteButton:
                intent = new Intent(this, VoteActivity.class);
                startActivity(intent);
                break;

            case R.id.receiveVoteButton:
                intent = new Intent(this, ReceiveVoteActivity.class);
                startActivity(intent);
                break;

            case R.id.appRtcBut:
                intent = new Intent(this, AppRtcGo.class);
                startActivity(intent);
                break;

            case R.id.vidComButton:
                intent = new Intent(this, VideoCommunication.class);
                startActivity(intent);
                break;

            case R.id.mapButton:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;

            case R.id.serverButton:
                intent = new Intent(this, ServerActivity.class);
                startActivity(intent);
                break;

            case R.id.contactButton:
                intent = new Intent(this, ContactList.class);
                new ContactListAsyncTask(this, intent).execute();
                //startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
