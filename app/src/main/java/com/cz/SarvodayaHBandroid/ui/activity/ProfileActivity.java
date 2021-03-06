package com.cz.SarvodayaHBandroid.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cz.SarvodayaHBandroid.ui.Models.Comment;
import com.cz.SarvodayaHBandroid.ui.Models.Post;
import com.cz.SarvodayaHBandroid.ui.adapter.CommentsAdapter;
import com.cz.SarvodayaHBandroid.ui.adapter.FeedAdapter;
import com.cz.SarvodayaHBandroid.ui.adapter.FeedItemAnimator;
import com.cz.SarvodayaHBandroid.ui.utils.CircleTransformation;
import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;
import com.cz.SarvodayaHBandroid.ui.view.FeedContextMenu;
import com.cz.SarvodayaHBandroid.ui.view.FeedContextMenuManager;
import com.cz.SarvodayaHBandroid.ui.view.RevealBackgroundView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cz.SarvodayaHBandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class ProfileActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener,FeedAdapter.OnFeedItemClickListener,FeedContextMenu.OnFeedContextMenuItemClickListener  {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    public static final String USER_ID = "user_id";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_NAME = "user_name";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vRevealBackground1)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile1)
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs1)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.ivUserProfilePhoto1)
    CircleImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails1)
    View vUserDetails;
    @BindView(R.id.btnFollow1)
    Button btnFollow;
    @BindView(R.id.vUserStats1)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot1)
    View vUserProfileRoot;
    @BindView(R.id.username_txt1)
    TextView vUserName;
    @BindView(R.id.username_simple1)
    TextView vUserNameSub;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.logout_image)
    TextView logout;
    @BindView(R.id.toolbar1)
    Toolbar toolbar;
    @BindView(R.id.back_image)
    ImageView backarrow;

    private int avatarSize;
    private String profilePhoto;
    String userId;
    String userName;
    String userProfile;

    private FirebaseAuth mAuth;
