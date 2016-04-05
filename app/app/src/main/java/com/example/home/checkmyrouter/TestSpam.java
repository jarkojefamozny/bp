package com.example.home.checkmyrouter;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Formatter;
import android.util.Log;

import java.util.List;
import java.util.Timer;

/**
 * Created by Home on 23.03.2016.
 */
public class TestSpam implements TestManager, Parcelable {
    private volatile boolean amIFinished = false;
    public static ScanService sContext;
    private boolean testPassed = false;
    public static final String BROADCAST_SPAM_TEST = "spam";
    private static final String NAME = "SPAM test";
    Intent intent;

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
    public void test() {
        Log.w("SPAM", "testing spam");
        try {
            Thread.sleep(10000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        Log.w("SPAM", "was that 10 sek");
        testPassed = true;
        intent = new Intent(BROADCAST_SPAM_TEST);
        intent.putExtra("pass", testPassed);
        sContext.sendBroadcast(intent);
    }

    @Override
    public boolean testPassed() {
        return testPassed;
    }

    @Override
    public String getSolution() {
        return  "SPAM";
    }

    protected TestSpam(Parcel in) {
        testPassed = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (testPassed ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TestSpam> CREATOR = new Parcelable.Creator<TestSpam>() {
        @Override
        public TestSpam createFromParcel(Parcel in) {
            return new TestSpam(in);
        }

        @Override
        public TestSpam[] newArray(int size) {
            return new TestSpam[size];
        }
    };
}