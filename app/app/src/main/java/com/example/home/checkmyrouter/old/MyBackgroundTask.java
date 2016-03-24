package com.example.home.checkmyrouter.old;

import android.util.Base64;
import android.util.Log;

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
    private List<String> name = Arrays.asList("Admin", "Router", "router", "");
    private List<String> pass = Arrays.asList("admin", "Admin", "Router", "router", "", "13ftun9d");

    private boolean logged = false;
    private boolean breaker = false;

    public boolean connectToRouter() {
        if(!logged) {
            routerLines = new ArrayList<String>();
            for (int i = 0; i < name.size(); i++) {
                for (int j = 0; j < pass.size(); j++) {
                    if (!breaker) {
                        try {
                            HttpURLConnection c = (HttpURLConnection) new URL("http://192.168.1.1").openConnection();

                            c.setRequestProperty("Authorization", getB64Auth(name.get(i), pass.get(j)));
                            if (c.getResponseMessage().equals("Unauthorized")) {
                                c.disconnect();
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
                                logged = true;
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return breaker;
    }

    public boolean checkLogin(String name, String pass){
        try {
            HttpURLConnection c = (HttpURLConnection) new URL("http://192.168.1.1").openConnection();

            c.setRequestProperty("Authorization", getB64Auth(name, pass));
            if (c.getResponseMessage().equals("Unauthorized")) {
                c.disconnect();
            } else {
                logged = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logged;
    }

    public boolean loginToRouter(String name, String pass) {
        routerLines = new ArrayList<String>();
        Log.w("LOGINTOROUTER", String.valueOf((!breaker && ! logged)));
        if(!breaker && ! logged) {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL("http://192.168.1.1").openConnection();

                Log.w("LOGINTOROUTER", name + pass);
                c.setRequestProperty("Authorization", getB64Auth(name, pass));
                if (c.getResponseMessage().equals("Unauthorized")) {
                    c.disconnect();
                    Log.w("LOGINTOROUTER","Fail?");
                } else {
                    breaker = true;
                    logged = true;
                    Log.w("myApp", c.getResponseMessage());

                    Log.w("myApp", "presla kvazi autentizacia");

                    InputStream is = c.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = in.readLine()) != null) {
                        routerLines.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return breaker;
    }

    public String getRouterName() {
        if(logged) {
            for (String line : routerLines) {

                if (line.contains("<title>")) {
                    Log.w("myApp", line);
                    routerName = line;
                }
                //Log.w("myApp", line);
            }

            /**
             * <title>ASUS Wireless Router RT-N53 - Mapa sítě</title>
             */
            String split[] = routerName.split(">| - ");
            for (String word : split) {
                Log.w("MENo", word);
            }

            routerName = split[1];
            return routerName;
        } else {
            return "Fail";
        }
    }

    public String getFirmVersion() {
        if(logged) {
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
        } else {
            return "Fail";
        }
    }

    private String getB64Auth (String login, String pass) {
        String source=login+":"+pass;
        String ret="Basic "+Base64.encodeToString(source.getBytes(),Base64.URL_SAFE|Base64.NO_WRAP);
        return ret;
    }

    public boolean isLogged() {
        return logged;
    }
}
