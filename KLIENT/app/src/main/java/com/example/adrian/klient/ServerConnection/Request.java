package com.example.adrian.klient.ServerConnection;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by dennisdufback on 2016-03-14.
 */

public class Request {
    String action, id, message;
    String[] args;
    List<JsonObject> argList;
    JsonObject request, data;
    JsonArray dataArray;
    SharedPreferences preferences;
    String PREFS = "USER_INFO";

    public Request(Context context,String action, String... args){
        this.action = action;
        this.args = args;

        request = new JsonObject();
        dataArray = new JsonArray();
        data = new JsonObject();

        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        id = preferences.getString("USER_ID","1234abcd");
    }
    public Request(Context context,String action, List<JsonObject> argList){
        this.action = action;
        this.argList = argList;

        request = new JsonObject();
        dataArray = new JsonArray();
        data = new JsonObject();

        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        id = preferences.getString("USER_ID","1234abcd");
    }



    public Request NFCRequest(){

        request.addProperty("activity","nfc");
        request.addProperty("action",action);
        request.addProperty("sessionid",id);

        //args[0] contains the NFCid
        data.addProperty("NFCid",args[0]);
        dataArray.add(data);
        request.add("data", dataArray);
        message = request.toString();
        return this;
    }

    public Request contactRequest() {

        request.addProperty("activity", "contact");
        request.addProperty("action", action);
        request.addProperty("sessionid",id);

        switch (action){
            case "get":
                break;
            case "delete":
                data.addProperty("deletekey", args[0]);
                break;
            case "add":
                break;
        }
        dataArray.add(data);
        request.add("data", dataArray);
        message = request.toString();
        return this;
    }

    public Request voteRequest (){
        request.addProperty("activity", "vote");
        request.addProperty("action", "vote");
        request.addProperty("sessionid", id);
        request.addProperty("encrypted", args[1]);
        request.addProperty("starttime", args[2]);
        request.addProperty("limit", args[3]);
        request.addProperty("count", args[4]);
        data.addProperty("message", args[0]);
        dataArray.add(data);
        request.add("data", dataArray);
        message = request.toString();
        return this;

    }

    public Request receiveRequest (){
        request.addProperty("activity", "receive");
        request.addProperty("action", "receive");
        request.addProperty("sessionid", id);
        data.addProperty("IP", "ServerIP");
        dataArray.add(data);
        request.add("data", dataArray);
        message = request.toString();
        return this;

    }

    public Request mapRequest() {

        request.addProperty("activity","map");
        request.addProperty("action",action);
        request.addProperty("sessionid", id);

        switch (action){
            case "get":
                break;
            default:
                for(JsonObject o : argList){
                    dataArray.add(o);
                }
                break;
        }
        request.add("data", dataArray);
        message = request.toString();
        return this;
    }

    public Request passRequest () {

        int nfc_id = preferences.getInt("NFC_ID",1337);

        request.addProperty("activity","pass");
        request.addProperty("action",action);
        request.addProperty("sessionid", id);

        //args[0] contains the NFCid
        data.addProperty("NFCid", nfc_id);
        data.addProperty("pass", args[0]);
        dataArray.add(data);

        request.add("data", dataArray);
        message = request.toString();
        return this;
    }

    public Request callRequest(){
        request.addProperty("activity", "video");
        request.addProperty("action","call");
        request.addProperty("sessionid",id);

        data.addProperty("tocall",args[0]);
        dataArray.add(data);
        request.add("data",dataArray);
        message = request.toString();
        return this;
    }

}
