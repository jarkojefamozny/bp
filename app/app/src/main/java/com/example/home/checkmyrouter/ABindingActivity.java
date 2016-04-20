package com.example.home.checkmyrouter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by martin.sladecek on 20.4.2016.
 */
public abstract class ABindingActivity extends Activity implements ServiceConnection, IAService.IAServiceCallback {

    abstract void onServiceBound(IAService service);

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, AService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        IAService s = (IAService) service;
        onServiceBound(s);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
