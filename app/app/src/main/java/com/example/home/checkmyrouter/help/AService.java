package com.example.home.checkmyrouter.help;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public class AService extends Service implements IAService {

    private AServiceBinder mBinder = new AServiceBinder();

    class AServiceBinder extends Binder implements IAService {
        @Override
        public void startTests(IAServiceCallback callback) {
            AService.this.startTests(callback);
        }

        @Override
        public List<String> getTestNames() {
            return AService.this.getTestNames();
        }
    }

    private ArrayList<ITest> mTests;

    @Override
    public void startTests(final IAServiceCallback callback) {
        for (final ITest test : mTests) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test.runTest(new ITest.ITestCallback() {
                        @Override
                        public void onTestDone(boolean result) {
                            callback.onTestDone(test.getClass().getSimpleName(), result);
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public List<String> getTestNames() {
       List<String> names = new ArrayList<>();
        for(ITest test : mTests) {
           names.add(test.getClass().getSimpleName());
       }
        return names;
    }

    public void onCreate() {
        super.onCreate();
        ITest testA = new TestA();
        ITest testB = new TestB();
        mTests = new ArrayList<>();
        mTests.add(testA);
        mTests.add(testB);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
