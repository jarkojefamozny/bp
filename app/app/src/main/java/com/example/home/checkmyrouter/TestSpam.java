package com.example.home.checkmyrouter;

import android.util.Log;

/**
 * Class TestSpam is testing spam -> useless code running for 10 sec
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class TestSpam implements TestManager{
    private boolean testPassed = false;
    private static final String NAME = "SPAM test";

    public TestSpam() {
    }

    public TestSpam(boolean testPassed) {
        this.testPassed = testPassed;
    }

    @Override
    public String testName() {
        return NAME;
    }
    @Override
    public boolean testPassed() {
        return testPassed;
    }
    @Override
    public String getSolution() {
        return  "SPAM";
    }

    @Override
    public void runTest(ITestCallback callback) {
        Log.w("SPAM", "testing spam");
        try {
            Thread.sleep(10000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        callback.onTestDone(true);
    }
}
