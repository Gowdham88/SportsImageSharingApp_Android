package com.cz.SarvodayaHBandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.ui.Models.Users;
import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;
import com.cz.SarvodayaHBandroid.ui.view.RevealLoginView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

import com.cz.SarvodayaHBandroid.R;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by czltd on 12/21/17.
 */

public class LoginActivity extends BaseDrawerActivity implements RevealLoginView.OnStateChangeListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vloginRevealBackground)
    RevealLoginView vRevealBackground;
    @BindView(R.id.logo_img)
    ImageView ivLogo;
    @BindView(R.id.btnfb)
    Button btnFacebook;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private Button login;
    private CallbackManager mCallbackManager;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        setupRevealBackground(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        // [END initialize_auth]


    }

    @OnClick(R.id.btnfb)
    public void onTakePhotoClick(final View v) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        handleFacebookAccessToken(loginResult.getAccessToken(),v);
                    }

                    @Override
                    public void onCancel() {

                        Snackbar.make(v,"Facebook Login failed",Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Snackbar.make(v,"Facebook Login failed",Snackbar.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }




    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        final int[] startingLocation = new int[2];
        ivLogo.getLocationOnScreen(startingLocation);
        startingLocation[0] += ivLogo.getWidth() / 2;

            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });

    }

    @Override
    public void onStateChange(int state) {
        if (RevealLoginView.STATE_FINISHED == state) {
            ivLogo.setVisibility(View.VISIBLE);
            btnFacebook.setVisibility(View.VISIBLE);


            animateUserProfileHeader();
        } else {
            ivLogo.setVisibility(View.INVISIBLE);
            btnFacebook.setVisibility(View.INVISIBLE);

        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void handleFacebookAccessToken(final AccessToken token, final View v) {

        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            final FirebaseUser user = mAuth.getCurrentUser();

                            AddDatabase(user,v);


                        } else {
                            // If sign in fails, display a message to the user.
                            hideProgressDialog();
                            Snackbar.make(v,"Login failed",Snackbar.LENGTH_SHORT).show();

                        }

                    }
                });


    }

    private void animateUserProfileHeader() {
        ivLogo.setTranslationY(-ivLogo.getHeight());
        btnFacebook.setTranslationY(btnFacebook.getHeight());

        ivLogo.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        btnFacebook.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);


    }

    private void AddDatabase(final FirebaseUser user,final View v){


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Users users = new Users("",String.valueOf(user.getPhotoUrl()),user.getDisplayName());

        hideProgressDialog();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                if (documentSnapshot.exists()){

                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();

                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_EMAIL, user.getDisplayName());
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PROFILE_PIC, String.valueOf(user.getPhotoUrl()));
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID, user.getUid());

                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                } else {
                    db.collection("users").document(user.getUid())
                            .set(users)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Error", "Error adding document", e);
                                    hideProgressDialog();
                                    Snackbar.make(v,"Login failed",Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                            });

                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_EMAIL, user.getDisplayName());
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PROFILE_PIC, String.valueOf(user.getPhotoUrl()));
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID, user.getUid());

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.w("Error", "Error adding document", e);
                Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
