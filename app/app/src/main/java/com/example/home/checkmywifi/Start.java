package com.example.home.checkmywifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
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

    @Click
    public void buttonStart() {
        ScanPhase_.intent(this)
                .start();
    }
}
