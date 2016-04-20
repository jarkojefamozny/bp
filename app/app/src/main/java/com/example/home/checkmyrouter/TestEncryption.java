package com.example.home.checkmyrouter;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Formatter;
import android.util.Log;

import java.util.List;

/**
 * Created by Home on 23.03.2016.
 */
public class TestEncryption implements TestManager, Parcelable {
    public static ScanService sContext;
    private boolean testPassed = false;
    public static final String BROADCAST_ENCRYPTION_TEST = "encr";
    private volatile boolean amIFinished = false;
    private static final String NAME = "Encryption test";
    Intent intent;

    public TestEncryption() {
    }

    public TestEncryption(boolean testPassed) {
        this.testPassed = testPassed;
    }

    @Override
    public String testName() {
        return NAME;
    }

    @Override
    public void test() {
        Log.w("ENCRYPT", "testing encryption");
        List<ScanResult> networkList = ((WifiManager) sContext.getSystemService(Context.WIFI_SERVICE)).getScanResults();

        for (ScanResult network : networkList)
        {
            String Capabilities = network.capabilities;
            if(Capabilities.contains("WPA"))
            {
                testPassed = true;
            }
            else if(Capabilities.contains("WEP"))
            {
                testPassed = false;
            }
        }

        intent = new Intent(BROADCAST_ENCRYPTION_TEST);
        intent.putExtra("pass", testPassed);
        sContext.sendBroadcast(intent);
        Log.w("ENRYPT", "I am finished");
        Log.w("ENRYPT", "Result: " + testPassed);
    }

    @Override
    public boolean testPassed() {
        return testPassed;
    }

    @Override
    public String getSolution() {
        return  "Launch web browser.\n" +
                "Log into your device through router\n" +
                "IP address " + getRouterIp() + " .\n" +
                "Go to settings and set your encryption to WPA2\n" +
                "If there is no such an option, consider\n" +
                "changing router.";
    }

    private String getRouterIp(){
        final WifiManager manager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String result = Formatter.formatIpAddress(dhcp.gateway);
        Log.w("GET ROUTER IP", result);
        return result.equals("0.0.0.0") ? "192.168.1.1" : result;
    }


    protected TestEncryption(Parcel in) {
        testPassed = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt((byte) (testPassed ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TestEncryption> CREATOR = new Parcelable.Creator<TestEncryption>() {
        @Override
        public TestEncryption createFromParcel(Parcel in) {
            return new TestEncryption(in);
        }

        @Override
        public TestEncryption[] newArray(int size) {
            return new TestEncryption[size];
        }
    };
}
