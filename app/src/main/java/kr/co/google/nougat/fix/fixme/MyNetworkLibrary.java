package kr.co.google.nougat.fix.fixme;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

/**
 * Created by chansuk on 2016. 9. 11..
 */

class MyNetworkLibrary {

    private static final String TAG = "MyNetworkLibrary";
    private static MyNetworkLibrary instance;

    static void initiate() {
        instance = new MyNetworkLibrary();
    }

    static MyNetworkLibrary getInstance() {
        return instance;
    }

    private MyNetworkLibrary() {
        //do some initialize job here.
    }

    HttpURLConnection connect() {
        // connect to echo server.
        try {
            URL url = new URL("http://httpbin.org/ip");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    Socket connectLocalTcp() {
        Socket sock = null;
        try {
            sock = new Socket();
            SocketAddress address
                    = new InetSocketAddress(
                    Inet4Address.getByName("telehack.com"), 23);
            sock.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sock;
    }

    void sendPing(Socket sock) {
        try {
            sock.getOutputStream().write(new byte[]{'p','i','n','g'});
            sock.close();
            Log.d(TAG, "Write 5bytes successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readMessage(HttpURLConnection conn) {
        try {
            StringBuilder builder = new StringBuilder();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) break;
                builder.append(line);
            }

            final String result = builder.toString();
            Log.d(TAG, builder.toString());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
