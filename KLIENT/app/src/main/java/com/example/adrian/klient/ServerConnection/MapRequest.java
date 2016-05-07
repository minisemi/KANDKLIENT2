package com.example.adrian.klient.ServerConnection;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by dennisdufback on 2016-03-14.
 */
public class MapRequest extends Request {
    public MapRequest(Context context,String action, String... args) {
        super(context,action,args);
    }
    @Override
    public void buildRequest(String action, int id) {
        JsonObject request = new JsonObject();
        request.addProperty("activity","map");
        request.addProperty("action",action);
        // Array object for data params
        JsonObject params = new JsonObject();
        switch (action){
            case "get":
//                request.addProperty("min_per",0); // lvl 0 to get markers
                break;
            case "add":
//                request.addProperty("min_per",1); // lvl 1 to add markers
                params.addProperty("lat",args[0]);
                params.addProperty("lon", args[1]);
                params.addProperty("event", args[2]);
                break;
            case "delete":
//                request.addProperty("min_per",2); // lvl 2 to delete markers
                params.addProperty("lat",args[0]);
                params.addProperty("lon", args[1]);
                break;
        }
        params.addProperty("id",id);

        JsonArray data = new JsonArray();
        data.add(params);
        request.add("data", data);

        super.message = request.toString();
    }

}
