package com.example.home.checkmyrouter;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public interface ITest {

    interface ITestCallback {
        void onTestDone(boolean result);
    }

    void runTest(ITestCallback callback);

}
