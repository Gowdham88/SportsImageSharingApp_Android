package io.github.froger.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.Utils;
import io.github.froger.instamaterial.ui.Models.Comment;
import io.github.froger.instamaterial.ui.Models.Post;
import io.github.froger.instamaterial.ui.adapter.CommentsAdapter;
import io.github.froger.instamaterial.ui.utils.PreferencesHelper;
import io.github.froger.instamaterial.ui.view.LoadingFeedItemView;
import io.github.froger.instamaterial.ui.view.SendCommentButton;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentsActivity extends BaseDrawerActivity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;
    List<Comment> commeList = new ArrayList<Comment>();
    List<String> commeListId = new ArrayList<String>();
    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;
    String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupComments();
        loadPost();
        setupSendCommentButton();

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this,commeList);
        rvComments.setAdapter(commentsAdapter);
        commentsAdapter.notifyDataSetChanged();
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        commentsAdapter.setAnimationsLocked(false);
        commentsAdapter.setDelayEnterAnimation(false);
//            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());


        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);

    }
    public long getPostTime() {

        Date currentDate = new Date();
        long unixTime = currentDate.getTime() / 1000;
        return unixTime;


    }
    private void startIntroAnimation() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
//        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        datacomments();
//        if (validateComment()) {
//            commentsAdapter.addItem();
//            commentsAdapter.setAnimationsLocked(false);
//            commentsAdapter.setDelayEnterAnimation(false);
////            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

        etComment.setText(null);
        btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
//        }
    }
    private void datacomments() {

        String comment =  etComment.getText().toString();

        if (comment != null && comment.length() > 0) {

            addcomment(comment);
            Toast.makeText(this, comment, Toast.LENGTH_SHORT).show();


        } else {

            Toast.makeText(getApplicationContext(),"Comment is Empty",Toast.LENGTH_SHORT).show();
        }


    }

    private void addcomment(String comment) {
        String uid = PreferencesHelper.getPreference(CommentsActivity.this,PreferencesHelper.PREFERENCE_FIREBASE_UUID);
        String userName = PreferencesHelper.getPreference(CommentsActivity.this,PreferencesHelper.PREFERENCE_EMAIL);
        String profileImageURL = PreferencesHelper.getPreference(CommentsActivity.this,PreferencesHelper.PREFERENCE_PROFILE_PIC);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Boolean> comData = new HashMap<>();
        comData.put(uid, false);

        Comment comment1 = new Comment(uid,profileImageURL,userName,comment);

        db.collection("Comment").add(comment1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                loadPost();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Error", "Error adding document", e);
                Toast.makeText(getApplicationContext(),"Post Failed",Toast.LENGTH_SHORT).show();

            }

        });
    }


    public void loadPost() {

        commeList.clear();
        commeListId.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query first = db.collection("Comment");

        first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {

                        if (documentSnapshots.getDocuments().size() < 1) {

                            return;

                        }

                        for(DocumentSnapshot document : documentSnapshots.getDocuments()) {

                            Comment comment1 = document.toObject(Comment.class);
                            commeList.add(comment1);
                            commeListId.add(document.getId());
//                            Log.e("adbbd",document.getId());
//                            Log.e("adbbd", String.valueOf(document.getData()));

                        }
                        setupComments();

                    }

                });

    }


}
