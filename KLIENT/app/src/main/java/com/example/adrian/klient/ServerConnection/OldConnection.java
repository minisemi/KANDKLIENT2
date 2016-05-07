package com.example.adrian.klient.ServerConnection;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Fredrik on 16-03-05.
 */
public class OldConnection implements Runnable {
    private String json;
    private Request request;
    private Context context;
    //    private final String SERVERADRESS = "192.168.1.236";
    private final String SERVERADRESS = "2016-4.itkand.ida.liu.se";
    private final int SERVERPORT = 9001;
    private boolean result;

    public OldConnection(Request request, Context context) {
        this.context = context;
        this.request = request;
    }


    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // Connect to Server
            Socket socket = new Socket(SERVERADRESS, SERVERPORT);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException ioe) {
            System.err.println("Can not establish connection to " +
                    SERVERADRESS + ":" + SERVERPORT);
            ioe.printStackTrace();
            System.exit(-1);
        }
        // Create a thread to write to
        Sender sender = new Sender(out);
        sender.setMessage(request.message);
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

    public void close() {
        close();
    }

}