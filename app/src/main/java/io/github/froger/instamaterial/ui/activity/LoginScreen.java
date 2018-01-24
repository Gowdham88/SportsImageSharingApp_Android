package io.github.froger.instamaterial.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.froger.instamaterial.R;

import static io.github.froger.instamaterial.Utils.hideKeyboard;

public class LoginScreen extends AppCompatActivity {
    EditText mEmailEdt,mPassEdt;
    Button mSininBtn;
    TextView mAccntTxt;
    Animation slideUpAnimation, slideDownAnimation;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    RelativeLayout Signinrel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mEmailEdt=(EditText)findViewById(R.id.login_edt);
        mPassEdt=(EditText)findViewById(R.id.loginpass_edt);
        mAccntTxt=(TextView) findViewById(R.id.dont_txt);
        mSininBtn=(Button) findViewById(R.id.sinin_edt);
        Signinrel=(RelativeLayout) findViewById(R.id.signinrel_lay);
        Signinrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(LoginScreen.this);
            }
        });
        onBackPressed();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {

            currentUser.unlink(currentUser.getProviderId());
            mAuth.signOut();


        }
        mSininBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(mEmailEdt.getText().toString(), mPassEdt.getText().toString());
//                if(validateForm()){
////                    Toast.makeText(LoginScreen.this, "haii", Toast.LENGTH_SHORT).show();
////                    Intent in=new Intent(LoginScreen.this,MainActivity.class);
////                    startActivity(in);
//                }
            }
        });
        mAccntTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginScreen.this,SignupActivity.class);
                slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);
                mAccntTxt.setAnimation(slideUpAnimation);
//                slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
//                        R.anim.slide_down);
//                mAccntTxt.setAnimation(slideDownAnimation);
                startActivity(intent);
            }
        });
    }

    private void signIn(final String email, final String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginScreen.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent in=new Intent(LoginScreen.this,MainActivity.class);
                            startActivity(in);
                            // Sign in success, update UI with the signed-in user's information


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginScreen.this, "Registration failed! " + "\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                        }

//                        // [START_EXCLUDE]
//                        if (!task.isSuccessful()) {
//
//                            hideProgressDialog();
//                            showerror("Authentication failed.");
//                        }
                    }
                });

    }

    public void showProgressDialog() {


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginScreen.this);
        //View view = getLayoutInflater().inflate(R.layout.progress);
        alertDialog.setView(R.layout.progress);
        dialog = alertDialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    public void hideProgressDialog(){
        if(dialog!=null)
            dialog.dismiss();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailEdt.getText().toString();
        String password = mPassEdt.getText().toString();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            valid = true;

        } else {

            if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter email address and password.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            else if((email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()))
            {
                Toast.makeText(getApplicationContext(), "enter a valid email address", Toast.LENGTH_SHORT).show();
//            mEmail.setError("enter a valid email address");
                valid = false;
            }else if (TextUtils.isEmpty(password) || password.length()<4) {
                Toast.makeText(this, "Enter password.", Toast.LENGTH_SHORT).show();
                valid = false;
            } else {
                Toast.makeText(this, "Enter email address.", Toast.LENGTH_SHORT).show();
                valid = false;
            }


        }

        return valid;
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
