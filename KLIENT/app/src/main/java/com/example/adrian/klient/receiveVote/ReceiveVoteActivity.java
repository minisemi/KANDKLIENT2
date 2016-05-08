package com.example.adrian.klient.receiveVote;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adrian.klient.R;
import com.example.adrian.klient.vote.FileEncryption;
import com.example.adrian.klient.vote.VoteAsyncTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocket;

public class ReceiveVoteActivity extends AppCompatActivity {


    public ReceiveVoteActivity receiveVoteActivity = this;
    public TextView votes1;
    public TextView votes2;
    FileEncryption encryption;


    ReceiveAsyncTask updateIPAsync;
    FileEncryption decryptReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_vote);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button updateIP = (Button) findViewById(R.id.button);


        votes1 = (TextView) findViewById(R.id.textView1_1);
        votes2 = (TextView) findViewById(R.id.textView2_1);

        votes1.setText("0");
        votes2.setText("0");

        updateIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateIPAsync = new ReceiveAsyncTask(receiveVoteActivity, "update");

            }
        });


        int PORT = 9001;
        try {
            decryptReceiver = new FileEncryption();
        } catch (GeneralSecurityException g){
            g.printStackTrace();
        }

        try {
        ServerSocket serverSocket = new ServerSocket(PORT);
        //serverSocket.setNeedClientAuth(true);
        System.out.println("Client server is up!");
        System.out.println("Waiting for server connection at the port: " + PORT);

            while (true) {
                new ConnectionListener(serverSocket.accept(), receiveVoteActivity, decryptReceiver).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        /*ServerSocket serverSocket = null;
        try {

            serverSocket = new ServerSocket(LISTENING_PORT);
            System.out.println("Server started on port " + LISTENING_PORT);
        } catch (IOException se) {
            System.err.println("Can not start listening on port " + LISTENING_PORT);
            se.printStackTrace();
            System.exit(-1);
        }*/

    }



    static class ConnectionListener extends Thread {
        FileUtils fileUtils;
        File in1;
        File in2;
        public File encrypted1;
        File encrypted1_1;
        File encrypted2;
        File encrypted2_1;
        public File decrypted1;
        File decrypted1_1;
        File rsaPublicKeyReceiver;
        File rsaPublicKeyMix;
        public File encryptedAesKeyReceiver;
        File rsaPrivateKeyMix;
        File encryptedAesKeyMix;
        public File rsaPrivateKeyReceiver;

        private Socket socket;
        FileEncryption decryptReceiver;
        ReceiveVoteActivity receiveVoteActivity;
        //Processor processor;

        public ConnectionListener(Socket socketValue, ReceiveVoteActivity receiveVoteActivity, FileEncryption decryptReceiver) {
            socket = socketValue;
            this.receiveVoteActivity = receiveVoteActivity;
            this.decryptReceiver = decryptReceiver;
        }

        public void run() {

            try {
                try {
                    InputStream inputStream = receiveVoteActivity.getAssets().open("encrypted1.txt");
                    encrypted1 = stream2file(inputStream, "encrypted1", ".txt");
                    inputStream = receiveVoteActivity.getAssets().open("encryptedAesKeyReceiver.txt");
                    encryptedAesKeyReceiver = stream2file(inputStream, "encryptedAesKeyReceiver", ".txt");
                    inputStream = receiveVoteActivity.getAssets().open("privateReceiver.der");
                    rsaPrivateKeyReceiver = stream2file(inputStream, "privateReceiver", ".der");
                    inputStream = receiveVoteActivity.getAssets().open("decrypted1.txt");
                    decrypted1 = stream2file(inputStream, "decrypted1", ".txt");
                } catch (IOException i){
                    i.printStackTrace();
                }
                //PrintWriter out = null;
                BufferedReader in;
                try {

                    System.out.println("Connection received from: " + socket.getInetAddress().getHostAddress());

                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    //out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                    String clientMessage = in.readLine();
                    while (!clientMessage.isEmpty()){
                       // processor = new Processor(clientMessage);
                        System.out.println("Server says: " + clientMessage);
                        processMessage(clientMessage);
                        clientMessage = "";
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void processMessage (String message) {
            JsonParser parser = new JsonParser();
            JsonObject receivedObject = (JsonObject) parser.parse(message);
            String activity = receivedObject.get("activity").getAsString().toLowerCase();
            decryptReceiver(encrypted1, decrypted1);

        }

        public void decryptReceiver (File in, File out){
            try {
                fileUtils = new FileUtils();
                //String encryptedText = fileUtils.readFileToString(in).replace("RANDOM_SALT","");
                //JsonParser parser = new JsonParser();
                //JsonObject jo = (JsonObject)parser.parse(encryptedText1);
                //String [] parts = encryptedText.split("\"?,?\"[a-z]*\":\"");
                //String encryptedMessage = jo.get("message").getAsString().toLowerCase();
                /*String encryptedMessage = parts[2];
                String [] encryptedKeyParts = parts [3].split("\"}");
                //String encryptedKey = jo.get("aeskey").getAsString().toLowerCase();
                String encryptedKey = parts[3].replace("\"}", "");
                BufferedWriter writer = new BufferedWriter(new FileWriter(encryptedAesKeyReceiver, false));
                writer.write(encryptedKey);
                writer.close();*/
                /*writer = new BufferedWriter(new FileWriter(in, false));
                writer.write(encryptedMessage);
                writer.close();*/
                decryptReceiver.loadKey(encryptedAesKeyReceiver, rsaPrivateKeyReceiver);
                decryptReceiver.decrypt(in, out);
                String decryptedText = fileUtils.readFileToString(out);
                System.out.println(decryptedText);
                handleVote(decryptedText);

            } catch (IOException i){
                i.printStackTrace();
            } catch (GeneralSecurityException e){
                e.printStackTrace();
            }
        }

        private void handleVote (String vote){
            if (vote.equals("Alexander rostar pa 1")){
                int votes = Integer.parseInt(receiveVoteActivity.votes1.getText().toString())+1;
                receiveVoteActivity.votes1.setText(votes);
            } else if(vote.equals("Alexander rostar pa 2")){
                int votes = Integer.parseInt(receiveVoteActivity.votes2.getText().toString())+1;
                receiveVoteActivity.votes2.setText(votes);
            }

        }

        public File stream2file (InputStream in, String prefix, String suffix) throws IOException {
            //File tempFile = File.createTempFile(prefix, suffix);
            File file = new File (receiveVoteActivity.getFilesDir(), prefix+suffix);
            //tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(in, out);
            }
            // return tempFile;
            return file;
        }
    }

}
