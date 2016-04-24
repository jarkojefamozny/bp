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

    private ArrayList<TestManager> mTests;
    private Map<TestManager, Boolean> results = new HashMap<>();

    @Override
    public void onCreate() {
        Log.w("SERVICA", "onCreate");
        super.onCreate();
        TestManager pass = new TestPassword();
        TestManager encr = new TestEncryption();
        TestManager spam = new TestSpam();
        mTests = new ArrayList<>();
        mTests.add(pass);
        mTests.add(encr);
        mTests.add(spam);
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
        TestSpam.sContext = ScanService.this;
        TestPassword.sContext = ScanService.this;
        TestEncryption.sContext = ScanService.this;

        for(final TestManager test : mTests){
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
