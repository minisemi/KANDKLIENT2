package com.example.adrian.klient.maps;

import android.content.Context;
import android.os.AsyncTask;

import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.MapRequest;
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
public class MapsAsyncTask extends AsyncTask<Void,Void,String> {

    com.example.adrian.klient.ServerConnection.Connection connection;
    Context context;
    private GoogleMap mMap;
    private ArrayList markerList;
    private String update;
    public MapsActivity mapsActivity;
    private long startTime, duration;

    public MapsAsyncTask(Context context, MapsActivity mapsActivity, GoogleMap mMap, ArrayList markerList){
        this.context = context;
        this.mMap = mMap;
        this.markerList = markerList;
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected String doInBackground(Void... params) {

        String jsonString;
        Request updateMap;
        Thread t;
        Iterator iterator = markerList.iterator();
        startTime = System.currentTimeMillis();

        // Iterates over the local markers
        while (iterator.hasNext()) {
            String next = iterator.next().toString();
            String update[] = next.split(":");

            // If a marker has an "add" or "delete" tag (i.e. not "local"), then update the server with that info
            if (!update[1].equals("local")) {
                String parts[] = next.split(";");
                String lat = parts[0];
                String lon = parts[1];

                switch (update[1]) {

                    case "delete":
                        // Send delete request to server
                        updateMap = new MapRequest(context, update[1], lat, lon);
                        connection = new com.example.adrian.klient.ServerConnection.Connection(updateMap, context);
                        t = new Thread(connection);
                        t.start();
                        break;

                    case "add":
                        String event = parts[2];
                        if (!event.isEmpty()) {
                            // Send add request to server
                            updateMap = new MapRequest(context, update[1], lat, lon, event);
                            connection = new com.example.adrian.klient.ServerConnection.Connection(updateMap, context);
                            t = new Thread(connection);
                            t.start();
                        }
                        break;

                    default:
                        break;
                }
            }

        }

        updateMap = new MapRequest(context, "get");
        connection = new Connection(updateMap, context);

        // Wait a second to allow the server to handle eventual adds or deletes.
        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        // Get all updated marker info from server
        t = new Thread(connection);
        t.start();
        do {
            jsonString = connection.getJson();
        } while (jsonString == null);


        return jsonString;
    }

    protected void onPostExecute (String result){

        duration = System.currentTimeMillis() - startTime;
        mapsActivity.makeToast(duration);

        // When all communication between client and server is complete, update the locally saved
        // markers with the downloaded ones.
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(result);
        JsonArray data = (JsonArray) jo.get("data");
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
//        long elapsedTime = System.currentTimeMillis() - startTime;
//        Toast.makeText(this.context, "Total time to get response from server: " + elapsedTime, Toast.LENGTH_LONG).show();
    }
}
