package com.example.home.checkmyrouter.old;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.home.checkmyrouter.R;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
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
