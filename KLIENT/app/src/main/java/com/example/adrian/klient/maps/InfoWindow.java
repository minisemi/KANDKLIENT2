package com.example.adrian.klient.maps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.adrian.klient.R;

public class InfoWindow extends AppCompatActivity {

    private String infoWindowText;
    private String lat;
    private String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window);
        final EditText editText = (EditText) findViewById(R.id.editText);

        // Get all info about the selected marker, which was attatched to the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            infoWindowText = extras.getString("EXTRA_MARKER_OLD_TEXT");
            lat = extras.getString("EXTRA_MARKER_LAT");
            lon = extras.getString("EXTRA_MARKER_LON");
        }

        if (infoWindowText.equals("Klicka för att ange info")) {
            editText.setHint(infoWindowText);
        }else{
            editText.setText(infoWindowText);
        }

        // The button that modifies the selected markers info
        Button setText = (Button) findViewById(R.id.setText);
        setText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String event = editText.getText().toString();
                if (event.equals("")) {
                    intent.putExtra("EXTRA_MARKER_NEW_TEXT", "Ospecifierat ärende");
                } else {
                    intent.putExtra("EXTRA_MARKER_NEW_TEXT", event);
                }
                intent.putExtra("EXTRA_MARKER_OLD_TEXT", infoWindowText);
                intent.putExtra("EXTRA_MARKER_LON", lon);
                intent.putExtra("EXTRA_MARKER_LAT", lat);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        // The button that deletes the selected marker
        Button deleteMarker = (Button) findViewById(R.id.Delete);
        deleteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("EXTRA_MARKER_OLD_TEXT", infoWindowText);
                intent.putExtra("EXTRA_MARKER_LON", lon);
                intent.putExtra("EXTRA_MARKER_LAT", lat);
                setResult(Activity.RESULT_FIRST_USER, intent);
                finish();
            }
        });

    }

    // Returns the user to the map if the back button on the client is clicked
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
