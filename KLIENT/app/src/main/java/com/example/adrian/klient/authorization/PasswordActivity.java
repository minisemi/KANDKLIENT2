package com.example.adrian.klient.authorization;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adrian.klient.MainMenuActivity;
import com.example.adrian.klient.R;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class PasswordActivity extends AppCompatActivity {
    Connection connection;
    String PREFS = "USER_INFO";
    SharedPreferences preferences;
    boolean authenticated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final EditText passwordView = (EditText) findViewById(R.id.passwordInput);
        Button enterButton = (Button) findViewById(R.id.enterButton);

        enterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                authenticated = false;
                String input = passwordView.getText().toString();
                int hashPass = input.hashCode();
                //Check if password matches
                if (!input.isEmpty()) {
                    login(hashPass);
                    if (authenticated) {
                        Intent intent = new Intent(PasswordActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(PasswordActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        passwordView.setText("");
                    }
                } else {
                    Toast.makeText(PasswordActivity.this, "Type something", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(int input) {
//        Request passRequest = new PassRequest(PasswordActivity.this,"pass",String.valueOf(input));
        Request passRequest = new Request(PasswordActivity.this, "get_pass", String.valueOf(input)).passRequest();
        connection = new Connection(passRequest, this);
        Thread t = new Thread(connection);
        t.start();

        /**
         * Get response from server
         */
        String jsonString;
        do {
            jsonString = connection.getJson();
        } while (jsonString == null);

        try {
            JsonParser parser = new JsonParser();
            JsonObject response = (JsonObject) parser.parse(jsonString);
            // Check access
            boolean access = response.get("access").getAsBoolean();
            if (access) {
                authenticated = true;
                // Data object in response
                JsonArray dataArray = response.getAsJsonArray("data");
                JsonObject data = dataArray.get(0).getAsJsonObject();
                String name = data.get("name").getAsString();
                String id = data.get("sessionid").getAsString();

                // Get shared resource and set the user's id
                preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("USER_NAME", name);
                //Replaces the NFCid with a sessionID
                editor.putString("USER_ID", id);
                editor.apply();
                //
                Toast.makeText(PasswordActivity.this, "Name: " + name + "\nAccess: " + access, Toast.LENGTH_LONG).show();
                //
            }

        } catch (JsonSyntaxException syntax) {
            System.out.println("Wrong syntax on server message");
        } catch (ClassCastException c) {
            System.out.println("Wrong syntax on server message");
        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
