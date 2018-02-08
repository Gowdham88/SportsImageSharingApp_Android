package com.cz.SarvodayaHBandroid.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.Utils;
import com.cz.SarvodayaHBandroid.ui.Models.Comment;
import com.cz.SarvodayaHBandroid.ui.activity.CommentsActivity;
import com.cz.SarvodayaHBandroid.ui.activity.MainActivity;
import com.cz.SarvodayaHBandroid.ui.activity.MyObject;
import com.cz.SarvodayaHBandroid.ui.activity.SignupScreenActivity;
import com.cz.SarvodayaHBandroid.ui.activity.UserProfileActivity;
import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cz.SarvodayaHBandroid.R;

import org.w3c.dom.Document;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    List<Comment> commeList = new ArrayList<>();
    List<String> commeListId = new ArrayList<String>();
    Comment commentlis;
    List<String> postListId = new ArrayList<String>();
    String postId;
    public CommentsAdapter(Context context, List<Comment> commeList) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
        this.commeList=commeList;
//        this.commeListId=commeListId;
    }
    public  void addData(List<Comment> stringArrayList){
        commeList.addAll(stringArrayList);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivUserAvatar)
        CircleImageView ivUserAvatar;
        @BindView(R.id.tvComment)
        TextView tvComment;
        @BindView(R.id.tvName)
        TextView tvname;
        @BindView(R.id.frame_lay)
        FrameLayout FrameLay;
        @BindView(R.id.comment_lay)
        LinearLayout conmmentLinLay;


        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        final CommentViewHolder holder = (CommentViewHolder) viewHolder;

        String str=commeList.get(position).getCommentText();
        holder.tvComment.setText(str);
        if (this.commeList.get(position).getUid().equals(PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_FIREBASE_UUID))) {

            holder.tvname.setText(PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_EMAIL));

        } else {

            if(this.commeList.get(position).getUserName() != null) {

                holder.tvname.setText(this.commeList.get(position).getUserName());

            }

        }

        holder.conmmentLinLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
//                if(context instanceof UserProfileActivity){
                    ((CommentsActivity) context).intialpopup(adapterPosition);
//                }
//                showBottomSheet();
//
            }
        });
//            holder.FrameLay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
//        switch (position % 3) {
//            case 0:
//                holder.tvComment.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit.");
//                break;
//            case 1:
//                holder.tvComment.setText("Cupcake ipsum dolor sit amet bear claw.");
//                break;
//            case 2:
//                holder.tvComment.setText("Cupcake ipsum dolor sit. Amet gingerbread cupcake. Gummies ice cream dessert icing marzipan apple pie dessert sugar plum.");
//                break;
//        }
        if(!this.commeList.get(position).getProfileImageURL().equals("") && this.commeList.get(position).getProfileImageURL()!= null) {

            Picasso.with(context).load(this.commeList.get(position).getProfileImageURL())
                    .fit().centerInside()
                    .into(holder.ivUserAvatar);
        }

//        Picasso.with(context)
//                .load(R.drawable.logo_ic)
//                .centerCrop()
//                .resize(avatarSize, avatarSize)
//                .transform(new RoundedTransformation())
//                .into(holder.ivUserAvatar);
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return commeList.size();
    }

    public void updateItems() {
        itemsCount = 10;
        notifyDataSetChanged();
    }

    public void addItem() {
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }



    private void showBottomSheet() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        LayoutInflater factory = LayoutInflater.from(context);
        View bottomSheetView = factory.inflate(R.layout.comment_deldialog, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

       TextView delete = (TextView) bottomSheetView.findViewById(R.id.delete_title);
        TextView cancel=(TextView) bottomSheetView.findViewById(R.id.cancel_txt);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
//
                db.collection("comments").document()
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
                                commeList.remove(postId);
                                Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
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

}
