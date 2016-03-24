package com.example.home.checkmyrouter;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Home on 23.03.2016.
 */
public class ScanService extends Service implements ServiceManager {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private final TestManager passwordTest = new TestPassword();
    private final TestManager encryptionTest = new TestEncryption();

    public class LocalBinder extends Binder {
        ScanService getService() {
            // Return this instance of LocalService so clients can call public methods
            Log.w("LOCALBINDER", "✓");
            return ScanService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void run() {
        Log.w("SCAN SERVICE - RUN", "✓");
        TestPassword.context = this;
        passwordTest.test();
        Log.w("SCAN SERVICE", String.valueOf(passwordTest.testPassed()));

        TestEncryption.context = this;
        encryptionTest.test();
        Log.w("SCAN SERVICE", String.valueOf(encryptionTest.testPassed()));
    }

    @Override
    public boolean getResult() {
        return false;
    }
}
