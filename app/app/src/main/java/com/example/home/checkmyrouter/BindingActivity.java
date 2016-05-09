package com.example.home.checkmyrouter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Class BindingActivity activity that binds on a service
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public abstract class BindingActivity extends Activity implements ServiceConnection, ServiceManager.ServiceCallback {
    protected BindingActivity bContext = this;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

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
        registerReceiver(receiver, intentFilter);
        Log.w("Binding", "Spustam servicu ONRESUME");
        Intent intent = new Intent(this, ScanService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
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