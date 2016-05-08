package com.example.adrian.klient.receiveVote;

import android.content.Context;
import android.os.AsyncTask;

import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.example.adrian.klient.maps.MapsActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dotson on 2016-04-04.
 */
public class ReceiveAsyncTask extends AsyncTask<Void,Void,String> {

    com.example.adrian.klient.ServerConnection.Connection connection;
    private static String SERVERADRESS = "2016-4.itkand.ida.liu.se";
    private static int SERVERPORT = 9002;
    Context context;
    private GoogleMap mMap;
    private ArrayList markerList;
    public MapsActivity mapsActivity;
    private long startTime, duration;
    private JsonParser parser = new JsonParser();
    private JsonObject object;
    private JsonArray data;
    ReceiveVoteActivity receiveVoteActivity;
    String action;

    public ReceiveAsyncTask(ReceiveVoteActivity receiveVoteActivity, String action){
        this.receiveVoteActivity = receiveVoteActivity;
        this.action = action;
        this.context = context;
        this.mMap = mMap;
        this.markerList = markerList;
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected String doInBackground(Void... params) {

        String jsonString = "hej";
        Thread t;
        Iterator iterator = markerList.iterator();
        JsonObject receive = new JsonObject();
        receive.addProperty("","");

        switch (action) {

            case "update":
            // Iterates over the local markers
            startTime = System.currentTimeMillis();
            Request request = new Request(context, "receive").receiveRequest();
            connection = new Connection(request, context);
            t = new Thread(connection);
            t.start();
                break;

            /*case "receive":
                String serverName = SERVERADRESS;
                int port = SERVERPORT;
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(port);
                    System.out.println("Server started on port " + port);
                } catch (IOException e) {
                    System.err.println("Can't start listening on port " + port);
                    e.printStackTrace();
                    System.exit(-1);
                }
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Connection received from " +
                            socket.getInetAddress().getHostAddress()
                            + ":" + socket.getPort());
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.mSocket = socket;
                    Listener listener =
                            new Listener(clientInfo, receiveVoteActivity);
                    //ClientSender clientSender =
                    //      new ClientSender(clientInfo, serverDispatcher);
                    clientInfo.mClientListener = listener;
                    //clientInfo.mClientSender = clientSender;
                    listener.start();



            } catch (IOException e) {
                e.printStackTrace();
            }

            }
                break;*/

                    default:
                        break;
        /*do {
            jsonString = connection.getJson();
        } while (jsonString == null);
        */
        }
            //object = (JsonObject) parser.parse(jsonString);

        return jsonString;
    }

    public void updateVotes (String vote){

    }

    /*protected void onPostExecute (String result){

        // Toasts the time it took
        duration = System.currentTimeMillis() - startTime;
        mapsActivity.makeToast(duration);

        // When all communication between client and server is complete, update the locally saved
        // markers with the downloaded ones.
        object = (JsonObject) parser.parse(result);
        data = (JsonArray) object.get("data");
        markerList.clear();
        mMap.clear();

        for (JsonElement e : data) {
            JsonObject o = e.getAsJsonObject();
            double lat = o.get("lat").getAsDouble();
            double lon = o.get("lon").getAsDouble();
            String event = o.get("event").getAsString();
            markerList.add(String.valueOf(lat) + ";" + String.valueOf(lon) + ";" + event + ";:local");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Pågående ärende:").snippet(event));
        }

        mapsActivity.saveMarkers();
    }*/
}
