package com.cz.SarvodayaHBandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cz.SarvodayaHBandroid.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by thulir on 9/10/17.
 */

public class MyActivity extends AppCompatActivity {

    private AlertDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

//    public void showProgressDialog() {
//        RelativeLayout layout = new RelativeLayout(this);
//        ProgressBar progressBar = new ProgressBar(MyActivity.this,null,android.R.attr.progressBarStyleLarge);
//        progressBar.setIndeterminate(true);
//        progressBar.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        layout.addView(progressBar,params);
//        layout.getRootView().setBackgroundResource(R.color.transparent);
//        setContentView(layout);
//    }
//
//    public void hideProgressDialog() {
//        setContentView(R.layout.activity_login);
//    }
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //View view = getLayoutInflater().inflate(R.layout.progress);
        alertDialog.setView(R.layout.progress);
        dialog = alertDialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public void hideProgressDialog(){
        if(dialog!=null)
        dialog.dismiss();
    }

    private void showSnackBar(String message){
        Snackbar.make(this.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_righ);
    }


}
