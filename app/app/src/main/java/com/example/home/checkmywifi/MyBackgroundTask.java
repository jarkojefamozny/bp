package com.example.home.checkmywifi;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Home on 12.03.2016.
 */
public class MyBackgroundTask {
    private String routerName= "";
    private String routerFirm= "";
    private String routerEncrypt = "";
    private List<String> routerLines;
    // Most often used credentials for default settings
    private List<String> name = Arrays.asList("Admin", "Router", "router", "", "admin");
    private List<String> pass = Arrays.asList("admin", "Admin", "Router", "router", "", "13ftun9d");
    private boolean breaker = false;

    public void connectToRouter() {
        routerLines = new ArrayList<String>();
        for (int i = 0; i < name.size(); i++) {
            for (int j = 0; j < pass.size(); j++) {
                if (!breaker) {
                    try {
                        HttpURLConnection c = (HttpURLConnection) new URL("http://192.168.1.1").openConnection();

                        c.setRequestProperty("Authorization", getB64Auth(name.get(i), pass.get(j)));
                        if (c.getResponseMessage().equals("Unauthorized")) {
                            c.disconnect();
                            Log.w("UN", "Teeraz čo?");
                        } else {
                            Log.w("myApp", c.getResponseMessage());

                            Log.w("myApp", "presla kvazi autentizacia");

                            InputStream is = c.getInputStream();
                            BufferedReader in = new BufferedReader(new InputStreamReader(is));
                            String line;
                            while ((line = in.readLine()) != null) {
                                routerLines.add(line);
                            }
                            breaker = true;
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //TODO login manager
    }

    public String getRouterName() {
        for(String line : routerLines){

            if ( line.contains("<title>")){
                Log.w("myApp", line);
                routerName = line;
            }
            //Log.w("myApp", line);
        }

        /**
         * <title>ASUS Wireless Router RT-N53 - Mapa sítě</title>
         */
        String split[] = routerName.split(">| - ");
        for( String word : split)
        {
            Log.w("MENo", word);
        }

        routerName = split[1];
        return routerName;
    }

    public String getFirmVersion() {
        Log.w("FIRMTEST", "SME TU?");
        for(String line : routerLines){

            if ( line.contains("firmver")){
                Log.w("myApp", line);
                routerFirm = line;
                break;
            }
        }

        /**
         * <input type="hidden" name="firmver" value="3.0.0.4">
         */
        String split[] = routerFirm.split("\"");
        for( String word : split)
        {
            Log.w("YOLO", word);
        }

        routerFirm = split[5];
        return routerFirm;
    }

    private String getB64Auth (String login, String pass) {
        String source=login+":"+pass;
        String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
        return ret;
    }
}
