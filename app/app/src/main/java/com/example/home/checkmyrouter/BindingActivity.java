package com.example.home.checkmyrouter;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Class BindingActivity activity that binds on a service
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public abstract class BindingActivity extends Activity implements ServiceConnection, ServiceManager.ServiceCallback {
    protected BindingActivity bContext = this;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    private static final int ACCESS_FINE_LOCATION_RESULT = 1;

    volatile boolean isWifiOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("BIND", "START");
        super.onCreate(savedInstanceState);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();

                NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                NetworkInfo.State state = info.getState();

                if (!(state == NetworkInfo.State.CONNECTED)|| !(info.getType() == ConnectivityManager.TYPE_WIFI)) {
                    Intent i = new Intent(BindingActivity.this, StateNoWifi.class);
                    startActivity(i);
                } else {
                    isWifiOn = true;
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w("Binding", "Spustam servicu ONStart");
        registerReceiver(receiver, intentFilter);

        /*Intent intent = new Intent(this, ScanService.class);
        bindService(intent, this, BIND_AUTO_CREATE);*/
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ScanService.class);
                bindService(intent, this, BIND_AUTO_CREATE);
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Access fine location required to access your wifi", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]permissions, int [] grantResults){
        if(requestCode == ACCESS_FINE_LOCATION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ScanService.class);
                bindService(intent, this, BIND_AUTO_CREATE);
            } else {
                Toast.makeText(this,
                        "Access fine location was not granted, app can not run", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    abstract void onServiceBound(ServiceManager service);

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(isWifiOn) {
            ServiceManager s = (ServiceManager) service;
            onServiceBound(s);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}