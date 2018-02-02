package com.cz.SarvodayaHBandroid.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cz.SarvodayaHBandroid.R;

public class OnBoardingActivity extends AppCompatActivity {
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        btn=(Button)findViewById(R.id.button_letme);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OnBoardingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
