package com.example.home.checkmyrouter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * Created by Home on 23.03.2016.
 */
public class TestPassword implements TestManager {
    private List<String> name;
    private List<String> pass;
    private double actualPercentage;

    private static final int NUMBER_OF_TESTS = 2;

    Handler handler = new Handler();

    protected static ScanService sContext;
    protected static BindingActivity bContext;

    private boolean defaultTest = false;
    private boolean testPassed = false;

    @Override
    public String testName() {
        return "Default password test";
    }

    @Override
    public void test() {
        connectToRouter();
    }

    @Override
    public boolean testPassed() {
        return testPassed;
    }

    public boolean isDefaultTest() {
        return defaultTest;
    }

    public void setDefaultTest(boolean defaultTest) {
        this.defaultTest = defaultTest;
    }

    public void setTestPassed(boolean testPassed) {
        this.testPassed = testPassed;
    }

    @Override
    public String getSolution() {
        return "Launch web browser.\n" +
               "Log into your device through router\n" +
               "IP address - default is 192.168.1.1 .\n" +
               "Go to settings and set your password";
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
        Log.w("getAssets", "✓");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(am.open("credentials.txt")))){
            String line;
            while((line = in.readLine()) != null)
            {
               // Log.w("Reading", "✓ " + line);
                String parts[] = {"", ""};
                parts = line.split(":", -1);
               // Log.w("Parts", "✓ " + "[" + parts[0] + "]" + "[" + parts[1] + "]");
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

        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < name.size(); i++) {
                    if(testPassed()){
                        break;
                    }

                    try {
                        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();

                        c.setRequestProperty("Authorization", getB64Auth(name.get(i), pass.get(i)));
                        if (c.getResponseMessage().equals("Unauthorized")) {
                            c.disconnect();
                        } else {
                            setTestPassed(false);
                            setDefaultTest(true);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    bContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double temp = (double) 100 / NUMBER_OF_TESTS;
                            double eps = (double) name.size() / temp;
                            int tmp = (int) actualPercentage;
                            actualPercentage += eps;
                            if(temp > tmp) {
                                actualPercentage += eps;
                                tmp = (int) actualPercentage;
                                bContext.percentage.setText(String.valueOf(tmp) + " %");

                                bContext.progressBar.setProgress(tmp);
                            }
                        }
                    });
                }
            }
        };

        Thread testPass = new Thread(r);
        testPass.start();
        return testPassed();
    }
}
