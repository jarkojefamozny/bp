package com.example.home.checkmywifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_start)
public class Start extends AppCompatActivity {

    @ViewById(R.id.imageLogo)
    ImageView imageLogo;

    @ViewById(R.id.buttonStart)
    ImageButton buttonStart;

    @ViewById(R.id.textGit)
    TextView gitText;

    @ViewById(R.id.textGitPage)
    TextView gitPageText;
/*
    @Receiver(actions = ConnectivityManager.CONNECTIVITY_ACTION,
            registerAt = Receiver.RegisterAt.OnResumeOnPause)

    void onConnectivityChange() {
        NoWifi_.intent(this)
                .start();
    }*/

    @Click
    public void buttonStart() {
        Log.w("HALO", "ide to");
        ScanPhase_.intent(this)
                .start();
    }

}
