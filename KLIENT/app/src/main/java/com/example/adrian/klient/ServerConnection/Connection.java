package com.example.adrian.klient.ServerConnection;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Fredrik on 16-03-05.
 */
public class Connection implements Runnable {
//public class Connection extends Activity{
    private Context context;
    private String json;
    private String message;
    private boolean active;
    private Handler handler;
    int SERVERPORT = 9010;

    public Connection(Request request, Context context, Handler handler) {
        this.context = context;
        message = request.message;
        this.handler = handler;
        active = false;
    }
    public Connection(Request request, Context context) {
        this.context = context;
        message = request.message;
        active = false;
    }

    public Connection(Request request, Context context, int port) {
        this.context = context;
        message = request.message;
        active = false;
        SERVERPORT = port;
    }

    @Override
    public void run() {

        String SERVERADRESS = "2016-4.itkand.ida.liu.se";
        String SERVERADRESS_BACKUP = "2016-3.itkand.ida.liu.se";
        int SERVERPORT_BACKUP = 9001;
        BufferedReader in = null;
        PrintWriter out = null;

        /**
         * Connect to primary server, if it fails, connect to backup server
         */
        Socket s = null;
        try {
            // Connect to primary server
            //s = new Client(context).getConnection(SERVERADRESS, SERVERPORT);
            s = new Socket(SERVERADRESS, SERVERPORT);
        } catch (IOException e) {
            // Print error and try connect to backup server
            System.err.println("Cannot establish connection to " +
                    SERVERADRESS + ":" + SERVERPORT);
            System.err.println("Trying to connect to backup server on " + SERVERADRESS_BACKUP +
                    ":" + SERVERPORT_BACKUP);
            /*try {
                s = new Client(context).getConnection(SERVERADRESS_BACKUP, SERVERPORT_BACKUP);
            } catch (IOException e1) {

                System.err.println("Cannot establish connection to any server :(");
            }*/
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

            s.setSoTimeout(6000);
            String msg;
            while ((msg = in.readLine()) != null) {


                setJson(msg);

         /*       setActive(msg);
                if(active){
                    setJson(msg);

                    try{

                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putString("json",msg);
                    m.setData(b);
                    handler.dispatchMessage(m);
                    } catch (Exception e){
                        System.err.println("No handler...");
                    }
                }
            }
            if (!isActive()) {
                s.close();
                // Restart application if session isn't active
                new AppRestart();



       */     }
        }catch(SocketTimeoutException ste) {
            try {
                s.close();
                in.close();
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //this.run();
            setJson("Timeout");
        }
        catch(Exception ioe){
            try {
                s.close();
                in.close();
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            setJson("Lost");

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

    public void setActive(String json) {
        JsonParser parser = new JsonParser();
        JsonObject fromServer = (JsonObject) parser.parse(json);
        active = fromServer.get("active").getAsBoolean();
    }

    public boolean isActive() {
        return this.active;
    }

}


