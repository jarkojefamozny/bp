package com.example.home.checkmyrouter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Class StateNoWifi manages state when no wifi is available
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class StateNoWifi extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_wifi);
    }

    public void onClick(View v) {
        Log.w("BUTTON", "Clicked");
        Intent i = new Intent(StateNoWifi.this, MainActivity.class);
        startActivity(i);
    }
}
