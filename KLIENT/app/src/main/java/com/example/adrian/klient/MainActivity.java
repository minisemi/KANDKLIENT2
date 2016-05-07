package com.example.adrian.klient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.adrian.klient.authorization.NFCAuthorization;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Clear User information
        getSharedPreferences("USER_INFO",MODE_PRIVATE).edit().clear().apply();

        Button authorizationButton = (Button) findViewById(R.id.NFCButton);
        Button guestBtn = (Button) findViewById(R.id.guestButton);

        guestBtn.setBackgroundColor(Color.TRANSPARENT);

        authorizationButton.setOnClickListener(this);
        guestBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.NFCButton:
                intent = new Intent(this, NFCAuthorization.class);
                startActivity(intent);
                break;

            case R.id.guestButton:
                intent = new Intent(this, MainMenuActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        }
    }