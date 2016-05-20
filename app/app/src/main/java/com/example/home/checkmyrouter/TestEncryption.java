package com.example.home.checkmyrouter;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.List;

/**
 * Class TestEncryption testing encryption in network
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class TestEncryption implements TestManager {
    public static ScanService sContext;
    private boolean testPassed = false;
    private static final String NAME = "Encryption test";

    public TestEncryption() {
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
        return  "Launch web browser.\n" +
                "Log into your device through router\n" +
                "IP address " + getRouterIp() + " .\n" +
                "Go to settings and set your encryption to WPA2\n" +
                "If there is no such an option, consider\n" +
                "changing router.";
    }

    @Override
    public void runTest(ITestCallback callback) {
        Log.w("ENCRYPT", "testing encryption");
        List<ScanResult> networkList = ((WifiManager) sContext.getSystemService(Context.WIFI_SERVICE)).getScanResults();
        Log.w("ENRYPT", String.valueOf(networkList.size()));
        for (ScanResult network : networkList)
        {
            String capabilities = network.capabilities;
            Log.w("ENRYPT", capabilities.toString());
            if(capabilities.contains("WPA"))
            {
                testPassed = true;
            }
            else if(capabilities.contains("WEP"))
            {
                testPassed = false;
            }
        }

        callback.onTestDone(testPassed);
        Log.w("ENRYPT", "I am finished");
        Log.w("ENRYPT", "Result: " + testPassed);
    }

    private String getRouterIp(){
        final WifiManager manager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String result = Formatter.formatIpAddress(dhcp.gateway);
        Log.w("GET ROUTER IP", result);
        return result.equals("0.0.0.0") ? "192.168.1.1" : result;
    }
}
