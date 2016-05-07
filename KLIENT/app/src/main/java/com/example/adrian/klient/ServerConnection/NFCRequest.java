package com.example.adrian.klient.ServerConnection;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by dennisdufback on 2016-03-15.
 */
public class NFCRequest extends Request{

    public NFCRequest(Context context,String action, String... args) {
        super(context,action, args);
    }

    @Override
    public void buildRequest(String action, int uid) {
        JsonObject request = new JsonObject();

        request.addProperty("activity","nfc");
        request.addProperty("action",action);
        request.addProperty("min_per",0);

        // Array object for data params
        JsonObject params = new JsonObject();
        JsonArray data = new JsonArray();

        //args[0] contains the NFCid
        params.addProperty("NFCid",args[0]);
        data.add(params);
        request.add("data", data);
        super.message = request.toString();
    }
}