//    private UserProfileAdapter userPhotosAdapter;

    private FeedAdapter feedAdapter;

    private boolean pendingIntroAnimation;
    List<Post> postList = new ArrayList<Post>();
    List<String> postListId = new ArrayList<String>();
    List<Post> postItem = new ArrayList<Post>();

    DocumentSnapshot lastVisible = null;
    private int visibleThreshold = 1;
    private boolean isLoading=false;

    public static void startProfileUserProfileFromLocation(int[] startingLocation, Activity startingActivity,String Userid,String name,String image) {
        Intent intent = new Intent(startingActivity, ProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(USER_ID,Userid);
        intent.putExtra(USER_NAME,name);
        intent.putExtra(USER_IMAGE,image);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_redirect);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);

        mAuth = FirebaseAuth.getInstance();
        userId = getIntent().getStringExtra(USER_ID);
        userName = getIntent().getStringExtra(USER_NAME);
        userProfile = getIntent().getStringExtra(USER_IMAGE);

        this.profilePhoto = userProfile;
        vUserName.setText(userName);
        vUserNameSub.setText("@"+userName);
        if(!profilePhoto.equals(null)&&!profilePhoto.isEmpty()){
//            Picasso.with(this)
//                    .load(profilePhoto).rotate(90)
//                    .resize(avatarSize, avatarSize)
//                    .centerCrop()
//                    .transform(new CircleTransformation())
//                    .into(ivUserProfilePhoto);
            Glide.with(this)
                    .load(profilePhoto)
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

    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupFeed() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvUserProfile.setLayoutManager(linearLayoutManager);
        Collections.sort(postList, new Comparator<Post>() {
            @Override
            public int compare(Post lhs, Post rhs) {
                return String.valueOf(rhs.getPostTime()).compareTo(String.valueOf(lhs.getPostTime()));
            }

        });
        feedAdapter = new FeedAdapter(this,postList);
        rvUserProfile.setAdapter(feedAdapter);
        feedAdapter.setOnFeedItemClickListener(ProfileActivity.this);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
        rvUserProfile.setItemAnimator(new FeedItemAnimator());
        rvUserProfile.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        vUserStats.setAlpha(0);

        vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
        ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
        vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
        vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
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

        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);
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
        PreferencesHelper.signOut(ProfileActivity.this);
        mAuth.signOut();
        finishAffinity();
    }



    public void loadPost() {

        postList.clear();
        postListId.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("posts").whereEqualTo("uid", userId).limit(5);

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
                        feedAdapter.updateItems(true);


                    }

                });

    }

    public void loadMorePost() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("posts").whereEqualTo("uid", userId)
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
                            Log.e("dbbd",document.getId());
                            Log.e("dbbd", String.valueOf(document.getData()));

                        }

                        isLoading = false;

                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() -1);

                        feedAdapter.updateItems(false);

                    }

                });

    }

    public void updateLikeCount(int position,int likecount,Boolean likeData) {

        String uid = PreferencesHelper.getPreference(this, PreferencesHelper.PREFERENCE_FIREBASE_UUID);

        try {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference washingtonRef = db.collection("posts").document(this.postListId.get(position));

            washingtonRef
                    .update("likeCount", likecount,"likes."+uid, likeData)
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

            DocumentReference docRef = db.collection("posts").document(this.postListId.get(position));
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


    public void checkUserLikes(int position, Post post, String type) {

        int likecount = post.getLikeCount();
        if (post.getLikes().size() > 0) {

            if (post.getLikes().get(PreferencesHelper.getPreference(this, PreferencesHelper.PREFERENCE_FIREBASE_UUID)) != null){

                Boolean isLiked = post.getLikes().get(PreferencesHelper.getPreference(this, PreferencesHelper.PREFERENCE_FIREBASE_UUID));

                if (isLiked) {

                    postList.get(position).setLikeCount(likecount-1);

                } else {

                    postList.get(position).setLikeCount(likecount+1);
                }

                Map<String, Boolean> likeData = new HashMap<>();
                for (Map.Entry<String, Boolean> entry : post.getLikes().entrySet())
                {
                    if (entry.getKey().equals(PreferencesHelper.getPreference(this, PreferencesHelper.PREFERENCE_FIREBASE_UUID))){

                        likeData.put(PreferencesHelper.getPreference(this, PreferencesHelper.PREFERENCE_FIREBASE_UUID), !isLiked);

                    } else {

                        likeData.put(entry.getKey(),entry.getValue());
                    }
                }

                for (Map.Entry<String, Boolean> entry : likeData.entrySet())
                {

                    Log.e("d"+entry.getKey(), String.valueOf(entry.getValue()));

                }


                postList.get(position).setLikes(likeData);
                feedAdapter.notifyItemChanged(position, type);
                updateLikeCount(position, postList.get(position).getLikeCount(),!isLiked);

            } else {

                Map<String, Boolean> newlikeData = new HashMap<>();
                for (Map.Entry<String, Boolean> entry : post.getLikes().entrySet())
                {

                    newlikeData.put(entry.getKey(),entry.getValue());

                }

                newlikeData.put(PreferencesHelper.getPreference(this, PreferencesHelper.PREFERENCE_FIREBASE_UUID), true);

                for (Map.Entry<String, Boolean> entry : newlikeData.entrySet())
                {

                    Log.e("dsjdhsjdhj"+entry.getKey(), String.valueOf(entry.getValue()));

                }

                postList.get(position).setLikeCount(likecount+1);
                postList.get(position).setLikes(newlikeData);
                feedAdapter.notifyItemChanged(position, type);
                updateLikeCount(position,postList.get(position).getLikeCount(),true);

            }

        }


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

    @Override
    public void onDeleteClick(final int feedItem) {

        FeedContextMenuManager.getInstance().hideContextMenu();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postListId.get(feedItem))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");

                        postListId.remove(feedItem);
                        postList.remove(feedItem);
                        feedAdapter.updateItems(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }
}

