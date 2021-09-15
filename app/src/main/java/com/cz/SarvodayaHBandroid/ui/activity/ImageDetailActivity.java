package com.cz.SarvodayaHBandroid.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.R;
import com.cz.SarvodayaHBandroid.ui.Models.Post;
import com.cz.SarvodayaHBandroid.ui.adapter.FeedAdapter;
import com.cz.SarvodayaHBandroid.ui.adapter.UserAdapter;
import com.cz.SarvodayaHBandroid.ui.view.FeedContextMenuManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;


public class ImageDetailActivity extends MyActivity{
    ImageButton btnMore;
    RelativeLayout linearpopup;
    List<Post> postList = new ArrayList<Post>();
    List<String> postListId = new ArrayList<String>();
    private FeedAdapter feedAdapter;
    int feedItem;
    private boolean name;
    String postid;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slice);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btnMore=(ImageButton) findViewById(R.id.btnMore);
        linearpopup=(RelativeLayout) findViewById(R.id.linearpopup);
        name =getIntent().getBooleanExtra("name",false);
        postid=getIntent().getStringExtra("postid");
        if(name){
            btnMore.setVisibility(View.VISIBLE);
        }
        else {
            btnMore.setVisibility(View.GONE);
        }
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
//                Toast.makeText(ImageDetailActivity.this, "delete", Toast.LENGTH_SHORT).show();
            }
        });
        ImageView image=(ImageView)findViewById(R.id.popup_image);
        LinearLayout btncancel = (LinearLayout) findViewById(R.id.btncancelcat);
        try{

                Bundle in=getIntent().getExtras();
                String uri= in != null ? in.getString("imagepath") : null;

                Picasso.with(getApplicationContext()).load(uri)
                        .placeholder(R.drawable.background)
                        .into(image);

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//            overridePendingTransition(R.anim.slide_down, R.anim.stay);
               
            }
        });

    }
    private void showBottomSheet() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ImageDetailActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        View bottomSheetView = factory.inflate(R.layout.delete_bottomsheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

       TextView delete = (TextView) bottomSheetView.findViewById(R.id.delete_title);
       TextView cancel=(TextView) bottomSheetView.findViewById(R.id.cancel_txt);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("posts").document(postid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                                UserAdapter.updateItems(false);
                                Intent intent=new Intent();
                                setResult(5555,intent);
                                hideProgressDialog();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
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


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImageDetailActivity.this);
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
