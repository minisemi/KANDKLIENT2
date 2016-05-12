package com.example.adrian.klient.vote;

import android.content.Context;
import android.os.AsyncTask;
import com.example.adrian.klient.ServerConnection.Connection;
import com.example.adrian.klient.ServerConnection.Request;

/**
 * Created by Dotson on 2016-04-04.
 */
public class VoteAsyncTask extends AsyncTask<Void,Void,String> {

    com.example.adrian.klient.ServerConnection.Connection connection;
    private Context context;
    private String jsonMessage, encrypted;
    private String startTime, duration, limit, count;

    public VoteAsyncTask(Context context, String jsonMessage, String encrypted, String time, String limit, String count){
        this.context = context;
        this.jsonMessage = jsonMessage;
        this.encrypted = encrypted;
        this.startTime = time;
        this.limit = limit;
        this.count = count;

    }

    @Override
    protected String doInBackground(Void... params) {

        Thread t;
        Request request = new Request(context, "vote", jsonMessage, encrypted, startTime, limit, count).voteRequest();
        connection = new Connection(request, context);
        t = new Thread(connection);
        t.start();

        return null;
    }

    protected void onPostExecute (String result){

    }
}
