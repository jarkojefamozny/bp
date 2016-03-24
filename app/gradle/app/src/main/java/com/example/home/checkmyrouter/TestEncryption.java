package com.example.home.checkmyrouter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Home on 23.03.2016.
 */
public class TestEncryption implements TestManager {
    private boolean isOk = false;
    public static ScanService context = null;

    @Override
    public void test() {
        Log.w("SCAN SERVICE", "SOM TU?");
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        ScanResult network = (ScanResult) wifi.getScanResults();
        String Capabilities =  network.capabilities;

        if(Capabilities.contains("WPA"))
        {
            isOk = true;
        }
        else if(Capabilities.contains("WEP"))
        {
            isOk = false;
        }
    }

    @Override
    public boolean testPassed() {
        return isOk;
    }

    @Override
    public String getSolution() {
        return  "Launch web browser.\n" +
                "Log into your device through router\n" +
                "IP address - default is 192.168.1.1 .\n" +
                "Go to settings and set your encryption to WPA2\n" +
                "If there is no such an option, consider\n" +
                "changing router.";
    }
}
