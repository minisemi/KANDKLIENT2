package com.example.adrian.klient.contactList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.ContactRequest;
import com.example.adrian.klient.ServerConnection.Request;

public class Contact extends AppCompatActivity {
    private String name, address, phoneNr, title, ssn, salary;
    private int permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get the contact info and display it as the Title
        final Bundle extras = getIntent().getExtras();
        toolbar.setTitle(extras.getString("NAME"));
        setSupportActionBar(toolbar);

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
                        Request deleteRequest = new ContactRequest(Contact.this,"delete",extras.getString("SSN"));
                        Connection connection = new Connection(deleteRequest, Contact.this);
                        Thread t = new Thread(connection);
                        t.start();
                    }
                });
                break;
        }


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
