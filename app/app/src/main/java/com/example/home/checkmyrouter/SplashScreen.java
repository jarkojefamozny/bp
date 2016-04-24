package com.example.home.checkmyrouter;

/**
 * Created by Home on 24.03.2016.
 */
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Class SplashScreen is set to start application and to check permission.
 *
 * @author Jaroslav Bonco &lt;https://github.com/jarkojefamozny
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private static final int ACCESS_FINE_LOCATION_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                if(ContextCompat.checkSelfPermission(SplashScreen.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Log.w("SPLASH", "nepotrebujem ta");
                    startMain();
                } else {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)){
                        Toast.makeText(SplashScreen.this, "Access fine location required to access your wifi", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_RESULT);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[]permissions, int [] grantResults){
        if(requestCode == ACCESS_FINE_LOCATION_RESULT) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMain();
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

    private void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
