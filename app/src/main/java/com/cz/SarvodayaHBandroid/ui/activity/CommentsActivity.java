package com.cz.SarvodayaHBandroid.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.Utils;
import com.cz.SarvodayaHBandroid.ui.Models.Comment;
import com.cz.SarvodayaHBandroid.ui.adapter.CommentsAdapter;
import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;
import com.cz.SarvodayaHBandroid.ui.view.SendCommentButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cz.SarvodayaHBandroid.R;

import org.w3c.dom.Document;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentsActivity extends AppCompatActivity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    public static final String POST_ID = "post_id";
    public static final String Comments_ID = "comments_id";

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
    String commentid;
    String postId;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.back_image)
    ImageView backarrow;
    int position;
    String str;
    private android.support.v7.app.AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        postId = getIntent().getStringExtra(POST_ID);

        setupComments();
        loadPost();
        setupSendCommentButton();

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
//        loadPost();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);
        Collections.sort(commeList, new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                return String.valueOf(lhs.getPostTime()).compareTo(String.valueOf(rhs.getPostTime()));
            }
        });
        commentsAdapter = new CommentsAdapter(this,commeList);
        rvComments.setAdapter(commentsAdapter);
//        commentsAdapter = new CommentsAdapter(this,commeList);
//        rvComments.setAdapter(commentsAdapter);
        commentsAdapter.notifyDataSetChanged();
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        commentsAdapter.setAnimationsLocked(false);
        commentsAdapter.setDelayEnterAnimation(false);
        linearLayoutManager.scrollToPosition(commeList.size() - 1);
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
//    public void sortDate() {
//
//    }
    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);

    }
    public long getPostTime() {

        Date currentDate = new Date();
        long unixTime = currentDate.getTime() / 1000;
        return unixTime;


    }
    private void startIntroAnimation() {
        ViewCompat.setElevation(toolbar, 0);
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
                        ViewCompat.setElevation(toolbar, Utils.dpToPx(8));
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

    @OnClick(R.id.back_image)
    public void setBackarrow() {

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(toolbar, 0);
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
        Utils.hideKeyboard(CommentsActivity.this);
        etComment.setText(null);
        btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
//        }
    }
    private void datacomments() {

        String comment =  etComment.getText().toString();

        if (comment != null && comment.length() > 0) {

            addcomment(comment);



        } else {

            Toast.makeText(getApplicationContext(),"Comment is Empty",Toast.LENGTH_SHORT).show();
        }


    }

    private void addcomment(String comment) {
        String uid = PreferencesHelper.getPreference(CommentsActivity.this, PreferencesHelper.PREFERENCE_FIREBASE_UUID);
        String userName = PreferencesHelper.getPreference(CommentsActivity.this, PreferencesHelper.PREFERENCE_EMAIL);
        String profileImageURL = PreferencesHelper.getPreference(CommentsActivity.this, PreferencesHelper.PREFERENCE_PROFILE_PIC);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Boolean> comData = new HashMap<>();
        comData.put(uid, false);

        Comment comment1 = new Comment(uid,profileImageURL,userName,comment,postId,getPostTime());

        db.collection("comments").add(comment1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

        Query first = db.collection("comments").whereEqualTo("postid",postId);

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

    public void intialpopup(int position) {
//        Intent intent=new Intent(UserProfileActivity.this, ImageDetailActivity.class);
//        intent.putExtra("imagepath",postList.get(position).getPhotoURL());
//        intent.putExtra("name",isLoading);
//        intent.putExtra("postid",postListId.get(position));
//        startActivityForResult(intent,5555);
//            ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);
       String id=commeListId.get(position);
        showBottomSheet(position,id);

    }

    private void showBottomSheet(final int position, final String id) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CommentsActivity.this);
        LayoutInflater factory = LayoutInflater.from(CommentsActivity.this);
        View bottomSheetView = factory.inflate(R.layout.comment_deldialog, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        TextView delete = (TextView) bottomSheetView.findViewById(R.id.delete_title);
        TextView cancel=(TextView) bottomSheetView.findViewById(R.id.cancel_txt);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
//
                db.collection("comments").document(id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                                UserAdapter.updateItems(false);
//                                Intent intent=new Intent();
//                                setResult(5555,intent);
//                                hideProgressDialog();
//                                finish();
                                commeList.remove(position);
                                commeListId.remove(position);
                                commentsAdapter.notifyDataSetChanged();
                                hideProgressDialog();
//                                Toast.makeText(CommentsActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                                hideProgressDialog();
                            }
                        });
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
    public void showProgressDialog() {


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(CommentsActivity.this);
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
