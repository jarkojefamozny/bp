package com.example.home.checkmyrouter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public class WifiStateManager {

    boolean isSecure(Context context) {
        ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getScanResults();
        return true;
    }

}
