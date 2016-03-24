package com.example.home.checkmyrouter.old;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;

import com.example.home.checkmyrouter.old.Login_;
import com.example.home.checkmyrouter.R;
import com.example.home.checkmyrouter.old.Start_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Home on 10.03.2016.
 */
@EActivity(R.layout.scanning_phase)
public class ScanPhase extends AppCompatActivity {
    private String name = "";
    private String pass = "";
    private boolean loggedByLogin = false;

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

        if(!m.connectToRouter() && !loggedByLogin){
            loggedByLogin = true;
            Intent in = new Intent(this, Login_.class);
            startActivityForResult(in, 1);
        }

        Log.w("PASS", pass);
        m.loginToRouter(name, pass);

        Log.w("YOLO", m.getFirmVersion());
        updateUi(m.getRouterName(), m.getFirmVersion());

        /* mainWifiObj;
        mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo ();

        Log.w("WIFI", mainWifiObj.getConnectionInfo().toString());
        Log.w("WIFI", info.getSSID().toString());

        if( info != null )
        {
            WifiConfiguration activeConfig = null;
            for( WifiConfiguration conn : wifiManager.getConfiguredNetworks() )
            {
                if( conn.status == WifiConfiguration.Status.CURRENT )
                {
                    activeConfig = conn;
                    break;
                }
            }
            if( activeConfig != null )
            {
                Log.w("CONFIG", activeConfig.toString());
            }
        }*/
    }

    @UiThread
    protected void updateUi(String router, String firm){
        routerName.setText(router);

        progressBarDefault.setVisibility(View.GONE);
        if(loggedByLogin) {
            textDefault.setTextColor(Color.parseColor("#76BC3F"));
        } else {
            textDefault.setTextColor(Color.parseColor("#CC2733"));
        }

        for(int i = 0; i < 34; i++){
            progressBar.setProgress(i);
            percentage.setText(i + " %");
        }

        textFirm.setText("Firmware " + firm);
        progressBarFirm.setVisibility(View.GONE);
        textFirm.setTextColor(Color.parseColor("#D8D444"));

        for(int i = 34; i < 67; i++){
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                name = data.getStringExtra("name");
                pass = data.getStringExtra("pass");

                Log.w("Name", name);
                Log.w("PASS", pass);
                connectToRouter();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

}
