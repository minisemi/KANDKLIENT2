package com.example.adrian.klient.contactList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.AppRestart;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * Created by Dotson on 2016-05-02.
 */
public class ContactListAsyncTask extends AsyncTask<Void, Void, String> {

    com.example.adrian.klient.ServerConnection.Connection connection;
    Context context;
    ArrayList contactList;
    ArrayAdapter adapter;
    Intent intent;

    private JsonObject object;
    private String active;

        public ContactListAsyncTask(Context context, Intent intent){

            this.context = context;
            this.intent = intent;
        }

    @Override
    protected String doInBackground(Void... params) {
        Request getContacts = new Request(context,"get").contactRequest();
        connection = new Connection(getContacts,context);
        Thread t = new Thread(connection);
        t.start();

        //Get response from server
        String jsonString;
        do{
            jsonString = connection.getJson();
        } while(jsonString == null);
        System.out.println("jsonString: " + jsonString);
        return jsonString;
    }

    protected void onPostExecute (String result) {


        JsonParser parser = new JsonParser();
        JsonObject object = (JsonObject) parser.parse(result);
        Boolean check =  object.get("active").getAsBoolean();


        if(!check){

            new AlertDialog.Builder(context).setIcon(R.drawable.alert)
                    .setTitle("Utloggade")
                    .setMessage("Du har blivit utloggad")
                    .setPositiveButton("Okej", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AppRestart().restart(context);
                        }
                    }).show();

           // Toast.makeText(context, "FALSE", Toast.LENGTH_SHORT).show();
        }


        else if (result != "Timeout" && result != "Lost") {
            intent.putExtra("DATA", result);
            context.startActivity(intent);
        } else if(result == "Lost") {
            Toast.makeText(context, "Connection Lost", Toast.LENGTH_SHORT).show();
        }
            else{
            Toast.makeText(context, "Connection Timeout", Toast.LENGTH_SHORT).show();
        }



    }
}
