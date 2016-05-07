package com.example.adrian.klient.ServerConnection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.adrian.klient.R;

public class ServerActivity extends AppCompatActivity {

    Button btn, btn2;
    OldConnection oldConn;
    Connection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_server);

        btn = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.button2);

        // Create and start Sender thread

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  String query = "SELECT * FROM Test";
                Request oldRequest = new MapRequest(ServerActivity.this,"add","20","20","Katt i träd");
                System.out.println("msg: " + oldRequest.message);
                oldConn = new OldConnection(oldRequest,ServerActivity.this);
                Thread t = new Thread(oldConn);
                t.start();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Request request = new MapRequest(ServerActivity.this,"add","30","30","Katt i träd");
                System.out.println("msg: " + request.message);
                conn = new Connection(request,ServerActivity.this);
                Thread t = new Thread(conn);
                t.start();
            }

        });
    }
}

