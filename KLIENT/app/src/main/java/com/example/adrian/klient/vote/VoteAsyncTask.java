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
    private String jsonMessage;
    private long startTime, duration;

    public VoteAsyncTask(Context context, String jsonMessage){
        this.context = context;
        this.jsonMessage = jsonMessage;
    }

    @Override
    protected String doInBackground(Void... params) {

        Thread t;
        startTime = System.currentTimeMillis();
        Request request = new Request(context, "vote", jsonMessage).voteRequest();
        connection = new Connection(request, context);
        t = new Thread(connection);
        t.start();

        return null;
    }

    protected void onPostExecute (String result){

    }
}
