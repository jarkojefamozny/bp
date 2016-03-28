package com.example.home.checkmyrouter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Home on 23.03.2016.
 */
public class TestEncryption implements TestManager {
    private boolean testPassed = false;
    public static ScanService sContext = null;

    @Override
    public String testName() {
        return "Encryption test";
    }

    @Override
    public void test() {
        List<ScanResult> networkList = ((WifiManager) sContext.getSystemService(Context.WIFI_SERVICE)).getScanResults();
        for (ScanResult network : networkList)
        {
            //Log.w("ENRYPTION TEST", network.capabilities);
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
                "IP address - default is 192.168.1.1 .\n" +
                "Go to settings and set your encryption to WPA2\n" +
                "If there is no such an option, consider\n" +
                "changing router.";
    }
}
