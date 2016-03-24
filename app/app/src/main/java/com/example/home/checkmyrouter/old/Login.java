package com.example.home.checkmyrouter.old;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.home.checkmyrouter.R;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Home on 17.03.2016.
 */
@EActivity(R.layout.login)
public class Login extends AppCompatActivity {
    private boolean logged = false;

    @ViewById(R.id.imageLogo)
    ImageView logo;

    @ViewById(R.id.buttonToScan)
    ImageButton buttonToScan;

    @ViewById(R.id.textRouter)
    TextView routerName;

    @ViewById(R.id.textNoDefaultMessage)
    TextView textNoDefaultMessage;

    @ViewById(R.id.textContinueMessage)
    TextView textContinueMessage;

    @ViewById(R.id.editName)
    EditText editName;

    @ViewById(R.id.editPassword)
    EditText editPassword;

    @Background
    public void getLogin(){
        editName.performClick();
        editPassword.performClick();

        String name = editName.getText().toString();
        String pass = editPassword.getText().toString();
        Log.w("LOGIN", name + pass);

        MyBackgroundTask m = new MyBackgroundTask();
        if (m.checkLogin(name, pass)) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("name", name);
            returnIntent.putExtra("pass", pass);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        updateUi();
    }

    @UiThread
    protected void updateUi(){
        textNoDefaultMessage.setText("Can't login: username/password");
        textNoDefaultMessage.setTextColor(Color.parseColor("#CC2733"));
        textContinueMessage.setText("combination is incorrect!!");
        textContinueMessage.setTextColor(Color.parseColor("#CC2733"));
    }

    @Click
    public void buttonToScan() {
        if(!logged) {
            getLogin();
        }
    }
}
