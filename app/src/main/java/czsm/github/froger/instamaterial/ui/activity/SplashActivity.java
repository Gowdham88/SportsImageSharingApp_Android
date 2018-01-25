package czsm.github.froger.instamaterial.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.ui.utils.PreferencesHelper;

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

                boolean isLoggedIn = PreferencesHelper.getPreferenceBoolean(SplashActivity.this,PreferencesHelper.PREFERENCE_LOGGED_IN);
                if(isLoggedIn )
                {
                    Intent in1=new Intent(SplashActivity.this,MainActivity.class);
                    in1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in1);

                }
                else {
                    Intent in=new Intent(SplashActivity.this,LoginScreen.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);

                }

            }
        }, SPLASH_DISPLAY_LENGTH);



    }
}
