package com.example.adrian.klient.contactList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.ContactRequest;
import com.example.adrian.klient.ServerConnection.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    Connection connection;
    private ArrayList contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ListView listView = (ListView)findViewById(R.id.contactList);
        // Arraylist to store the contacts
        contactList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,contactList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactList.this, Contact.class);
                Contact pressed = (Contact) contactList.get(position);

                intent.putExtra("PERMISSION", pressed.getPermission());
                intent.putExtra("NAME", pressed.get_name());
                intent.putExtra("SSN", pressed.getSSN());
                intent.putExtra("ADDRESS", pressed.getAddress());
                intent.putExtra("PHONE_NR", pressed.getPhoneNr());
                intent.putExtra("TITLE", pressed.getWorkTitle());
                intent.putExtra("SALARY", pressed.getSalary());
                startActivity(intent);
            }
        });

        getContacts();

    }

    public void getContacts (){
        Request getContacts = new ContactRequest(this,"get");
        connection = new Connection(getContacts,this);
        Thread t = new Thread(connection);
        t.start();

        //Get response from server
        String jsonString;
        do{
            jsonString = connection.getJson();
        } while(jsonString == null);
        System.out.println("jsonString: " + jsonString);

        contactList = new ArrayList<>();
        //Parser to parse through all contacts
        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject)parser.parse(jsonString);
        int permission = object.get("permission").getAsInt();
        JsonArray data = (JsonArray) object.get("data");

        //Add the contacts to the list
        int pos = 0;
        for(JsonElement e : data){
            String title, salary, ssn, address;
            Contact contact = new Contact();
            JsonObject o = e.getAsJsonObject();

            //Put the contact info on to current contact
            String name = o.get("name").getAsString();
            String phoneNr = o.get("phonenr").getAsString();

            //All permission fields
            contact.setPermission(permission);
            contact.set_name(name);
            contact.setPhoneNr(phoneNr);

            switch (permission){
                case 1:
                    // Mid permission fields
                    title = o.get("title").getAsString();
                    address = o.get("address").getAsString();
                    contact.setWorkTitle(title);
                    contact.setAddress(address);
                    break;
                case 2:
                    // Highest permission fields
                    address = o.get("address").getAsString();
                    contact.setAddress(address);
                    title = o.get("title").getAsString();
                    contact.setWorkTitle(title);
                    ssn = o.get("pnr").getAsString();
                    contact.setSSN(ssn);
                    salary = o.get("pay").getAsString();
                    contact.setSalary(salary);
                    break;
                default:
                    // Lowest priority gets only name, address and phoneNr-fields
            }
            adapter.add(name);
            contactList.add(pos,contact);
            pos++;

        }

    }



}
