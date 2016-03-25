package com.example.home.checkmyrouter;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Home on 24.03.2016.
 */
public class BindingActivity extends Activity {
    private ScanService mService;
    private boolean mBound = false;

    private static final int ACCESS_FINE_LOCATION_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanning_phase);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Log.w("ONSTART", "✓");
        /*if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {*/
            Intent intent = new Intent(this, ScanService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);/*
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Access fine location required to access your wifi", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_RESULT);
        }*/
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

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.w("SERVICECONNECTED", "✓");
            ScanService.LocalBinder binder = (ScanService.LocalBinder) service;
            mService = binder.getService();
            mService.run();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}