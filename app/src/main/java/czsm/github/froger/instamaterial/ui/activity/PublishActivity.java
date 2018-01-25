package czsm.github.froger.instamaterial.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import czsm.github.froger.instamaterial.ui.view.LoadingFeedItemView;
import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.Utils;
import czsm.github.froger.instamaterial.ui.Models.Post;
import czsm.github.froger.instamaterial.ui.utils.PreferencesHelper;

/**
 * Created by Miroslaw Stanek on 21.02.15.
 */
public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @BindView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @BindView(R.id.tbDirect)
    ToggleButton tbDirect;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.etDescription)
    EditText edt_description;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;
    String postimageurl = "";
    String postcaption  = "";
    String PATH;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri,String path) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        intent.putExtra("Path",path);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        toolbar.setTitle("Post");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
            PATH     = getIntent().getStringExtra("Path");
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
            PATH     = getIntent().getStringExtra("Path");
        }
        updateStatusBarColor();

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            bringMainActivityToTop();

            return true;
        }
        else if(item.getItemId() == R.id.action_clear){
            Clear();

            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void Clear() {

        edt_description.setText("");
        Utils.hideKeyboard(PublishActivity.this);

    }

    private void bringMainActivityToTop() {
        Utils.hideKeyboard(PublishActivity.this);
        String caption =  edt_description.getText().toString();

        if(photoUri != null)
        {
            uploadImage();


        } else {



            if (caption != null && caption.length() > 0) {

                addPost(postimageurl,caption);


            } else {

                Toast.makeText(getApplicationContext(),"Post is Empty",Toast.LENGTH_SHORT).show();
            }


        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    @OnCheckedChanged(R.id.tbFollowers)
    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    @OnCheckedChanged(R.id.tbDirect)
    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    public void addPost(String imageurl,String caption) {

        String uid = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID);
        String userName = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_EMAIL);
        String profileImageURL = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_PROFILE_PIC);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Boolean> likeData = new HashMap<>();
        likeData.put(uid, false);

        Post post = new Post(uid,profileImageURL,userName,caption,imageurl,0,false,getPostTime(),likeData);

        db.collection("Post").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                LoadingFeedItemView view = new LoadingFeedItemView(getApplicationContext());
                view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                view.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
                    @Override
                    public void onLoadingFinished() {



                    }
                });
                view.startLoading();

                Intent intent = new Intent(PublishActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
                startActivity(intent);




            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
                        Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

                    }

        });


    }

    public String uploadImage() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if(photoUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            ivPhoto.setDrawingCacheEnabled(true);
            ivPhoto.buildDrawingCache();


            Uri file = Uri.fromFile(new File(PATH));
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
                            Toast.makeText(PublishActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            postimageurl = taskSnapshot.getDownloadUrl().toString();

                            String caption =  edt_description.getText().toString();

                            if (postimageurl.equals("failed")) {


                                return;
                            }

                            if (caption == null && caption.length() == 0) {

                                caption = "empty";

                            }

                            addPost(postimageurl,caption);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PublishActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public long getPostTime() {

        Date currentDate = new Date();
        long unixTime = currentDate.getTime() / 1000;
        return unixTime;


    }


}
