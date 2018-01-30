package czsm.github.froger.instamaterial.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import czsm.github.froger.instamaterial.R;

public class InfoActivity extends AppCompatActivity {
    @BindView(R.id.toolbar1)
    Toolbar toolbar;
    @BindView(R.id.back_image)
    ImageView backarrow;
    @BindView(R.id.info_txt)
    TextView Textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent infointent=new Intent(InfoActivity.this,ProfileActivity.class);
//                startActivity(infointent);
                onBackPressed();
            }
        });
    }
}
