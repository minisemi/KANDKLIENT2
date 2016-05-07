package com.example.adrian.klient.ServerConnection;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Pontus on 2016-04-04.
 */
public class ContactRequest extends Request {
    public ContactRequest(Context context, String action, String... args) {
        super(context,action,args);
    }
    @Override
    public void buildRequest(String action, int id) {
        JsonObject request = new JsonObject();

        request.addProperty("activity", "contact");
        request.addProperty("action", action);

        // Array object for data params
        JsonObject params = new JsonObject();
        params.addProperty("id",id);
        switch (action){
            case "get":
                break;
            case "delete":
                params.addProperty("deletekey",args[0]);
                break;
            case "add":
                break;
        }
        JsonArray data = new JsonArray();
        data.add(params);
        request.add("data", data);
        super.message = request.toString();
    }
}
