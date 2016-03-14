package com.example.home.checkmywifi;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.HttpsClient;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Home on 10.03.2016.
 */
@EActivity(R.layout.scanning_phase)
public class ScanPhase extends AppCompatActivity {

    @ViewById(R.id.imageLogo)
    ImageView imageLogo;

    @ViewById(R.id.progressBar)
    ProgressBar progressBar;

    @ViewById(R.id.buttonStopScanning)
    Button stopButton;

    @ViewById(R.id.textRouter)
    TextView routerName;

    @ViewById(R.id.textDefault)
    TextView textDefault;

    @ViewById(R.id.textFirm)
    TextView textFirm;

    @ViewById(R.id.textEncrypt)
    TextView textEncrypt;

    @ViewById(R.id.textPercentage)
    TextView percentage;

    @ViewById(R.id.progressBarDefault)
    ProgressBar progressBarDefault;

    @ViewById(R.id.progressBarFirmVersion)
    ProgressBar progressBarFirm;

    @ViewById(R.id.progressBarEncryption)
    ProgressBar progressBarEncrypt;

    @AfterViews
    public void setupPercentage(){
        percentage.setText("0 %");
    }

    @AfterViews
    public void setupProgressBar(){
        progressBar.setProgress(0);
    }

    @Background
    public void connectToRouter() {
        MyBackgroundTask m = new MyBackgroundTask();
        m.connectToRouter();
        updateUi(m.getRouterName());

    }

    @UiThread
    protected void updateUi(String result){
        routerName.setText(result);
        progressBarDefault.setVisibility(View.GONE);
        textDefault.setTextColor(Color.parseColor("#CC2733"));

        for(int i = 0; i < 34; i++){
            progressBar.setProgress(i);
            percentage.setText(i + " %");
        }
    }

    @AfterViews
    public void setRouterName(){
        connectToRouter();
    }

    @Click
    public void buttonStopScanning() {
        Start_.intent(this)
                .start();
    }

}
