package com.example.home.checkmyrouter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Home on 23.03.2016.
 */
public class ScanService extends Service implements ServiceManager {

    public static final String BROADCAST_ACTION = "com.example.home.checkmyrouter";
    private final Handler handler = new Handler();
    Intent intent;

    TestManager test;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private Map<TestManager, Boolean> results = new HashMap<>();
    private List<TestManager> tests = new ArrayList<>();

    private double actual = 0;

    @Override
    public void onCreate() {
        Log.w("SERVICA", "onCreate");
        super.onCreate();

        handler.removeCallbacks(sendUpdatesToUI);
        IntentFilter filter = new IntentFilter();
        registerReceiver(receiver, new IntentFilter(TestPassword.BROADCAST_PASSWORD_TEST));
        registerReceiver(receiver, new IntentFilter(TestEncryption.BROADCAST_ENCRYPTION_TEST));
        registerReceiver(receiver, new IntentFilter(TestSpam.BROADCAST_SPAM_TEST));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
    }

    public void getSources(List<TestManager> tests) {
        for (TestManager test : tests) {
            this.tests.add(test);
        }
    }

    @Override
    public void run() {
        TestSpam.sContext = ScanService.this;
        TestPassword.sContext = ScanService.this;
        TestEncryption.sContext = ScanService.this;
        for(final TestManager test : tests){
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    test.test();
                }
            };

            Thread testPass = new Thread(r);
            testPass.start();
        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            int index = tests.lastIndexOf(test);
            DisplayLoggingInfo(test, index);
        }
    };

    private void DisplayLoggingInfo(TestManager test, int index) {
        intent = new Intent(BROADCAST_ACTION);
        Log.d("DATA", "entered DisplayLoggingInfo");
        Log.w("PARCELUJEM", "Na indexe : " + String.valueOf(index));
        intent.putExtra("test", (Parcelable) test);
        intent.putExtra("index", String.valueOf(tests.indexOf(test)));
        sendBroadcast(intent);
        Log.d("BUNDLE", intent.toString());
    }

    public class LocalBinder extends Binder {
        ScanService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ScanService.this;
        }
    }

    public ScanService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(TestEncryption.BROADCAST_ENCRYPTION_TEST)){
                Bundle bundle = intent.getExtras();
                boolean result = bundle.getBoolean("pass");
                Log.w("BROADCAST", "received ENC " + result);
                test = getTest("Encryption test");
                results.put(test, result);
                sendUpdatesToUI.run();
            } else if(action.equals(TestPassword.BROADCAST_PASSWORD_TEST)) {
                Bundle bundle = intent.getExtras();
                boolean result = bundle.getBoolean("pass");
                Log.w("BROADCAST", "received PASS = " + result);
                test = getTest("Default password test");
                results.put(test, result);
                sendUpdatesToUI.run();
            } else if(action.equals(TestSpam.BROADCAST_SPAM_TEST)){
                Bundle bundle = intent.getExtras();
                boolean result = bundle.getBoolean("pass");
                test = getTest("SPAM test");
                results.put(test, result);
                Log.w("BROADCAST", "received SPAM " + result);
                sendUpdatesToUI.run();
            }
        }
    };

    private TestManager getTest(String name){
        for(TestManager test : tests){
            if(test.testName().equals(name)){
                return test;
            }
        }

        return this.test;
    }

    @Override
    public Map getResults() {
        return results;
    }
}
