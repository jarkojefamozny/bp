package com.example.home.checkmyrouter.help;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public class TestB implements ITest {

    @Override
    public void runTest(ITestCallback callback) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callback.onTestDone(false);
    }

}
