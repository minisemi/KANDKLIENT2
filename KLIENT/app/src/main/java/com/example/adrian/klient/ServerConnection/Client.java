package com.example.adrian.klient.ServerConnection;

import android.content.Context;
import android.util.Log;

import com.example.adrian.klient.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by dennisdufback on 2016-03-18.
 */
public class Client {
    private Context context;
    public Client(Context context){
        this.context = context;
    }

    protected Socket getConnection(String ip, int port) throws IOException{
        try{

            //Setup truststore
            KeyStore trustStore = KeyStore.getInstance("BKS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            InputStream trustStoreStream = context.getResources().openRawResource(R.raw.clienttruststore);
            // kanske kan ha detta som PW?
            trustStore.load(trustStoreStream, "hallonsorbet".toCharArray());
            trustManagerFactory.init(trustStore);

            //Setup keystore
//            KeyStore keyStore = KeyStore.getInstance("BKS");
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            InputStream keyStoreStream = context.getResources().openRawResource(R.raw.client);
//            keyStore.load(keyStoreStream,"hallonsorbet".toCharArray());
//            keyManagerFactory.init(keyStore,"hallonsorbet".toCharArray());

            //Setup SSL context to use the truststore and keystore
            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(),null);
            sslContext.init(null,trustManagerFactory.getTrustManagers(),null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) factory.createSocket(ip,port);
            socket.setEnabledCipherSuites(SSLUtils.getCipherSuitesWhiteList(socket.getEnabledCipherSuites()));
            socket.startHandshake();

            return socket;
        } catch (GeneralSecurityException e) {
            Log.e(this.getClass().toString(), "Exception while creating context: ", e);
            throw new IOException("Could not connect to SSL Server", e);
        }
    }
}
final class SSLUtils {

    private SSLUtils() { //non instantiable
    }

    public static String[] getCipherSuitesWhiteList(String[] cipherSuites) {
        List<String> whiteList = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        for (String suite : cipherSuites) {
            String s = suite.toLowerCase();
            if (s.contains("anon") || //reject no anonymous
                    s.contains("export") || //reject no export
                    s.contains("null") || //reject no encryption
                    s.contains("md5") || //reject MD5 (weaknesses)
                    s.contains("_des") || //reject DES (key size too small)
                    s.contains("krb5") || //reject Kerberos: unlikely to be used
                    s.contains("ssl") || //reject ssl (only tls)
                    s.contains("empty")) {    //not sure what this one is
                rejected.add(suite);
            } else {
                whiteList.add(suite);
            }
        }
//        Log.d(this.getClass().toString(), "Rejected Cipher Suites: {}", rejected);
        return whiteList.toArray(new String[whiteList.size()]);
    }
}
