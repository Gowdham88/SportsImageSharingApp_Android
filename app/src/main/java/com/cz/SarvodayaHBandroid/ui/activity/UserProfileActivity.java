package com.cz.SarvodayaHBandroid.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.BuildConfig;
import com.cz.SarvodayaHBandroid.Constants;
import com.cz.SarvodayaHBandroid.ui.Models.Post;
import com.cz.SarvodayaHBandroid.ui.adapter.FeedAdapter;
import com.cz.SarvodayaHBandroid.ui.adapter.UserAdapter;
import com.cz.SarvodayaHBandroid.ui.utils.CircleTransformation;
import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;
import com.cz.SarvodayaHBandroid.ui.view.FeedContextMenuManager;
import com.cz.SarvodayaHBandroid.ui.view.RevealBackgroundView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.cz.SarvodayaHBandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class UserProfileActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener,FeedAdapter.OnFeedItemClickListener, EasyPermissions.PermissionCallbacks {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    public static final String USER_ID = "user_id";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_NAME = "user_name";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.ivUserProfilePhoto)
    CircleImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.vUserStats)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    @BindView(R.id.username_txt)
    TextView vUserName;
//    @BindView(R.id.username_simple)
//    TextView vUserNameSub;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.logout_image)
    TextView logout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.back_image)
    ImageView backarrow;
    @BindView(R.id.edit_btn)
    Button EditProfileBtn;
    Boolean isboolean=false;
//    @BindView(R.id.count_txt)
//    TextView vpostCount;

//    @BindView(R.id.flwcount_txt)
//    TextView vfolowerCount;

    private int avatarSize;
    private String profilePhoto;
    String userId;
    String userName;
    String userProfile;
    private FirebaseAuth mAuth;
    UserAdapter adapter;
    Context context;
    ImageView editicon;
    TextView Camera,Gallery,cancel;
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
    boolean isPhotoValid = false;
    private String mCurrentPhotoPath;
    LinearLayout cancelLay;
    private android.support.v7.app.AlertDialog dialog;
//    private UserProfileAdapter userPhotosAdapter;

    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;
    List<Post> postList = new ArrayList<Post>();
    List<String> postListId = new ArrayList<String>();
    List<String> postCount  = new ArrayList<String>();
    List<String> userCount  = new ArrayList<String>();

    DocumentSnapshot lastVisible = null;
    private int visibleThreshold = 1;
    private boolean isLoading=true;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity,String Userid,String name,String image) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(USER_ID,Userid);
        intent.putExtra(USER_NAME,name);
        intent.putExtra(USER_IMAGE,image);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        mAuth = FirebaseAuth.getInstance();
        userId = getIntent().getStringExtra(USER_ID);
        userName = getIntent().getStringExtra(USER_NAME);
        userProfile = getIntent().getStringExtra(USER_IMAGE);
        editicon=(ImageView) findViewById(R.id.imageView_profile_edit);
        editicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
        this.profilePhoto = userProfile;
        vUserName.setText(userName);
//        vUserNameSub.setText("@"+userName);
        if(!profilePhoto.equals(null)&&!profilePhoto.isEmpty()){
            Picasso.with(this)
                    .load(profilePhoto)
                    .resize(avatarSize, avatarSize)
                    .centerCrop()
                    .transform(new CircleTransformation())
                    .into(ivUserProfilePhoto);
        }
        else{
//            Picasso.with(this)
//                    .load(profilePhoto)
//                    .placeholder(R.drawable.background)
//                    .resize(avatarSize, avatarSize)
//                    .centerCrop()
//                    .transform(new CircleTransformation())
//                    .into(ivUserProfilePhoto);
        }

        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        loadPost();

