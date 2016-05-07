package com.example.adrian.klient.contactList;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.example.adrian.klient.VidCom.AppRtcGo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Contact extends AppCompatActivity {
    private String name, address, phoneNr, title, ssn, salary;
    private int permission;

    String response;
    ProgressDialog waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get the contact info and display it as the Title
        final Bundle extras = getIntent().getExtras();
        toolbar.setTitle(extras.getString("NAME"));
        setSupportActionBar(toolbar);
        FloatingActionButton call_fab = (FloatingActionButton) findViewById(R.id.call_fab);

        TextView titleView, ssnView, salaryView, addressView;
        // Displayed for all permission levels
        TextView nameView = (TextView) findViewById(R.id.nameView);
        nameView.setText(extras.getString("NAME"));
        TextView phoneNrView = (TextView) findViewById(R.id.phoneView);
        phoneNrView.setText(extras.getString("PHONE_NR"));

        TableRow addressRow = (TableRow) findViewById(R.id.addressRow);
        TableRow titleRow = (TableRow) findViewById(R.id.titleRow);
        TableRow salaryRow = (TableRow) findViewById(R.id.salaryRow);
        TableRow ssnRow = (TableRow) findViewById(R.id.ssnRow);
        switch (extras.getInt("PERMISSION")){
            case 1:
                // Displayed for medium permission
                addressRow.setVisibility(View.VISIBLE);
                addressView = (TextView) findViewById(R.id.addressView);
                addressView.setText(extras.getString("ADDRESS"));
                titleRow.setVisibility(View.VISIBLE);
                titleView = (TextView) findViewById(R.id.titleView);
                titleView.setText(extras.getString("TITLE"));
                break;
            case 2:
                // Displayed for high permission
                addressRow.setVisibility(View.VISIBLE);
                addressView = (TextView) findViewById(R.id.addressView);
                addressView.setText(extras.getString("ADDRESS"));
                ssnRow.setVisibility(View.VISIBLE);
                ssnView = (TextView) findViewById(R.id.ssnView);
                ssnView.setText(extras.getString("SSN"));
                titleRow.setVisibility(View.VISIBLE);
                titleView  = (TextView) findViewById(R.id.titleView);
                titleView.setText(extras.getString("TITLE"));
                salaryRow.setVisibility(View.VISIBLE);
                salaryView = (TextView) findViewById(R.id.salaryView);
                salaryView.setText(extras.getString("SALARY"));

                Button delete = (Button) findViewById(R.id.deleteContact);
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Request deleteRequest = new Request(Contact.this,"delete",extras.getString("SSN")).contactRequest();
                        Connection connection = new Connection(deleteRequest, Contact.this, handler);
                        Thread t = new Thread(connection);
                        t.start();
                    }
                });
                break;
        }

        call_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waiting = new ProgressDialog(Contact.this,ProgressDialog.STYLE_SPINNER);
                waiting.setMessage("CALLING");
                waiting.setCancelable(false);
                waiting.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                waiting.show();

                Request callRequest = new Request(Contact.this,"call",extras.getString("PHONE_NR")).callRequest();
                Connection connection = new Connection(callRequest, Contact.this, handler);
                new Thread(connection).start();

            }
        });

    }
    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            Looper.prepare();
            response = (String) msg.getData().get("json");
            waiting.dismiss();
            doSomething();
        }
    };

    private void doSomething() {
        JsonParser parser = new JsonParser();
        JsonObject data = (JsonObject)parser.parse(response);
        boolean callready = data.get("callready").getAsBoolean();
        if(callready){
            String videoID = data.get("videoid").getAsString();
            startChatting(videoID);
        } else {
            System.out.println("ERROR LOL");
        }
    }

    private void startChatting(String id){
        System.out.println("Starting videoCall");
        Intent intent = new Intent(Contact.this, AppRtcGo.class);
        intent.putExtra("ROOM", id);
        startActivity(intent);
    }

    //Setters and getters
    public String get_name() {
        return this.name;
    }
    public void set_name(String name){
        this.name = name;
    }
    public String getSSN() {
        return this.ssn;
    }
    public void setSSN(String ssn){
        this.ssn = ssn;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getPhoneNr() {
        return this.phoneNr;
    }
    public void setPhoneNr(String phoneNr){
        this.phoneNr = phoneNr;
    }
    public String getWorkTitle() {
        return this.title;
    }
    public void setWorkTitle(String title){
        this.title = title;
    }
    public String getSalary() {
        return this.salary;
    }
    public void setSalary(String salary){
        this.salary = salary;
    }
    public int getPermission(){
        return this.permission;
    }
    public void setPermission(int permission){
        this.permission = permission;
    }


}
