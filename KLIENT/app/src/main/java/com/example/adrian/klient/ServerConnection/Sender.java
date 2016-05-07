package com.example.adrian.klient.ServerConnection;

import java.io.PrintWriter;

/**
 * Created by dennisdufback on 2016-03-07.
 */
class Sender extends Thread
{
    private PrintWriter mOut;
    private String message;

    public Sender(PrintWriter aOut)
    {
        mOut = aOut;
    }

    public void run()
    {
        try {
            while (!message.isEmpty()) {
                mOut.println(message);
                mOut.flush();
                message = "";
            }
        }catch (Exception e){
            System.out.println("Nothing to send");
        }

    }
    public void setMessage(String message){
        this.message = message;
    }

}