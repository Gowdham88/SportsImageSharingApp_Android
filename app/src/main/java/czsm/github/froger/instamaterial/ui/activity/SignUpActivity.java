package czsm.github.froger.instamaterial.ui.activity;

import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;
import java.util.zip.Inflater;

import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.ui.Models.Users;
import czsm.github.froger.instamaterial.ui.utils.PreferencesHelper;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

import static czsm.github.froger.instamaterial.Utils.hideKeyboard;

public class SignUpActivity extends AppCompatActivity {
    EditText EmailEdt,UsernameEdt,PassEdt;
    Button SignupBtn;
    TextView AccntTxt;
    CircleImageView profileImg;
    TextView Camera,Gallery,cancel;
    ImageView GalleryIcon, GenderDropimg;
    ImageView CameraIcon;
    Inflater inflater;
    RelativeLayout Signuprellay;
    Animation slideUpAnimation, slideDownAnimation;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private static final int PERMISSIONS_REQUEST_CAMERA = 1888;
    private static final int PERMISSIONS_REQUEST_GALLERY = 1889;
    private static final String[] CAMERA = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private String selectedImagePath = "";
    final private int RC_PICK_IMAGE = 1;
    final private int RC_CAPTURE_IMAGE = 2;
    private Uri fileUri;
    Uri imageUri;
    String postimageurl = "";
    Uri contentURI;
    private String mCurrentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        EmailEdt=(EditText)findViewById(R.id.email_edit);
        UsernameEdt=(EditText)findViewById(R.id.username_edit);
        PassEdt=(EditText)findViewById(R.id.pass_edit);
        AccntTxt=(TextView)findViewById(R.id.accnt_txt);
        Signuprellay=(RelativeLayout) findViewById(R.id.signuprel_lay);
        Signuprellay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SignUpActivity.this);
            }
        });
        SignupBtn=(Button)findViewById(R.id.signup_btn);
        profileImg=(CircleImageView)findViewById(R.id.image_view);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {

            currentUser.unlink(currentUser.getProviderId());
            LoginManager.getInstance().logOut();
            mAuth.signOut();

        }
        SignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SignUpActivity.this);
                createAccount(EmailEdt.getText().toString(), UsernameEdt.getText().toString(),PassEdt.getText().toString(),view);

                if(validateForm()){
                    Intent in=new Intent(SignUpActivity.this,LoginScreen.class);
                    startActivity(in);
                }
            }
        });
        AccntTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),LoginScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }

    private void showBottomSheet() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        View bottomSheetView = factory.inflate(R.layout.dialo_camera_bottomsheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        Camera = (TextView) bottomSheetView.findViewById(R.id.camera_title);
        Gallery = (TextView) bottomSheetView.findViewById(R.id.gallery_title);
//        GalleryIcon = (ImageView) bottomSheetView.findViewById(R.id.gallery_icon);
//        CameraIcon = (ImageView) bottomSheetView.findViewById(R.id.camera_image);
        cancel=(TextView) bottomSheetView.findViewById(R.id.cancel_txt);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();

                if (hasPermissions()) {
                    captureImage();
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, "Permissions required", PERMISSIONS_REQUEST_CAMERA, CAMERA);
                }
            }
        });

        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions()) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RC_PICK_IMAGE);
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, "Permissions required", PERMISSIONS_REQUEST_GALLERY, CAMERA);
                }
                bottomSheetDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


    }

    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, CAMERA);
    }

    private void createAccount(final String email, final String username, final String password,final  View view) {

        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
//                                                Intent in=new Intent(SignupActivity.this,LoginScreen.class);
//                                                startActivity(in);
                                                final FirebaseUser user = mAuth.getCurrentUser();
                                                Log.e("user", String.valueOf(user));
//                                                AddDatabase(user,view);
                                                uploadImage(view);
                                                hideProgressDialog();
                                                // Sign in success, update UI with the signed-in user's information
                                            }
                                        }
                                    });


                        }else{
                            Toast.makeText(getApplicationContext(), "Registration failed! " + "\n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            hideProgressDialog();
                            // If sign in fails, display a message to the user.



//                                showerror(" email is already exists");

                        }
                    }

                });

    }
    public String uploadImage(final View view) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if(contentURI != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

//            ivPhoto.setDrawingCacheEnabled(true);
//            ivPhoto.buildDrawingCache();


            Uri file = Uri.fromFile(new File(selectedImagePath));
            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
//            uploadTask = riversRef.putFile(file);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = storageRef.child("images/"+ UUID.randomUUID().toString()+".jpg");
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            postimageurl = taskSnapshot.getDownloadUrl().toString();

//                            String caption =  edt_description.getText().toString();

                            if (postimageurl.equals("failed")) {


                                return;
                            }
                            final FirebaseUser user = mAuth.getCurrentUser();
                            AddDatabase(user,view);
//                            if (caption == null && caption.length() == 0) {
//
//                                caption = "empty";
//
//                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            postimageurl = "failed";
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        } else {

            return "empty";

        }

        return "empty";

    }

    public static ObjectAnimator createTopDownAnimation(View view, AnimatorListenerAdapter listener,
                                                        float distance) {
        view.setTranslationY(-distance);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 0);
        animator.removeAllListeners();
        if (listener != null) {
            animator.addListener(listener);
        }
        return animator;
    }

    public void showProgressDialog() {


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(SignUpActivity.this);
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

        String email = EmailEdt.getText().toString();
        String username = UsernameEdt.getText().toString();
        String password = PassEdt.getText().toString();
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
            }else if (username.isEmpty()&&username.equals(null)) {
                Toast.makeText(this, "Enter username.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            else if (TextUtils.isEmpty(password) || password.length()<4) {
                Toast.makeText(this, "Enter password.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            else if (mCurrentPhotoPath.equals(null)) {
                Toast.makeText(this, "" +
                        "please fill the image", Toast.LENGTH_SHORT).show();
                valid = false;
            }

            else {
                Toast.makeText(this, "Enter email address.", Toast.LENGTH_SHORT).show();
                valid = false;
            }


        }

        return valid;
    }
    private void AddDatabase(final FirebaseUser user, final View view){



        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Users users = new Users("",String.valueOf(postimageurl),UsernameEdt.getText().toString());
        hideProgressDialog();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                if (documentSnapshot.exists()){

                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();

                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_EMAIL,UsernameEdt.getText().toString());
                    PreferencesHelper.setPreferenceBoolean(getApplicationContext(), PreferencesHelper.PREFERENCE_LOGGED_IN,true);

                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PROFILE_PIC, String.valueOf(postimageurl));
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_FIREBASE_UUID, user.getUid());

                    final Intent intent = new Intent(SignUpActivity.this, LoginScreen.class);
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
//                                    Toast.makeText(SignupActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(view,"Login failed",Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                            });

                    final Intent intent = new Intent(SignUpActivity.this, LoginScreen.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                    PreferencesHelper.setPreferenceBoolean(getApplicationContext(), PreferencesHelper.PREFERENCE_LOGGED_IN,true);
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_EMAIL,UsernameEdt.getText().toString());
                    PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PROFILE_PIC, String.valueOf(postimageurl));
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
