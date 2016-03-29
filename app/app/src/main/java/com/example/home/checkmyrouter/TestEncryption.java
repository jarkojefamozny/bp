package com.example.home.checkmyrouter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.List;

/**
 * Created by Home on 23.03.2016.
 */
public class TestEncryption implements TestManager {
    private boolean testPassed = false;
    public static ScanService sContext = null;
    public static BindingActivity bContext = null;


    @Override
    public String testName() {
        return "Encryption test";
    }

    @Override
    public void test() {
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
}
