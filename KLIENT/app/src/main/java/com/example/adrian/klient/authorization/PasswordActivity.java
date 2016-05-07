package com.example.adrian.klient.authorization;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adrian.klient.MainMenuActivity;
import com.example.adrian.klient.R;

import java.util.ArrayList;

public class PasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final EditText passwordView = (EditText) findViewById(R.id.passwordInput);
        Button enterButton = (Button) findViewById(R.id.enterButton);

        enterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Check if password matches
                if (getPasswordList().contains(passwordView.getText().toString().hashCode())) {
                    Intent intent = new Intent(PasswordActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(PasswordActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    passwordView.setText("");
                }
            }
        });
    }

    private ArrayList<Integer> getPasswordList() {
        ArrayList<Integer> passwordList = new ArrayList<>();
        //TODO: Get hashed passwords from database
        Integer testPass = "test".hashCode();
        passwordList.add(testPass);
        return passwordList;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
