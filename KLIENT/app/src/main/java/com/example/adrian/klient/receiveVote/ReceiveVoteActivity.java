package com.example.adrian.klient.receiveVote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.example.adrian.klient.vote.FileEncryption;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.Enumeration;

public class ReceiveVoteActivity extends AppCompatActivity {


    public ReceiveVoteActivity receiveVoteActivity = this;
    public TextView votes1;
    public TextView votes2;
    FileEncryption encryption;
    Thread serverThread;
    // DEFAULT IP
    public static String SERVERIP = "10.100.102.15";

    // DESIGNATE A PORT
    public static final int SERVERPORT = 8080;

    private Handler handler = new Handler();

    private ServerSocket serverSocket;
    FileUtils fileUtils;
    File in1;
    File in2;
    public File encrypted1;
    File encrypted1_1;
    File encrypted2;
    File encrypted2_1;
    public File decrypted1;
    File decrypted1_1;
    File rsaPublicKeyReceiver;
    File rsaPublicKeyMix;
    public File encryptedAesKeyReceiver;
    File rsaPrivateKeyMix;
    File encryptedAesKeyMix;
    public File rsaPrivateKeyReceiver;
    ReceiveAsyncTask updateIPAsync, clientServer;
    FileEncryption decryptReceiver;
    String vote;
    Server server;
    TextView infoip, msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button updateIP = (Button) findViewById(R.id.button);
        final Button server1     = (Button) findViewById(R.id.server);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);



        votes1 = (TextView) findViewById(R.id.textView1_1);
        votes2 = (TextView) findViewById(R.id.textView2_1);

        votes1.setText("0");
        votes2.setText("0");


        updateIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateIPAsync = new ReceiveAsyncTask(receiveVoteActivity, "update");
                //updateIPAsync.execute();
                Long h = System.currentTimeMillis();
                String k = Long.toString(h);
                msg.setText(k);


            }
        });

        server1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        server = new Server(receiveVoteActivity);
        infoip.setText(server.getIpAddress() + ":" + server.getPort());
               // clientServer = new ReceiveAsyncTask(receiveVoteActivity, "receive");
               // clientServer.execute();


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }
}