//        PostCount();
//        UserCount();


        EditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUp();
            }
        });
    }

    private void showBottomSheet() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserProfileActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        View bottomSheetView = factory.inflate(R.layout.dialo_camera_bottomsheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        Camera = (TextView) bottomSheetView.findViewById(R.id.camera_title);
        Gallery = (TextView) bottomSheetView.findViewById(R.id.gallery_title);
        cancel = (TextView)bottomSheetView.findViewById(R.id.cancel_txt);
        cancelLay = (LinearLayout) bottomSheetView.findViewById(R.id.cance_lay);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog.dismiss();

                if (hasPermissions()) {
                    captureImage();
                } else {
                    EasyPermissions.requestPermissions(UserProfileActivity.this, "Permissions required", PERMISSIONS_REQUEST_CAMERA, CAMERA);
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
                    EasyPermissions.requestPermissions(UserProfileActivity.this, "Permissions required", PERMISSIONS_REQUEST_GALLERY, CAMERA);
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
        cancelLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        switch (requestCode){

            case PERMISSIONS_REQUEST_GALLERY:
                if(perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)&&perms.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RC_PICK_IMAGE);
                }
                break;

            case PERMISSIONS_REQUEST_CAMERA:
                if(perms.contains(Manifest.permission.CAMERA)) {
                    captureImage();
                }
                break;

        }


    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }



    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(UserProfileActivity.this, CAMERA);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RC_CAPTURE_IMAGE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return FileProvider.getUriForFile(getApplicationContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Constants.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + mediaFile.getAbsolutePath();

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        showProgressDialog();
        if(requestCode==5555){
            loadPost();
        }
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == RC_PICK_IMAGE) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    isPhotoValid = true;
                    this.contentURI = contentURI;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), contentURI);
                        ivUserProfilePhoto.setImageBitmap(bitmap);
                        selectedImagePath=getRealPathFromURI(contentURI);
                        uploadImage(getRealPathFromURI(contentURI));

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == RC_CAPTURE_IMAGE) {
                // Show the thumbnail on ImageView
//                showProgressDialog();
                Uri imageUri = Uri.parse(mCurrentPhotoPath);
                this.contentURI = imageUri;
                isPhotoValid = true;
                File file = new File(imageUri.getPath());
                try {
                    InputStream ims = new FileInputStream(file);
                    ivUserProfilePhoto.setImageBitmap(BitmapFactory.decodeStream(ims));
                } catch (FileNotFoundException e) {
                    return;
                }

                // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                selectedImagePath = imageUri.getPath();
                uploadImage(imageUri.getPath());

            }

        } else {
            super.onActivityResult(requestCode, resultCode,
                    data);
        }
    }



    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    public void uploadImage(String realPathFromURI) {

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
                            Toast.makeText(UserProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            postimageurl = taskSnapshot.getDownloadUrl().toString();

                            if (postimageurl.equals("failed")) {


                                return;
                            }
                            final FirebaseUser user = mAuth.getCurrentUser();
//                            AddDatabase(user,view);
                            saveUserImage(postimageurl);



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

            return ;

        }



    }

    private void PopUp() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View deleteDialogView = factory.inflate(R.layout.edit_alert, null);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(deleteDialogView);
        Button ok = (Button)deleteDialogView.findViewById(R.id.ok_button);
        Button cancel = (Button)deleteDialogView.findViewById(R.id.cancel_button);
        final EditText EdtPfl = (EditText)deleteDialogView.findViewById(R.id.profile_edt);
        EdtPfl.setText(userName);
        final AlertDialog alertDialog1 = alertDialog.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Str=EdtPfl.getText().toString();
                if(!Str.isEmpty()||!Str.equals(null)){
                    saveUserName(Str,alertDialog1);
                }else{
                    Toast.makeText(UserProfileActivity.this, "Please edit the name", Toast.LENGTH_SHORT).show();
                }
