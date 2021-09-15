package com.cz.SarvodayaHBandroid.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;

import com.cz.SarvodayaHBandroid.R;


public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
//                TextView tx = (TextView)findViewById(R.id.splash_txt);
//
//                Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/chalkboard-bold.ttf");
//
//                tx.setTypeface(custom_font);
                boolean isLoggedIn = PreferencesHelper.getPreferenceBoolean(SplashActivity.this, PreferencesHelper.PREFERENCE_LOGGED_IN);
                if(isLoggedIn )
                {
                    Intent in1=new Intent(SplashActivity.this,MainActivity.class);
                    in1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in1);
                    finish();

                }
                else {
                    Intent in=new Intent(SplashActivity.this,LoginScreen.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);



    }
}
