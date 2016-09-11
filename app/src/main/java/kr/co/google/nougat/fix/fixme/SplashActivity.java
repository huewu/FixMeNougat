package kr.co.google.nougat.fix.fixme;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {


    private static boolean initFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLibraries();

        if (!initFlag) {
            startAppLoading();
        } else {
            finish();
        }
    }

    //initiate network library
    private void initLibraries() {
        MyNetworkLibrary.initiate();
    }

    //loading animation
    private void startAppLoading() {

        initFlag = true;

        final TextView loadingText = (TextView) findViewById(R.id.loading_text);

        new Thread() {
            @Override
            public void run() {
                int loadingCt = 0;
                while (loadingCt < 100) {
                    SystemClock.sleep(30);
                    ++loadingCt;
                    final int finalLoadingCt = loadingCt;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingText.setText(finalLoadingCt + "%");
                        }
                    });
                }

                //finish loading.
                finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

            }
        }.start();

    }

    //jump to the main activity.
}
