package com.example.adrian.klient.vote;


import android.content.Context;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.adrian.klient.R;
import com.example.adrian.klient.internetcontrol.InternetController;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Scanner;

public class VoteActivity extends AppCompatActivity {
    public Context context = this;
    private static final String TAG = VoteActivity.class.getSimpleName();
        public InternetController internetController = new InternetController(this);

    public String addressReceiver = "345.2534.64.65";
    FileEncryption encryption;
    FileEncryption decryptionMix;
    FileEncryption decryptionReceiver;
    File in1;
    File in2;
    File encrypted1;
    File encrypted1_1;
    File encrypted2;
    File encrypted2_1;
    File decrypted1;
    File decrypted1_1;
    File rsaPublicKeyReceiver;
    File rsaPublicKeyMix;
    File encryptedAesKeyReceiver;
    File rsaPrivateKeyMix;
    File encryptedAesKeyMix;
    File rsaPrivateKeyReceiver;
    FileUtils fileUtils;
    VoteAsyncTask voteAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {

            InputStream inputStream = this.getAssets().open("In1.txt");
            in1 = stream2file(inputStream, "In1",".txt");
            inputStream = this.getAssets().open("In2.txt");
            in2 = stream2file(inputStream, "In2", ".txt");
            inputStream = this.getAssets().open("encrypted1.txt");
            encrypted1 = stream2file(inputStream, "encrypted1", ".txt");
            inputStream = this.getAssets().open("encrypted2.txt");
            encrypted2 = stream2file(inputStream, "encrypted2", ".txt");
            inputStream = this.getAssets().open("encrypted1_1.txt");
            encrypted1_1 = stream2file(inputStream, "encrypted1_1", ".txt");
            inputStream = this.getAssets().open("encrypted2_1.txt");
            encrypted2_1 = stream2file(inputStream, "encrypted2_1",".txt");
            inputStream = this.getAssets().open("decrypted1.txt");
            decrypted1 = stream2file(inputStream, "decrypted1",".txt");
            inputStream = this.getAssets().open("decrypted1_1.txt");
            decrypted1_1 = stream2file(inputStream, "decrypted1_1",".txt");
            inputStream = this.getAssets().open("publicReceiver.der");
            rsaPublicKeyReceiver = stream2file(inputStream, "publicReceiver",".der");
            inputStream = this.getAssets().open("encryptedAesKeyReceiver.txt");
            encryptedAesKeyReceiver = stream2file(inputStream, "encryptedAesKeyReceiver",".txt");
            inputStream = this.getAssets().open("encryptedAesKeyMix.txt");
            encryptedAesKeyMix = stream2file(inputStream, "encryptedAesKeyMix",".txt");
            inputStream = this.getAssets().open("privateSender.der");
            rsaPrivateKeyMix = stream2file(inputStream, "privateSender",".der");
            inputStream = this.getAssets().open("publicSender.der");
            rsaPublicKeyMix = stream2file(inputStream, "publicSender",".der");
            inputStream = this.getAssets().open("privateReceiver.der");
            rsaPrivateKeyReceiver = stream2file(inputStream, "privateReceiver",".der");
            inputStream.close();


            fileUtils = new FileUtils();
            decryptionMix = new FileEncryption();
            decryptionReceiver = new FileEncryption();
            encryption = new FileEncryption();
            encryption.makeKey();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        }

        catch (IOException i){
            i.printStackTrace();
        }

