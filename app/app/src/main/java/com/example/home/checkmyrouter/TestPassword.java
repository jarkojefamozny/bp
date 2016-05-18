package com.example.home.checkmyrouter;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class TestPassword testing default password
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class TestPassword implements TestManager {
    private List<String> name;
    private List<String> pass;
    protected static ScanService sContext;

    private boolean haveItried = false;
    private boolean testPassed = true;

    private static final String NAME = "Default password test";

    public TestPassword() {
    }

    @Override
    public String testName() {
        return NAME;
    }

    @Override
    public boolean testPassed() {
        return testPassed;
    }

    public void setTestPassed(boolean testPassed) {
        this.testPassed = testPassed;
    }

    @Override
    public String getSolution() {
        return "Launch web browser.\n" +
                "Log into your device through router\n" +
                "IP address " + getRouterIp() + ".\n" +
                "Go to settings and set your password";
    }

    @Override
    public void runTest(ITestCallback callback) {
        Log.w("PASS", "testing default");
        connectToRouter();
        Log.w("PASSWORD", "I am finished");
        callback.onTestDone(testPassed);
    }

    private String getRouterIp(){
        final WifiManager manager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String result = Formatter.formatIpAddress(dhcp.gateway);
        Log.w("GET ROUTER IP", result);
        return result.equals("0.0.0.0") ? "192.168.1.1" : result;
    }

    private String getB64Auth (String login, String pass) {
        String source=login+":"+pass;
        String ret="Basic "+ Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    private void getCredentials(){
        name = new ArrayList<>();
        pass = new ArrayList<>();

        AssetManager am = sContext.getAssets();
        //Log.w("getAssets", "&#10003;");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(am.open("credentials.txt")))){
            String line;
            while((line = in.readLine()) != null)
            {
                String parts[] = {"", ""};
                parts = line.split(":", -1);
                name.add(parts[0]);
                pass.add(parts[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connectToRouter() {
        this.getCredentials();
        final String url = "http://"+ getRouterIp();

        for (int i = 0; i < name.size(); i++) {
            if(haveItried){
                break;
            }
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                c.setRequestProperty("Authorization", getB64Auth(name.get(i), pass.get(i)));
                if (c.getResponseMessage().equals("Unauthorized")) {
                    c.disconnect();
                    Log.w("PASS", String.valueOf(i) + ". try");
                } else {
                    Log.w("PASS", c.getResponseMessage());
                    Log.w("PASS", "TEST failed");
                    setTestPassed(false);
                    haveItried = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return testPassed;
    }
}