//                Intent intent = new Intent(UserProfileActivity.this,LoginScreen.class);
//                startActivity(intent);
                alertDialog1.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupFeed() {
        final GridLayoutManager GridLayoutManager = new GridLayoutManager(UserProfileActivity.this,3) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = 3;
        rvUserProfile.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new UserAdapter(UserProfileActivity.this,postList);
        adapter.notifyDataSetChanged();
//        adapter.setOnFeedItemClickListener(UserProfileActivity.this);
        rvUserProfile.setAdapter(adapter);
//        feedAdapter = new FeedAdapter(this,postList);
//        feedAdapter.setOnFeedItemClickListener(UserProfileActivity.this);
//        rvUserProfile.setAdapter(feedAdapter);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
//        rvUserProfile.setItemAnimator(new FeedItemAnimator());
//        rvUserProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int visibleItemCount = GridLayoutManager.getChildCount();
//                int totalItemCount = GridLayoutManager.getItemCount();
//                int firstVisibleItemPosition = GridLayoutManager.findLastVisibleItemPosition();
//
//
//
//                if (!isLoading && totalItemCount <= (firstVisibleItemPosition + visibleItemCount)) {
//
//                    if (lastVisible != null) {
//
//                        Log.e("totalItemCount", String.valueOf(totalItemCount));
//                        Log.e("visibleThreshold", String.valueOf(firstVisibleItemPosition + visibleItemCount));
//
////                        loadMorePost();
//
//                    }
//
//                    isLoading = true;
//                }
//
//
//
//
//            }
//        });
    }
    public void intialpopup(int position) {
            Intent intent=new Intent(UserProfileActivity.this, ImageDetailActivity.class);
            intent.putExtra("imagepath",postList.get(position).getPhotoURL());
            intent.putExtra("name",isLoading);
            intent.putExtra("postid",postListId.get(position));
            startActivityForResult(intent,5555);
//            ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);

    }
    private void setupUserProfileGrid() {
//        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        rvUserProfile.setLayoutManager(layoutManager);
//        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                userPhotosAdapter.setLockedAnimations(true);
//            }
//        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
//            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
//            userPhotosAdapter = new UserProfileAdapter(this);
//            rvUserProfile.setAdapter(userPhotosAdapter);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());
           vUserStats.setTranslationY(-vUserStats.getHeight());

           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           vUserStats.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);;
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(300).setInterpolator(INTERPOLATOR);


    }

    @Override
    public void onCommentsClick(View v, int position) {

        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        intent.putExtra(CommentsActivity.POST_ID, postListId.get(position));
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    @Override
    public void onMoreClick(View v, int position) {

    }

    @Override
    public void onProfileClick(View v, int position) {

    }

    @OnClick(R.id.back_image)
    public void setBackarrow() {

       onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick(R.id.logout_image)
    public void logout() {

        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        startActivity(intent);
        PreferencesHelper.signOut(UserProfileActivity.this);
        mAuth.signOut();
        finishAffinity();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    public void loadPost() {

        postList.clear();
        postListId.clear();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("posts").whereEqualTo("uid", userId);

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocuments().size() < 1) {

                            return;

                        }

                        for(DocumentSnapshot document : documentSnapshots.getDocuments()) {

                            Post post = document.toObject(Post.class);
                            postList.add(post);
                            postListId.add(document.getId());
                            Log.e("dbbd",document.getId());
                            Log.e("dbbd", String.valueOf(document.getData()));

                        }

                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                            setupFeed();
//                            adapter.notifyDataSetChanged();
//                            UserAdapter.updateItems(true);F


                    }

                });

    }

//    public void loadMorePost() {
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//
//        Query first = db.collection("Post").whereEqualTo("uid", userId)
//                .startAfter(lastVisible)
//                .limit(5);
//
//        first.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot documentSnapshots) {
//
//                        if (documentSnapshots.getDocuments().size() < 1) {
//
//                            return;
//
//                        }
//
//                        for(DocumentSnapshot document : documentSnapshots.getDocuments()) {
//
//                            Post post = document.toObject(Post.class);
//                            postList.add(post);
//                            postListId.add(document.getId());
//                            Log.e("dbbd",document.getId());
//                            Log.e("dbbd", String.valueOf(document.getData()));
//
//                        }
//
//                        isLoading = false;
//
//                        lastVisible = documentSnapshots.getDocuments()
//                                .get(documentSnapshots.size() -1);
//
//                        feedAdapter.updateItems(false);
//
//                    }
//
//                });
//
//    }

//    public void updateLikeCount(int position,int likecount,Boolean likeData) {
//
//        String uid = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID);
//
//        try {
//
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            DocumentReference washingtonRef = db.collection("Post").document(this.postListId.get(position));
//
//            washingtonRef
//                    .update("likecount", likecount,"userlikes."+uid, likeData)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "DocumentSnapshot successfully updated!");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error updating document", e);
//                        }
//                    });
//
//        } catch (NullPointerException e) {
//
//            e.printStackTrace();
//        }
//
//    }

    public void getDocument(final int position, final String type) {

        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docRef = db.collection("posts").document(this.postListId.get(position));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            Post post = document.toObject(Post.class);
//                            checkUserLikes(position,post,type);


                        } else {
                            Log.d("Doc", "No such document");
                        }
                    } else {
                        Log.d("Doc", "get failed with ", task.getException());
                    }
                }
            });

        }catch (NullPointerException e) {

            e.printStackTrace();
        }

    }



