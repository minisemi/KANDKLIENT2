package com.example.adrian.klient.maps;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.adrian.klient.R;
import com.example.adrian.klient.internetcontrol.InternetController;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private Context context = this;
    private File markersFile = null;
    public ArrayList markerList;
    private static int EDIT_INFO_WINDOW = 1;
    public MapsActivity mapsActivity = this;
    private InternetController internetController = new InternetController(mapsActivity);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Creates a file in the primary internal storage space of the current application.
        // If the file does not exists, it is created.
        markersFile = new File(this.getFilesDir(), "MarkersFile.txt");
        markerList = new ArrayList<String>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                                       @Override
                                       public void onMapClick(LatLng arg0) {
                                           // Adds the marker to the map
                                           // TODO Auto-generated method stub
                                           Log.d("arg0", arg0.latitude + "-" + arg0.longitude);
                                           MarkerOptions markerOptions = new MarkerOptions().
                                                   position(new LatLng(arg0.latitude, arg0.longitude)).
                                                   title("Pågående ärende:").snippet("Klicka för att ange info");
                                           Marker marker = mMap.addMarker(markerOptions);
                                           LatLng position = marker.getPosition();
                                           String lat = String.valueOf(position.latitude);
                                           String lon = String.valueOf(position.longitude);
                                           marker.showInfoWindow();
                                           String event = marker.getSnippet();
                                           String markerInfo = lat + ";" + lon + ";" + event + ";:local";
                                           markerList.add(markerInfo);
                                           markerList.add(lat + ";" + lon + ";" + event + ";:add");
                                       }
                                   }

        );

        // Allows the user to edit a markers info by clicking on the info window.
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, InfoWindow.class);
                intent.putExtra("EXTRA_MARKER_OLD_TEXT", marker.getSnippet());
                intent.putExtra("EXTRA_MARKER_LON", String.valueOf(marker.getPosition().longitude));
                intent.putExtra("EXTRA_MARKER_LAT", String.valueOf(marker.getPosition().latitude));
                startActivityForResult(intent, EDIT_INFO_WINDOW);
            }
        });

        // Allows the user to push his/her marker-changes to the server, and get any new changes
        // to the markers from the server, by clicking on the sync button
        FloatingActionButton sync = (FloatingActionButton) findViewById(R.id.sync);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Checks if the client has an internet connection. If it does, it initiates a sync.
                if (internetController.InternetAvailable()) {
                    MapsAsyncTask mapSync = new MapsAsyncTask(context, mapsActivity, mMap, markerList);
                    mapSync.execute();
                    Snackbar.make(view, "Update successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "No connection available. Update not successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        // Loads the clients locally saved markers
        loadMarkers();
//        markerList.clear();
//        mMap.clear();
//        saveMarkers();
    }

    // If a markers is deleted, created or modified in "InfoWindow.java", this method will handle it
    // on return
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checks if the back button was pressed in InfoWIndow. If so, do nothing.
        // Else, delete or modify the marker depending on the resultCode
        if (requestCode == EDIT_INFO_WINDOW && !(resultCode == RESULT_CANCELED)) {
            Bundle extras = data.getExtras();
            String findLat = extras.getString("EXTRA_MARKER_LAT");
            String findLon = extras.getString("EXTRA_MARKER_LON");
            String findEvent = extras.getString("EXTRA_MARKER_OLD_TEXT");
            String newEvent = extras.getString("EXTRA_MARKER_NEW_TEXT");

            if (requestCode == EDIT_INFO_WINDOW && resultCode == RESULT_OK) {
                changeMarker(findLat, findLon, findEvent, newEvent);
            } else {
                deleteMarker(findLat, findLon, findEvent);
            }
        }
    }

    // Saves the markers locally on the client by writing their info to a text file
    public void saveMarkers(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(markersFile, false /*append*/));
            Iterator iterator = markerList.iterator();

            while (iterator.hasNext()) {
                String next = iterator.next().toString();
                writer.write(next);
                writer.newLine();
            }

            writer.close();

            // Refresh the data so it can seen when the device is plugged in a
            // computer. You may have to unplug and replug the device to see the
            // latest changes. This is not necessary if the user should not modify
            // the files.
            MediaScannerConnection.scanFile(this,
                    new String[]{markersFile.toString()}, null, null);
        }catch (IOException e) {
            Log.e("maps.", "Unable to write to the Markers.txt file.");
        }
    }

    // Modifies a markers info
    public void changeMarker (String findLat, String findLon, String findEvent, String newEvent){
        markerList.remove(findLat+";"+findLon+";"+findEvent+";:local");
        markerList.remove(findLat+";"+findLon+";"+ findEvent + ";:add");
        markerList.add(findLat + ";" + findLon + ";" + newEvent + ";:local");
        markerList.add(findLat+";"+findLon+";"+newEvent+";:add");
        saveMarkers();
        mMap.clear();
        loadMarkers();
    }

    // Deletes a marker
    public void deleteMarker (String findLat, String findLon, String findEvent) {
        markerList.remove(findLat + ";" + findLon + ";" + findEvent + ";:local");
        markerList.add(findLat+";" +findLon+";" +findEvent+";:delete");
        saveMarkers();
        mMap.clear();
        loadMarkers();
    }

    // Loads the markers from the locally saved text file and displays them on the phone
    public void loadMarkers () {
        InputStream isM = null;
        InputStreamReader isrM = null;
        BufferedReader brM = null;

        try{
            if (!markersFile.exists()) markersFile.createNewFile();
            isM = new FileInputStream(markersFile);
            isrM = new InputStreamReader(isM);
            brM = new BufferedReader(isrM);
            String thisLineM = null;
            markerList.clear();

            while ((thisLineM = brM.readLine()) != null) {
                markerList.add(thisLineM);
                String update[] = thisLineM.split(":");
                String parts[] = thisLineM.split(";");
                double lat = Double.valueOf(parts[0]);
                double lon = Double.valueOf(parts[1]);
                String event = parts[2];

                // Only markers with the update-tag "local" are supposed to be shown. All other marker-info
                // on the locally saved text file is info about local deletes or modification of markers, and is
                // only used when a sync is initialized to update the server.
                if (update[1].equals("local")) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Pågående ärende:").snippet(event));
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {

                // Releases resources associated with the streams
                if (isM != null)
                    isM.close();
                if (isrM != null)
                    isrM.close();
                if (brM != null)
                    brM.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Saves the clients markers locally if the app is closed or crashes
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveMarkers();
    }

    public void makeToast(long duration){
        Toast.makeText(context,"It took " + duration + "ms",Toast.LENGTH_LONG).show();
    }
}