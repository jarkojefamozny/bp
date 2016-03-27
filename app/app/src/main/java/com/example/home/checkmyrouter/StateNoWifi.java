package com.example.home.checkmyrouter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by Home on 26.03.2016.
 */
public class StateNoWifi extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_wifi);
    }

    public void onClick(View v) {
        Log.w("NOWIFI", "BUTOT");
        Intent i = new Intent(StateNoWifi.this, BindingActivity.class);
        startActivity(i);
    }
}
