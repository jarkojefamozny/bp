package com.example.home.checkmywifi;

import android.app.Activity;
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
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 12.03.2016.
 */
public class MyBackgroundTask {
    private String result= "";
    private List<String> routerLines;

    public void connectToRouter() {
        routerLines = new ArrayList<String>();
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("http://192.168.1.1").openConnection();

            c.setRequestProperty("Authorization", getB64Auth("admin", "admin"));

            Log.w("myApp", "presla kvazi autentizacia");

            InputStream is = c.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = in.readLine()) != null) {
                routerLines.add(line);
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public String getRouterName() {
        for(String line : routerLines){

            if ( line.contains("<title>")){
                Log.w("myApp", line);
                result = line;
            }
            //Log.w("myApp", line);
        }
        return result;
    }

    private String getB64Auth (String login, String pass) {
        String source=login+":"+pass;
        String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
        return ret;
    }
}
