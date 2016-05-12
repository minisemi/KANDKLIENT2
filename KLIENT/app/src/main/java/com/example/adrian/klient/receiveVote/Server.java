package com.example.adrian.klient.receiveVote;

        import android.util.Log;

        import com.example.adrian.klient.ServerConnection.Connection;
        import com.example.adrian.klient.ServerConnection.Request;
        import com.example.adrian.klient.vote.FileEncryption;
        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;

        import org.apache.commons.io.FileUtils;
        import org.apache.commons.io.IOUtils;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.io.PrintStream;
        import java.io.PrintWriter;
        import java.net.InetAddress;
        import java.net.NetworkInterface;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.net.SocketException;
        import java.security.GeneralSecurityException;
        import java.util.Enumeration;
        import java.util.logging.Level;
        import java.util.logging.Logger;

public class Server {
    ReceiveVoteActivity activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8080;
    FileEncryption decryptReceiver;
    File in1;
    File in2;
    public File encrypted1;
    File encrypted1_1;
    File encrypted2;
    File encrypted2_1;
    public File decrypted1;
    File decrypted1_1;
    File rsaPublicKeyReceiver;
    File rsaPublicKeyMix;
    public File encryptedAesKeyReceiver;
    File rsaPrivateKeyMix;
    File encryptedAesKeyMix;
    public File rsaPrivateKeyReceiver;
    FileUtils fileUtils;

    private Socket socket;

    int order1=0, order2=0, limit1, limit2;
    String votes1, votes2;

    JsonObject receivedObject;
    BufferedReader in;
    int count1 = 0, count2 = 0;


    String receivedVote;
    Long timeElapsed1, timeElapsed2;
    String startTime1="0", startTime2="0";



    public Server(ReceiveVoteActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {




        @Override
        public void run() {
            //startTime = System.currentTimeMillis();
            ReceiveAsyncTask updateIPAsync = new ReceiveAsyncTask(activity);
            updateIPAsync.execute();
            /*Request request = new Request(activity, "receive");
            Connection connection = new Connection(request, activity);
            Thread t = new Thread(connection);
            t.start();*/
            try {
                // create ServerSocket using specified port
                //TESTA PORT 9000!!!
                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object




                    Socket socket = serverSocket.accept();

                    in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    String clientMessage = in.readLine();

                    while (!clientMessage.isEmpty()) {
                        //System.out.println("RECEIVED MESSAGE: " + clientMessage);

                        processMessage(clientMessage);
                        clientMessage ="";
                    }

                    //count++;

                    //message += "#" + count + " from " + socket.getInetAddress() + ":" + socket.getPort() + ": " + elapsed + "\n";


                    //OM SLUTAR FUNKA KÖR WEAKREFERENCE (KOLLA RECEIVEASYNCTASK)

                    socket.close();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void processMessage (String jsonString){
            //Create parser for the request
            JsonParser parser = new JsonParser();
            // Convert Json formatted string to one JsonObject
            receivedObject = (JsonObject) parser.parse(jsonString);
            String encrypted = receivedObject.get("encrypted").getAsString().toLowerCase();

            if (encrypted.equalsIgnoreCase("true")){

                try {
                    order1 = Integer.parseInt(receivedObject.get("count").getAsString().toLowerCase());
                    InputStream inputStream = activity.getAssets().open("encrypted1.txt");
                    encrypted1 = stream2file(inputStream, "encrypted1", ".txt");
                    inputStream = activity.getAssets().open("encryptedAesKeyReceiver.txt");
                    encryptedAesKeyReceiver = stream2file(inputStream, "encryptedAesKeyReceiver", ".txt");
                    inputStream = activity.getAssets().open("privateReceiver.der");
                    rsaPrivateKeyReceiver = stream2file(inputStream, "privateReceiver", ".der");
                    inputStream = activity.getAssets().open("decrypted1.txt");
                    decrypted1 = stream2file(inputStream, "decrypted1", ".txt");
                    inputStream = activity.getAssets().open("In1.txt");
                    in1 = stream2file(inputStream, "In1", ".txt");
                    inputStream = activity.getAssets().open("publicReceiver.der");
                    rsaPublicKeyReceiver = stream2file(inputStream, "publicReceiver", ".der");
                    inputStream.close();
                    String encryptedText1 = receivedObject.get("message").getAsString();
                    decryptReceiver = new FileEncryption();
                    decryptReceiver.makeKey();
                    decryptReceiver.saveKey(encryptedAesKeyReceiver, rsaPublicKeyReceiver);
                    //String encryptedAesKey = fileUtils.readFileToString(encryptedAesKeyReceiver);
                    //Log.d("RECEIVEASYNCTASK: ","DECRYPTED AESKEY: "+encryptedAesKey);
                    decryptReceiver.encrypt(in1, encrypted1);
                    decryptReceiver.loadKey(encryptedAesKeyReceiver, rsaPrivateKeyReceiver);
                    //String encryptedText = fileUtils.readFileToString(encrypted1);
                    //Log.d("RECEIVEASYNCTASK: ","ENCRYPTED TEXT: "+encryptedText);
                    decryptReceiver.decrypt(encrypted1, decrypted1);
                    //String decryptedText = fileUtils.readFileToString(decrypted1);
                    //Log.d("RECEIVEASYNCTASK: ","DECRYPTED TEXT: "+decryptedText);
                     count1++;

                    limit1 = Integer.parseInt(receivedObject.get("limit").getAsString().toLowerCase());
                    votes1 = Integer.toString(count1);
                    if (order1 == 1){
                        startTime1 = receivedObject.get("starttime").getAsString().toLowerCase();
                    }

                    if (count1 == limit1) {
                        timeElapsed1 = System.currentTimeMillis() - 1430 - Long.parseLong(startTime1);
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.votes1.setText(votes1);
                            if (count1 == limit1){
                                String elapsed1 = Long.toString(timeElapsed1);
                                message += count1 + " encrypted votes took: " + elapsed1 + " milliseconds" + "\n";
                                count1 = 0;
                                activity.msg.setText(message);

                            }
                        }
                    });

                } catch (IOException i){
                    i.printStackTrace();
                } catch (GeneralSecurityException g){
                    g.printStackTrace();
                }

            } else {

                order2 = Integer.parseInt(receivedObject.get("count").getAsString().toLowerCase());
                count2++;
                limit2 = Integer.parseInt(receivedObject.get("limit").getAsString().toLowerCase());
                votes2 = Integer.toString(count2);
                if (order2 == 1) {
                    startTime2 = receivedObject.get("starttime").getAsString().toLowerCase();
                }

                if (count2 == limit2) {
                    timeElapsed2 = System.currentTimeMillis() - 1430 - Long.parseLong(startTime2);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.votes2.setText(votes2);

                        if (count2 == limit2) {
                            String elapsed2 = Long.toString(timeElapsed2);
                            message += count2 + " non-encrypted votes took: " + elapsed2 + " milliseconds" + "\n";
                            count2 = 0;
                            activity.msg.setText(message);
                        }


                    }
                });
            }

        }

        public File stream2file (InputStream in, String prefix, String suffix) throws IOException {
            //File tempFile = File.createTempFile(prefix, suffix);
            File file = new File (activity.getFilesDir(), prefix+suffix);
            //tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(in, out);
            }
            // return tempFile;
            return file;
        }


    }


    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}