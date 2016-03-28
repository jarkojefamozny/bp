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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Home on 24.03.2016.
 */
public class BindingActivity extends Activity {
    private ServiceManager mService;
    private boolean mBound = false;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private BindingActivity bContext = this;

    protected TextView percentage;
    protected ProgressBar progressBar;

    private static final int ACCESS_FINE_LOCATION_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_phase);

        percentage = (TextView) findViewById(R.id.textPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();

                NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                NetworkInfo.State state = info.getState();

                if (!(state == NetworkInfo.State.CONNECTED)|| !(info.getType() == ConnectivityManager.TYPE_WIFI)) {
                    Intent i = new Intent(BindingActivity.this, StateNoWifi.class);
                    startActivity(i);
                }
            }
        };

        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) receiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        //Log.w("ONSTART", "✓");

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startScan();
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Access fine location required to access your wifi", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]permissions, int [] grantResults){
        if(requestCode == ACCESS_FINE_LOCATION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
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

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            //Log.w("SERVICECONNECTED", "✓");
            ScanService.LocalBinder binder = (ScanService.LocalBinder) service;
            //Log.w("BINDING", this.toString());
            mService = binder.getService();
            mService.setbContext(bContext);
            mService.run();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void startScan(){
        Intent intent = new Intent(this, ScanService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
}