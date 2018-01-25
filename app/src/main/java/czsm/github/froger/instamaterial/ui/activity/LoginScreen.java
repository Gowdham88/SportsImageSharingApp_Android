package czsm.github.froger.instamaterial.ui.activity;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.Utils;
import czsm.github.froger.instamaterial.ui.Models.Post;
import czsm.github.froger.instamaterial.ui.Models.Users;
import czsm.github.froger.instamaterial.ui.utils.PreferencesHelper;

public class LoginScreen extends AppCompatActivity {
    EditText mEmailEdt,mPassEdt;
    Button mSininBtn;
    TextView mAccntTxt;
    Animation slideUpAnimation, slideDownAnimation;
    private FirebaseAuth mAuth;
    private android.support.v7.app.AlertDialog dialog;
    RelativeLayout Signinrel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mEmailEdt=(EditText)findViewById(czsm.github.froger.instamaterial.R.id.login_edt);
        mPassEdt=(EditText)findViewById(czsm.github.froger.instamaterial.R.id.loginpass_edt);
        mAccntTxt=(TextView) findViewById(czsm.github.froger.instamaterial.R.id.dont_txt);
        mSininBtn=(Button) findViewById(czsm.github.froger.instamaterial.R.id.sinin_edt);
        Signinrel=(RelativeLayout) findViewById(czsm.github.froger.instamaterial.R.id.signinrel_lay);
        Signinrel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(LoginScreen.this);
            }
        });

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
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.stay);

            }
        });

    }
    private static ObjectAnimator createBottomUpAnimation(View view,
                                                          AnimatorListenerAdapter listener, float distance) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", -distance);
//        animator.setDuration(???)
        animator.removeAllListeners();
        if (listener != null) {
            animator.addListener(listener);
        }
        return animator;
    }

    private void startBottomToTopAnimation(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_anim));
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

                            final FirebaseUser users = mAuth.getCurrentUser();
                            FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("users").document(users.getUid());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {


                                    if (documentSnapshot.exists()){

                                        Users user = documentSnapshot.toObject(Users.class);



                                        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_EMAIL,user.getuserName());
                                        PreferencesHelper.setPreferenceBoolean(getApplicationContext(), PreferencesHelper.PREFERENCE_LOGGED_IN,true);

                                        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PROFILE_PIC, user.getProfileImageURL());
                                        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID, users.getUid());

                                        Intent in=new Intent(LoginScreen.this,MainActivity.class);
                                        startActivity(in);

                                    } else {
                                        Toast.makeText(LoginScreen.this, "No user exits", Toast.LENGTH_LONG).show();

                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.w("Error", "Error adding document", e);
                                    Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                                }
                            });

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
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}