        Button vote1 = (Button) findViewById(R.id.voteButton1);
        vote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetController.InternetAvailable()) {
                    voteAsyncTask = new VoteAsyncTask(context, encryptMix(encrypted1, encrypted1_1, encryptClient(in1, encrypted1)));
                    voteAsyncTask.execute();
                    //String string = encryptMix(encrypted1, encrypted1_1, encryptClient(in1, encrypted1));
                    //JsonObject j = encryptClient(in1, encrypted1);

                    Snackbar.make(v, "Update successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(v, "No connection available. Update not successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        Button vote2 = (Button) findViewById(R.id.voteButton2);
        vote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetController.InternetAvailable()) {

                    //encrypt(in2, encrypted2);
                    //decryptMix (encrypted1_1, decrypted1_1);
                    //decryptReceiver (decrypted1_1, decrypted1);
                    //decryptReceiver(encrypted1, decrypted1);
                    voteAsyncTask = new VoteAsyncTask(context, encryptMix(encrypted2, encrypted2_1, encryptClient(in2, encrypted2)));
                    voteAsyncTask.execute();
                    Snackbar.make(v, "Update successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(v, "No connection available. Update not successful!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public File stream2file (InputStream in, String prefix, String suffix) throws IOException {
       //File tempFile = File.createTempFile(prefix, suffix);
        File file = new File (this.getFilesDir(), prefix+suffix);
       //tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(in, out);
        }
       // return tempFile;
        return file;
    }

    public String encrypt (File in, File out, File rsaPublicKey, File encryptedAesKey) {
        try {
            Log.d(TAG, "File text");
            String encryptedText1 = fileUtils.readFileToString(in);
            Log.d(TAG, encryptedText1);

            encryption.saveKey(encryptedAesKey, rsaPublicKey);
            encryption.encrypt(in, out);
            Log.d(TAG, "Encrypted file text");

            String encryptedText = fileUtils.readFileToString(out);
                Log.d(TAG, encryptedText);
            //BufferedReader brM1 = new BufferedReader(new InputStreamReader(new FileInputStream(out)));


            return encryptedText;
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        } catch (IOException i){
            i.printStackTrace();
        }
        return null;
    }

    public void decryptMix (File in, File out){
        try {
            Log.d(TAG, "Encrypted text at Mix");
            String encryptedText1 = fileUtils.readFileToString(in);;
            Log.d(TAG, encryptedText1);
            decryptionMix.loadKey(encryptedAesKeyMix, rsaPrivateKeyMix);
            decryptionMix.decrypt(in, out);
            Log.d(TAG, "Decrypted text at Mix");
            String encryptedText = fileUtils.readFileToString(out);;
            Log.d(TAG, encryptedText);
        } catch (IOException i){
            i.printStackTrace();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        }
    }

    public void decryptReceiver (File in, File out){
        try {
            String encryptedText1 = fileUtils.readFileToString(in).replace("RANDOM_SALT","");
            //JsonParser parser = new JsonParser();
            //JsonObject jo = (JsonObject)parser.parse(encryptedText1);
            String [] parts = encryptedText1.split("\"?,?\"[a-z]*\":\"");
            //String encryptedMessage = jo.get("message").getAsString().toLowerCase();
            String encryptedMessage = parts[2];
            String [] encryptedKeyParts = parts [3].split("\"}");
            //String encryptedKey = jo.get("aeskey").getAsString().toLowerCase();
            String encryptedKey = parts[3].replace("\"}", "");
            BufferedWriter writer = new BufferedWriter(new FileWriter(encryptedAesKeyReceiver, false /*append*/));
            writer.write(encryptedKey);
            writer.close();
            decryptionReceiver.loadKey(encryptedAesKeyReceiver, rsaPrivateKeyReceiver);
            writer = new BufferedWriter(new FileWriter(in, false /*append*/));
            writer.write(encryptedMessage);
            writer.close();
            decryptionReceiver.decrypt(in, out);
            Log.d(TAG, "Decrypted text at receiver");
            String encryptedText = fileUtils.readFileToString(out);;
            Log.d(TAG, encryptedText);
        } catch (IOException i){
            i.printStackTrace();
        } catch (GeneralSecurityException e){
            e.printStackTrace();
        }
    }

    public String encryptClient(File vote, File encryptedVote){

            String encryptedText = encrypt(vote, encryptedVote, rsaPublicKeyReceiver, encryptedAesKeyReceiver);
            String encryptedAesKey = encryption.getEncryptedAesKey(encryptedAesKeyReceiver);
            //String jsonString = "{\"address\":\"345.2534.64.65\",\"message\":\""+encryptedText+"\",\"aeskey\":\""+encryptedAesKey+"\"}";
            JsonObject params = new JsonObject();
            params.addProperty("address", addressReceiver);
            params.addProperty("message", encryptedText);
            params.addProperty("aeskey", encryptedAesKey);
            return params.toString();
    }

    public String encryptMix (File in, File out, String jsonString) {

        try{

        String encryptedClientText = "RANDOM_SALT" + jsonString;
        BufferedWriter writer = new BufferedWriter(new FileWriter(in, false /*append*/));
            writer.write(encryptedClientText);
            writer.close();

            String encryptedText = encrypt(in, out, rsaPublicKeyMix, encryptedAesKeyMix);
            String encryptedAesKey = encryption.getEncryptedAesKey(encryptedAesKeyMix);
            //String jsonStringMix = "{\"activity\":\"vote\",\"message\":\""+encryptedText+"\",\"aeskey\":\""+encryptedAesKey+"\"}";
            Log.d(TAG, "jsonStringMix");
            Log.d(TAG, jsonString);
            JsonObject paramsMix = new JsonObject();
            paramsMix.addProperty("activity", "vote");
            paramsMix.addProperty("message", encryptedText);
            Log.d(TAG, "Encrypted AES key Mix");
            Log.d(TAG, encryptedAesKey);
            paramsMix.addProperty("aeskey", encryptedAesKey);
            return paramsMix.toString();

    }catch (IOException e) {
            Log.e(TAG, "Unable to write to the file.");
        }
        return "";

    }

}
