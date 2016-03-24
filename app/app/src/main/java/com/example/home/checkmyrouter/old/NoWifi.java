package com.example.home.checkmyrouter.old;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.home.checkmyrouter.R;
import com.example.home.checkmyrouter.old.Start_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Home on 15.03.2016.
 */
@EActivity(R.layout.no_wifi)
public class NoWifi extends AppCompatActivity {
    @ViewById(R.id.imageLogo)
    ImageView imageLogo;

    @ViewById(R.id.textNoWifi)
    TextView textNoWifi;

    @ViewById(R.id.textNoWifiMessage)
    TextView textNoWifiMsg;

    @ViewById(R.id.buttonNoWifi)
    ImageButton buttonNoWifi;

    @Click
    public void buttonStart() {
        Log.w("NOWIFI", "STLACIL");
        Start_.intent(this)
                .start();
    }
}
