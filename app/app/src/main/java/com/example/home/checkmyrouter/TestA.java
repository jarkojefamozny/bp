package com.example.home.checkmyrouter;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public class TestA implements ITest {

    @Override
    public void runTest(ITestCallback callback) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callback.onTestDone(true);
    }

}
