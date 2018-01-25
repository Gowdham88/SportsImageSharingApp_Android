package czsm.github.froger.instamaterial.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import czsm.github.froger.instamaterial.BuildConfig;
import czsm.github.froger.instamaterial.Constants;
import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.Utils;
import czsm.github.froger.instamaterial.ui.Models.Post;
import czsm.github.froger.instamaterial.ui.adapter.FeedAdapter;
import czsm.github.froger.instamaterial.ui.adapter.FeedItemAnimator;
import czsm.github.froger.instamaterial.ui.utils.PreferencesHelper;
import czsm.github.froger.instamaterial.ui.view.FeedContextMenu;
import czsm.github.froger.instamaterial.ui.view.FeedContextMenuManager;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class MainActivity extends AppCompatActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener,EasyPermissions.PermissionCallbacks {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    public static final String ACTION_SHOW_DEFAULT_ITEM = "action_show_default_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @BindView(R.id.content)
    CoordinatorLayout clContent;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.profile_image)
    ImageView profile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.back_image)
    ImageView backarrow;

    TextView Camera,Gallery,cancel;
    ImageView GalleryIcon, GenderDropimg;
    ImageView CameraIcon;
    private Uri fileUri;



    final private int RC_PICK_IMAGE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 1888;
    private static final int PERMISSIONS_REQUEST_GALLERY = 1889;
    private static final String[] CAMERA = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    ProgressDialog mProgressDialog;
    private String selectedImagePath = "";
    private String mCurrentPhotoPath;
    final private int RC_CAPTURE_IMAGE = 2;
    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;
    List<Post> postList = new ArrayList<Post>();
    List<String> postListId = new ArrayList<String>();
    DocumentSnapshot lastVisible = null;
    private int visibleThreshold = 1;
    private boolean isLoading=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        backarrow.setVisibility(View.GONE);
        loadPost(ACTION_SHOW_DEFAULT_ITEM);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        } else {
            feedAdapter.updateItems(false);
        }

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);

        rvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();



                if (!isLoading && totalItemCount <= (firstVisibleItemPosition + visibleItemCount)) {

                    if (lastVisible != null) {

                        Log.e("totalItemCount", String.valueOf(totalItemCount));
                        Log.e("visibleThreshold", String.valueOf(firstVisibleItemPosition + visibleItemCount));

                        loadMorePost();

                    }

                    isLoading = true;
                }




            }
        });


    }

    private void setupFeed() {


        feedAdapter = new FeedAdapter(this,postList);
        feedAdapter.setOnFeedItemClickListener(this);
        rvFeed.setAdapter(feedAdapter);
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
        rvFeed.setItemAnimator(new FeedItemAnimator());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {

            showFeedLoadingItemDelayed();

            loadPost(ACTION_SHOW_LOADING_ITEM);

        }
    }

    private void showFeedLoadingItemDelayed() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                rvFeed.smoothScrollToPosition(0);
//                feedAdapter.showLoadingView();
//            }
//        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        toolbarTitle.setTranslationY(-actionbarSize);
        profile.setTranslationY(-actionbarSize);
        backarrow.setTranslationX(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        toolbarTitle.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        backarrow.animate()
                .translationX(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500);
        profile.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
//        feedAdapter.updateItems(true);
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
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v, int position) {

        String name    = postList.get(position).getuserName();
        String profile = postList.get(position).getProfileImageURL();

        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this,postList.get(position).getUid(),name,profile);
        overridePendingTransition(0, 0);
    }



    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        if (hasPermissions()) {
//            captureImage();
            showBottomSheet();
        } else {
            EasyPermissions.requestPermissions(this, "Permissions required", PERMISSIONS_REQUEST_GALLERY, CAMERA);
        }
    }

