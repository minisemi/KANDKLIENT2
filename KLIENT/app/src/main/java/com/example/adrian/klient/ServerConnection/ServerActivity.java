package com.example.adrian.klient.ServerConnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.adrian.klient.R;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ServerActivity extends AppCompatActivity {

    Button ssl,wrongssl,nossl;
    OldConnection oldConn;
    Connection conn;
    Connection2 conn2;
    List addList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_server);
        addList = new ArrayList();
        ssl = (Button) findViewById(R.id.sslbutt);
        wrongssl = (Button) findViewById(R.id.wrongsslbutt);
        nossl = (Button) findViewById(R.id.nosslbutt);

            JsonObject addReq = new JsonObject();
            addReq.addProperty("lat", 58.8963790165493);
            addReq.addProperty("lon", 15.460723824799063);
            addReq.addProperty("event","Här brinner det!");
            addList.add(addReq);


        ssl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Request request = new Request(ServerActivity.this, "add", addList).mapRequest();
                conn = new Connection(request, ServerActivity.this);
                Thread t = new Thread(conn);
                t.start();
            }

        });
        wrongssl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request2 = new Request(ServerActivity.this,"add",addList).mapRequest();
                conn2 = new Connection2(request2,ServerActivity.this);
                Thread t = new Thread(conn2);
                t.start();
            }
        });
        nossl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = new Request(ServerActivity.this,"add",addList).mapRequest();
                oldConn = new OldConnection(request,ServerActivity.this);
                Thread t = new Thread(oldConn);
                t.start();
            }
        });
    }
}

