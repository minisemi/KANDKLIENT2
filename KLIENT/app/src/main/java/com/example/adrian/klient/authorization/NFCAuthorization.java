package com.example.adrian.klient.authorization;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.NFCRequest;
import com.example.adrian.klient.ServerConnection.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class NFCAuthorization extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Connection connection;
    TextView textViewInfo;
    protected boolean authenticated;
    public static final String PREFS = "USER_INFO";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_nfcauthorization);
        textViewInfo = (TextView) findViewById(R.id.info);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        authenticated = false;

        int authenticationCode;
        authenticationCode = getTagID(intent);
        getIDList(authenticationCode);

        if(authenticated){
            Toast.makeText(this, "AUTHENTICATED", Toast.LENGTH_SHORT).show();
            Intent passwordIntent = new Intent(this, PasswordActivity.class);
            startActivity(passwordIntent);

        } else {
            Toast.makeText(this, "Failed to authenticate NFC tag", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Returns the ID of the NFC tag
     * @param intent
     * @return
     */
    private int getTagID(Intent intent) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String authenticationCode = "";
        byte[] tagId = tag.getId();

        for (int i = 0; i < tagId.length; i++) {
            authenticationCode += Integer.toHexString(tagId[i] & 0xFF);
        }
//        System.out.println("AUTH:" + authenticationCode.hashCode());
        return authenticationCode.hashCode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    public void getIDList(int authCode) {
        //TODO: Get the list from the database via the server.

        Request getID = new NFCRequest(this,"get",String.valueOf(authCode));
        System.out.println("MESSAGE "+getID.message);
        connection = new Connection(getID,this);
        Thread t = new Thread(connection);
        t.start();

        /**
         * Get response from server
         */
        String jsonString;
        do{
            jsonString = connection.getJson();
        } while(jsonString == null);

        System.out.println("JSON STRING FROM SERVER> " + jsonString);

        //Fetch the id's
        try {
            JsonParser parser = new JsonParser();
            JsonObject fromServer = (JsonObject)parser.parse(jsonString);
            JsonArray data = fromServer.getAsJsonArray("data");
            for (Object o : data) {
                JsonObject jo = (JsonObject) o;
                boolean access = jo.get("access").getAsBoolean();
                if(access){
                    authenticated = true;
                    String name = jo.get("name").getAsString();
                    int id = jo.get("nfcid").getAsInt();

                    // Get shared resourse and set the user's id
                    preferences = getSharedPreferences(PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("USER_NAME",name);
                    editor.putInt("USER_ID",id);
                    editor.apply();
                    //
                    Toast.makeText(NFCAuthorization.this,"access: " + authenticated + "\nName: " + name + "\nNFC ID: " + id,Toast.LENGTH_LONG).show();
                    System.out.println("access: " + authenticated + "\nName: " + name + "\nNFC_ID: " + id);
                    //
                }
            }
        } catch (JsonSyntaxException syntax){
            System.out.println("Wrong syntax on server message");
        } catch (ClassCastException c){
            System.out.println("Wrong syntax on server message");
        }
    }
}
