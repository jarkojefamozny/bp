package com.example.home.checkmyrouter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
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
public class TestPassword implements TestManager, Parcelable {
    private List<String> name;
    private List<String> pass;

    protected static ScanService sContext;

    private boolean defaultTest = true;
    private boolean testPassed = true;

    private static final String NAME = "Default password test";

    public TestPassword() {
    }

    @Override
    public String testName() {
        return NAME;
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
                "IP address " + getRouterIp() + ".\n" +
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
        Log.w("getAssets", "&#10003;");
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
                }
            }
        };

        Thread testPass = new Thread(r);
        testPass.start();
        return testPassed();
    }

    protected TestPassword(Parcel in) {
        if (in.readByte() == 0x01) {
            name = new ArrayList<String>();
            in.readList(name, String.class.getClassLoader());
        } else {
            name = null;
        }
        if (in.readByte() == 0x01) {
            pass = new ArrayList<String>();
            in.readList(pass, String.class.getClassLoader());
        } else {
            pass = null;
        }
        defaultTest = in.readByte() != 0x00;
        testPassed = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (name == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(name);
        }
        if (pass == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(pass);
        }
        dest.writeByte((byte) (defaultTest ? 0x01 : 0x00));
        dest.writeByte((byte) (testPassed ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TestPassword> CREATOR = new Parcelable.Creator<TestPassword>() {
        @Override
        public TestPassword createFromParcel(Parcel in) {
            return new TestPassword(in);
        }

        @Override
        public TestPassword[] newArray(int size) {
            return new TestPassword[size];
        }
    };
}
