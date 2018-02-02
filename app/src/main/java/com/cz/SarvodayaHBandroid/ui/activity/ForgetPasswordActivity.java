package com.cz.SarvodayaHBandroid.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.cz.SarvodayaHBandroid.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    ImageView backBtn;
    LinearLayout cancelbtn;
    EditText emailEdit;
    Button resetBtn,resetbutton1;
    LinearLayout LinLay;
    TextView txt_error;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        emailEdit=(EditText)findViewById(R.id.email_edit);
        cancelbtn=(LinearLayout)findViewById(R.id.btncancel);
        resetBtn=(Button)findViewById(R.id.sinin_edt);
        resetbutton1=(Button)findViewById(R.id.sinin_edt1);
        txt_error = (TextView)findViewById(R.id.txt_error);
        LinLay=(LinearLayout)findViewById(R.id.const_lay);
//
        emailEdit.addTextChangedListener(mTextWatcher);
        checkFieldsForEmptyValues();
        emailEdit.setInputType(emailEdit.getInputType()
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                | EditorInfo.TYPE_TEXT_VARIATION_FILTER);

        LinLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ForgetPasswordActivity.this);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ForgetPasswordActivity.this);
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginScreen.class);
                startActivity(intent);
                finish();


            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ForgetPasswordActivity.this);
                String emailAddress = emailEdit.getText().toString();
                if (emailAddress.isEmpty()||!emailAddress.contains("@")||!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {

//                    showerror("invalid email address");
                    Toast.makeText(ForgetPasswordActivity.this, "inavalid email", Toast.LENGTH_SHORT).show();

                } else {
                    showProgressDialog();
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Popup();

//                                            Toast.makeText(getContext(), "Reset Successsfully", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, "Email sent.");
                                    }else {
//                                        showerror("Reset password failed.");
                                        Toast.makeText(ForgetPasswordActivity.this, "Reset password failed.", Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();

                                    }
                                }
                            });
                }
            }

        });
        resetbutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ForgetPasswordActivity.this);
                String emailAddress = emailEdit.getText().toString();
                if (emailAddress.isEmpty()||!emailAddress.contains("@")) {

//                    showerror("invalid email address");
                    Toast.makeText(ForgetPasswordActivity.this, "inavalid email", Toast.LENGTH_SHORT).show();

                } else {
                    showProgressDialog();
                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Popup();
//                                            Toast.makeText(getContext(), "Reset Successsfully", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, "Email sent.");
                                    }else {
//                                        showerror("Reset password failed.");
                                        Toast.makeText(ForgetPasswordActivity.this, "Reset password failed.", Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();

                                    }
                                }
                            });
                }
            }

        });

    }

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
    private void Popup() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.alert, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(deleteDialogView);
        Button ok = (Button)deleteDialogView.findViewById(R.id.ok_button);

        final AlertDialog alertDialog1 = alertDialog.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgetPasswordActivity.this,LoginScreen.class);
                startActivity(intent);
                finish();
                hideProgressDialog();
                alertDialog1.dismiss();
            }
        });


        alertDialog1.setCanceledOnTouchOutside(false);
        try {
            alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog1.show();
//        alertDialog1.getWindow().setLayout((int) Utils.convertDpToPixel(228,getActivity()),(int)Utils.convertDpToPixel(220,getActivity()));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog1.getWindow().getAttributes());
//        lp.height=200dp;
//        lp.width=228;
        lp.gravity = Gravity.CENTER;
//        lp.windowAnimations = R.style.DialogAnimation;
        alertDialog1.getWindow().setAttributes(lp);
    }
    private void checkFieldsForEmptyValues() {
        if ((TextUtils.isEmpty(emailEdit.getText()))){
            resetBtn.setVisibility(View.VISIBLE);
            resetBtn.setEnabled(false);
            resetbutton1.setVisibility(View.GONE);

        }
        else{
            resetbutton1.setVisibility(View.VISIBLE);
            resetBtn.setVisibility(View.GONE);
        }

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

}