//    @OnClick(R.id.back_image)
//    public void setBackarrow() {
//
//        finish();
//    }

    @OnClick(R.id.profile_image)
    public void profileArrow(View v) {

        String name = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_EMAIL);
        String profile = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_PROFILE_PIC);

        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        String uid = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID);
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this,uid,name,profile);
        overridePendingTransition(0, 0);
    }

    private boolean hasPermissions() {
        return EasyPermissions.hasPermissions(this, CAMERA);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        switch (requestCode) {

            case PERMISSIONS_REQUEST_CAMERA:

                break;

            case PERMISSIONS_REQUEST_GALLERY:
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RC_PICK_IMAGE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == RC_PICK_IMAGE) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), contentURI);
                        selectedImagePath = getRealPathFromURI(contentURI);
                        PublishActivity.openWithPhotoUri(this, contentURI,selectedImagePath);


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == RC_CAPTURE_IMAGE) {
                // Show the thumbnail on ImageView
//                showProgressDialog();
                Uri imageUri = Uri.parse(mCurrentPhotoPath);

               // ScanFile so it will be appeared on Gallery
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{imageUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                selectedImagePath = imageUri.getPath();

                PublishActivity.openWithPhotoUri(this, imageUri,selectedImagePath);

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

    public void loadPost(final String type) {

        postList.clear();
        postListId.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("Post")
                .orderBy("postTime", Query.Direction.DESCENDING)
                .limit(5);

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



                        if (type.equals(ACTION_SHOW_LOADING_ITEM)) {

                            feedAdapter.updateItems(true);

                        } else {


                            setupFeed();
                            feedAdapter.updateItems(true);
                        }


                    }

                });

    }

    public void loadMorePost() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("Post")
                .orderBy("postTime", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(5);

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
//                            Log.e("dbbd",document.getId());
//                            Log.e("dbbd", String.valueOf(document.getData()));

                        }

                        isLoading = false;

                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        feedAdapter.updateItems(false);

                    }

                });

    }


    public void updateLikeCount(int position,int likecount,Boolean likeData) {

        String uid = PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID);

        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference washingtonRef = db.collection("Post").document(this.postListId.get(position));

            washingtonRef
                    .update("likecount", likecount,"userlikes."+uid, likeData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

        } catch (NullPointerException e) {

            e.printStackTrace();
        }

    }

    public void getDocument(final int position, final String type) {

        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docRef = db.collection("Post").document(this.postListId.get(position));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {

                            Post post = document.toObject(Post.class);
                            checkUserLikes(position,post,type);


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


    public void checkUserLikes(int position,Post post,String type){

        int likecount = post.getLikecount();
        if (post.getUserlikes().size() > 0) {

            if (post.getUserlikes().get(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID)) != null){

                Boolean isLiked = post.getUserlikes().get(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID));

                if (isLiked) {

                    postList.get(position).setLikecount(likecount-1);

                } else {

                    postList.get(position).setLikecount(likecount+1);
                }

                Map<String, Boolean> likeData = new HashMap<>();
                for (Map.Entry<String, Boolean> entry : post.getUserlikes().entrySet())
                {
                    if (entry.getKey().equals(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID))){

                        likeData.put(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID), !isLiked);

                    } else {

                        likeData.put(entry.getKey(),entry.getValue());
                    }
                }

                for (Map.Entry<String, Boolean> entry : likeData.entrySet())
                {

                    Log.e("d"+entry.getKey(), String.valueOf(entry.getValue()));

                }


                postList.get(position).setUserlikes(likeData);
                feedAdapter.notifyItemChanged(position, type);
                updateLikeCount(position, postList.get(position).getLikecount(),!isLiked);

            } else {

                Map<String, Boolean> newlikeData = new HashMap<>();
                for (Map.Entry<String, Boolean> entry : post.getUserlikes().entrySet())
                {

                    newlikeData.put(entry.getKey(),entry.getValue());

                }

                newlikeData.put(PreferencesHelper.getPreference(this,PreferencesHelper.PREFERENCE_FIREBASE_UUID), true);

                for (Map.Entry<String, Boolean> entry : newlikeData.entrySet())
                {

                    Log.e("dsjdhsjdhj"+entry.getKey(), String.valueOf(entry.getValue()));

                }

                postList.get(position).setLikecount(likecount+1);
                postList.get(position).setUserlikes(newlikeData);
                feedAdapter.notifyItemChanged(position, type);
                updateLikeCount(position,postList.get(position).getLikecount(),true);

            }




        }


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

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });


    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RC_CAPTURE_IMAGE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return FileProvider.getUriForFile(MainActivity.this,
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


}