package com.example.home.checkmyrouter;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public class MainActivity extends ABindingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    @Override
    void onServiceBound(IAService service) {
        service.startTests(this);
    }

    @Override
    public void onTestDone(final String serviceName, final boolean result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("TEST", String.format("Test %s is done with result: %s", serviceName, String.valueOf(result)));
            }
        });
    }

}