//    public void checkUserLikes(int position,Post post,String type){
//
//        int likecount = post.getLikecount();
//        if (post.getUserlikes().size() > 0) {
//
//            if (post.getUserlikes().get(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID)) != null){
//
//                Boolean isLiked = post.getUserlikes().get(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID));
//
//                if (isLiked) {
//
//                    postList.get(position).setLikecount(likecount-1);
//
//                } else {
//
//                    postList.get(position).setLikecount(likecount+1);
//                }
//
//                Map<String, Boolean> likeData = new HashMap<>();
//                for (Map.Entry<String, Boolean> entry : post.getUserlikes().entrySet())
//                {
//                    if (entry.getKey().equals(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID))){
//
//                        likeData.put(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID), !isLiked);
//
//                    } else {
//
//                        likeData.put(entry.getKey(),entry.getValue());
//                    }
//                }
//
//                for (Map.Entry<String, Boolean> entry : likeData.entrySet())
//                {
//
//                    Log.e("d"+entry.getKey(), String.valueOf(entry.getValue()));
//
//                }
//
//
//                postList.get(position).setUserlikes(likeData);
//                feedAdapter.notifyItemChanged(position, type);
////                updateLikeCount(position, postList.get(position).getLikecount(),!isLiked);
//
//            } else {
//
//                Map<String, Boolean> newlikeData = new HashMap<>();
//                for (Map.Entry<String, Boolean> entry : post.getUserlikes().entrySet())
//                {
//
//                    newlikeData.put(entry.getKey(),entry.getValue());
//
//                }
//
//                newlikeData.put(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID), true);
//
//                for (Map.Entry<String, Boolean> entry : newlikeData.entrySet())
//                {
//
//                    Log.e("dsjdhsjdhj"+entry.getKey(), String.valueOf(entry.getValue()));
//
//                }
//
//                postList.get(position).setLikecount(likecount+1);
//                postList.get(position).setUserlikes(newlikeData);
//                feedAdapter.notifyItemChanged(position, type);
////                updateLikeCount(position,postList.get(position).getLikecount(),true);
//
//            }
//
//
//
//
//        }
//
//
//    }
    public void saveUserName(final String name, final AlertDialog dialog) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("users").document(userId);


        ref.update("username", name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        vUserName.setText(name);
                        Toast.makeText(UserProfileActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_EMAIL, name);
                        dialog.dismiss();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(UserProfileActivity.this,"Update failed",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                });

    }

    public void saveUserImage(final String postimageurl) {
        showProgressDialog();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("users").document(userId);


        ref.update("profileImageURL", postimageurl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        if(!postimageurl.equals(null)&&!postimageurl.isEmpty()){
                            Picasso.with(UserProfileActivity.this)
                                    .load(postimageurl)
                                    .resize(avatarSize, avatarSize)
                                    .centerCrop()
                                    .transform(new CircleTransformation())
                                    .into(ivUserProfilePhoto);
                        }
                        else{
//            Picasso.with(this)
//                    .load(profilePhoto)
//                    .placeholder(R.drawable.background)
//                    .resize(avatarSize, avatarSize)
//                    .centerCrop()
//                    .transform(new CircleTransformation())
//                    .into(ivUserProfilePhoto);
                        }
                        Toast.makeText(UserProfileActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                        PreferencesHelper.setPreference(getApplicationContext(), PreferencesHelper.PREFERENCE_PROFILE_PIC, postimageurl);
            hideProgressDialog();
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
                Toast.makeText(UserProfileActivity.this,"Update failed",Toast.LENGTH_SHORT).show();
                hideProgressDialog();
//                dialog.dismiss();
            }

        });

    }

    public void PostCount() {

    postCount.clear();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Query first = db.collection("posts");

    first.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {

                    if (documentSnapshots.getDocuments().size() < 1) {

                        return;

                    }

                    for (DocumentSnapshot document : documentSnapshots.getDocuments()) {

                        postCount.add(document.getId());

                    }

//                    vpostCount.setText("" + postCount.size());


                }


            });

}

    public void UserCount() {

        userCount.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("users");

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocuments().size() < 1) {

                            return;

                        }

                        for(DocumentSnapshot document : documentSnapshots.getDocuments()) {

                          userCount.add(document.getId());


                        }

//                        vfolowerCount.setText(""+userCount.size());

                    }

                });

    }
    public void showProgressDialog() {


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(UserProfileActivity.this);
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


}


