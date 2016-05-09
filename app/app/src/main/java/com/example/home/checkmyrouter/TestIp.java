package com.example.home.checkmyrouter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

/**
 * Created by Home on 05.05.2016.
 */
public class TestIp implements TestManager{
    public static ScanService sContext;
    private boolean testPassed = false;
    private boolean haveItried = false;
    private static final String NAME = "Default IP test";

    @Override
    public String testName() {
        return NAME;
    }

    @Override
    public void runTest(ITestCallback callback) {
        if(getRouterIp().equals("192.168.1.1")){
            Log.w("IP TEST", "failed");
            haveItried = true;
        }

        if(getRouterIp().equals("192.168.1.2")){
            Log.w("IP TEST", "failed");
            haveItried = true;
        }

        if(getRouterIp().equals("192.168.0.1")){
            Log.w("IP TEST", "failed");
            haveItried = true;
        }

        testPassed = !haveItried;

        Log.w("IP TEST FINAL", String.valueOf(testPassed));
        callback.onTestDone(testPassed);
    }

    @Override
    public boolean testPassed() {
        return testPassed;
    }

    @Override
    public String getSolution() {
        return "Launch web browser.\n" +
                "Log into your device through router\n" +
                "IP address 192.168.1.1.\n" +
                "Go to settings and set your IP for \n" +
                "something untrivial like 10.9.8.7";
    }

    private String getRouterIp(){
        final WifiManager manager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String result = Formatter.formatIpAddress(dhcp.gateway);
        Log.w("GET ROUTER IP", result);
        return result;
    }
}
