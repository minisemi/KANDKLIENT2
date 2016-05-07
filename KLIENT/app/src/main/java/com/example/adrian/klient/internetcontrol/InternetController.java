package com.example.adrian.klient.internetcontrol;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;

/**
 * Created by Villiam Rydfalk on 2016-03-29.
 */
public class InternetController {

    Context context;

    public InternetController(Context _context) {
        this.context = _context;
    }


    /**
     *  Creates a ConnectivityManager object and calls the getActiveNetworkInfo method and returns the result
     * @return
     */
    public boolean InternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connectivityManager.getActiveNetworkInfo() != null);

    }
}
