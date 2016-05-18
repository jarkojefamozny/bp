package com.example.home.checkmyrouter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class ScanService is a service which manages tests
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class ScanService extends Service implements ServiceManager {

    private ServiceBinder mBinder = new ServiceBinder();

    class ServiceBinder extends Binder implements ServiceManager {
        @Override
        public Map getResults() {
            return null;
        }

        @Override
        public void startTests(ServiceCallback callback) {
            ScanService.this.startTests(callback);
        }
    }

    private ArrayList<TestManager> tests = (ArrayList<TestManager>) MainActivity.tests;
    private Map<TestManager, Boolean> results = new HashMap<>();

    @Override
    public void onCreate() {
        Log.w("SERVICA", "START");
        super.onCreate();
    }

    public ScanService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public Map getResults() {
        return results;
    }

    @Override
    public void startTests(final ServiceCallback callback) {
        Log.w("SERVICA", "STARTTESTS");
        TestPassword.sContext = ScanService.this;
        TestEncryption.sContext = ScanService.this;
        TestIp.sContext = ScanService.this;

        for(final TestManager test : tests){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test.runTest(new TestManager.ITestCallback() {
                        @Override
                        public void onTestDone(boolean result) {
                            callback.onTestDone(test.testName(), result);
                        }
                    });
                }
            }).start();
        }
    }
}
