package com.example.adrian.klient.receiveVote;

import android.content.Context;
import android.os.AsyncTask;

import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.example.adrian.klient.maps.MapsActivity;
import com.example.adrian.klient.vote.FileEncryption;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dotson on 2016-04-04.
 */
public class ReceiveAsyncTask extends AsyncTask<Void,Void,String> {
    private WeakReference<ReceiveVoteActivity> mParentActivity = null;

    com.example.adrian.klient.ServerConnection.Connection connection;
    private static String SERVERADRESS = "2016-4.itkand.ida.liu.se";
    private static int SERVERPORT = 9002;
    private long startTime, duration;
    private JsonParser parser = new JsonParser();
    private JsonObject object;
    private JsonArray data;
    ReceiveVoteActivity receiveVoteActivity;
    String action, votes;
    FileEncryption decryptReceiver;
    int PORT = 8080;
    int i = 0;
    //ServerSocket serverSocket;
    static Boolean run;

    public ReceiveAsyncTask(ReceiveVoteActivity receiveVoteActivity){
        this.receiveVoteActivity = receiveVoteActivity;
        mParentActivity = new WeakReference<ReceiveVoteActivity>(receiveVoteActivity);
    }

    @Override
    protected String doInBackground(Void... params) {
       //android.os.Debug.waitForDebugger();

        Thread t;



            // Iterates over the local markers
            //startTime = System.currentTimeMillis();
            Request request = new Request(receiveVoteActivity, "receive").receiveRequest();
            connection = new Connection(request, receiveVoteActivity, 9010);
            t = new Thread(connection);
            t.start();


        return "";
    }
}
