package kr.co.google.nougat.fix.fixme;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.Socket;

/**
 * Created by chansuk on 2016. 9. 11..
 */

public class MyNetworkService extends IntentService {

    private static final String TAG = "MyNetworkService";
    private static final String CONNECT_SERVER = "CONNECT_SERVER";

    private MyNetworkHandler networkHandler;
    private MyNetworkLibrary networkLibrary;
    private boolean runningFlag = false;

    public MyNetworkService() {
        super(TAG);
        networkHandler = new MyNetworkHandler();
        networkLibrary = MyNetworkLibrary.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            if (intent.getAction().equals(CONNECT_SERVER) && !runningFlag) {
                runningFlag = true;
                //start network connect thread.
                new Thread() {
                    @Override
                    public void run() {

                        int count = 0;

                        while (true) {
                            ++count;
                            HttpURLConnection conn = MyNetworkLibrary.getInstance().connect();
                            MyNetworkLibrary.getInstance().readMessage(conn);
                            SystemClock.sleep(1000);
                            if (conn == null) {
                                throw new RuntimeException("Cannot connect to Server!");
                            } else if (count % 7 == 0){
                                Log.d(TAG, "Do something Special");
                                Socket socket = MyNetworkLibrary.getInstance().connectLocalTcp();
                                networkHandler.sendMessage(
                                        networkHandler.obtainMessage(MyNetworkHandler.RECV_ECHO, socket));
                            } else if (count > 50) {
                                Log.d(TAG, "Finish all the network job");
                                break;
                            }
                        }
                        runningFlag = false;
                    }
                }.start();
            }
        }
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, MyNetworkService.class);
        intent.setAction(CONNECT_SERVER);

        context.startService(intent);
    }

    private static class MyNetworkHandler extends Handler{

        private static final int RECV_ECHO = 1001;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what) {
                case RECV_ECHO:
                    //Send TCP Ping message.
                    MyNetworkLibrary.getInstance().sendPing((Socket) msg.obj);
                    break;
            }
        }
    }

}
