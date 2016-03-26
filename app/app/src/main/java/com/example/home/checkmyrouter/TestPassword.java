package com.example.home.checkmyrouter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 23.03.2016.
 */
public class TestPassword implements TestManager {
    private List<String> name;
    private List<String> pass;

    public static ScanService context = null;

    private boolean testPassed = false;

    @Override
    public void test() {
      //  Log.w("Router", getRouterIp());
       // Log.w("PASStest", "✓");
        connectToRouter();
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
               "IP address - default is 192.168.1.1 .\n" +
               "Go to settings and set your password";
    }

    private String getRouterIp(){
        final WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final DhcpInfo dhcp = manager.getDhcpInfo();
        String result = Formatter.formatIpAddress(dhcp.gateway);
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

        AssetManager am = context.getAssets();
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
            //Log.w("Reading finished", "✓");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connectToRouter() {
        //Log.w("Before Credentials", "✓");
        this.getCredentials();
       // Log.w("TestPass service before", "✓");


        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < name.size(); i++) {
                    try {
                        HttpURLConnection c = (HttpURLConnection) new URL("http://192.168.1.1").openConnection();

                        c.setRequestProperty("Authorization", getB64Auth(name.get(i), pass.get(i)));
                        if (c.getResponseMessage().equals("Unauthorized")) {
                            c.disconnect();
                        } else {
                          //  Log.w("TestPass", c.getResponseMessage());

                          //  Log.w("Autentizacia", "✓");

                            setTestPassed(false);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread testPass = new Thread(r);
        testPass.start();
        return testPassed();
    }
}
