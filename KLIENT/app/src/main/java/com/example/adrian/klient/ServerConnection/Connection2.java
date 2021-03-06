package com.example.adrian.klient.ServerConnection;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by Fredrik on 16-03-05.
 */
public class Connection2 implements Runnable {
    private Context context;
    private String json;
    private String message;

    public Connection2(Request request, Context context) {
        this.context = context;
        message = request.message;
    }

    @Override
    public void run() {

//        String SERVERADRESS = "192.168.1.236";
//        String SERVERADRESS = "130.236.227.173";
        String SERVERADRESS = "2016-4.itkand.ida.liu.se";
        String SERVERADRESS_BACKUP = "2016-3.itkand.ida.liu.se";
        int SERVERPORT = 9001;
        int SERVERPORT_BACKUP = 9001;
        BufferedReader in = null;
        PrintWriter out = null;

        /**
         * Connect to primary server, if it fails, connect to backup server
         */
        Socket s = null;
        try {
            // Connect to primary server
            s = new Client(context).getConnection2(SERVERADRESS, SERVERPORT);
        } catch (IOException e) {
            // Print error and try connect to backup server
            System.err.println("Cannot establish connection to " +
                    SERVERADRESS + ":" + SERVERPORT);
            System.err.println("Trying to connect to backup server on " + SERVERADRESS_BACKUP +
                    ":" + SERVERPORT_BACKUP);
            try {
                s = new Client(context).getConnection2(SERVERADRESS_BACKUP, SERVERPORT_BACKUP);
            } catch (SSLHandshakeException ssle){
                System.out.println("Error on SSL handshake");
                System.exit(0);
            } catch (IOException e1) {
                e1.printStackTrace();
                System.err.println("Cannot establish connection to any server :(");
                System.exit(0);
            }
        }

        try {
            in = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(
                    new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("");
        }

        // Create a thread to write to
        System.out.println("MSG: " + message);
        Sender sender = new Sender(out);
        sender.setMessage(message);
        sender.setDaemon(true);
        sender.start();
        try {
            // Read messages from the server and print them
            String msg;
            while ((msg = in.readLine()) != null) {
                json = msg;
                setJson(msg);
            }
        } catch (IOException ioe) {
            System.err.println("Connection to server broken.");
            ioe.printStackTrace();
        }
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}


