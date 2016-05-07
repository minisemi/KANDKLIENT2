package com.example.adrian.klient.ServerConnection;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dennisdufback on 2016-03-14.
 */

public class Request {
    public String action;
    public String message;
    public String[] args;
    SharedPreferences preferences;
    public String prefs = "USER_INFO";

    public Request(Context context,String action, String... args){

        preferences = context.getSharedPreferences(prefs,Context.MODE_PRIVATE);
        int id = preferences.getInt("USER_ID", 0);
        this.action = action;
        this.args = args;
        buildRequest(action, id);
    }

    public void buildRequest(String action,int uid) {
    }

}
