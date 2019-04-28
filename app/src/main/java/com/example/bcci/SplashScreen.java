package com.example.bcci;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.hamza.slidingsquaresloaderview.SlidingSquareLoaderView;

public class SplashScreen extends Activity {

    public static final String MY_PREFS = "myPrefs";
    private static int timeout = 3000;
    SlidingSquareLoaderView slidingSquareLoaderView;
    SharedPreferences sharedPreferences;
    Boolean firstTime;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        sharedPreferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);

        slidingSquareLoaderView = findViewById(R.id.loader);
        slidingSquareLoaderView.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splash;
                firstTime = sharedPreferences.getBoolean("firstTime", true);
                if(firstTime){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    firstTime = false;
                    editor.putBoolean("firstTime", firstTime);
                    editor.apply();
                     splash = new Intent(SplashScreen.this, OnBoardActivity.class);
                    slidingSquareLoaderView.stop();
                    startActivity(splash);
                }
                else{
                    splash = new Intent(SplashScreen.this, MainActivity.class);
                    slidingSquareLoaderView.stop();
                    startActivity(splash);
                }
                finish();
            }
        }, timeout);
    }
}