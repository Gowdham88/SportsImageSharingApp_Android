package com.cz.SarvodayaHBandroid.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.ui.Models.Post;
import com.cz.SarvodayaHBandroid.ui.activity.ImageDetailActivity;
import com.cz.SarvodayaHBandroid.ui.activity.MainActivity;
import com.cz.SarvodayaHBandroid.ui.activity.ProfileActivity;
import com.cz.SarvodayaHBandroid.ui.utils.PreferencesHelper;
import com.cz.SarvodayaHBandroid.ui.view.LoadingFeedItemView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.cz.SarvodayaHBandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;

    private Context context;
    private OnFeedItemClickListener onFeedItemClickListener;
    List<Post> postList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private boolean showLoadingView = false;

    public FeedAdapter(Context context,List<Post> postList) {
        this.context  = context;
        this.postList = postList;

    }

    public  void addData(List<Post> stringArrayList){
        postList.addAll(stringArrayList);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DEFAULT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
            CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
            setupClickableViews(view, cellFeedViewHolder);
            return cellFeedViewHolder;
        } else if (viewType == VIEW_TYPE_LOADER) {
            LoadingFeedItemView view = new LoadingFeedItemView(context);
            view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            return new LoadingCellFeedViewHolder(view);
        }

        return null;
    }

    private void setupClickableViews(final View view, final CellFeedViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onCommentsClick(view, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(v, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                if (context instanceof MainActivity) {

                    ((MainActivity) context).getDocument(adapterPosition,ACTION_LIKE_IMAGE_CLICKED);

                } else if  (context instanceof ProfileActivity) {

                    ((ProfileActivity) context).getDocument(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);

                }


            }
        });
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                if (context instanceof MainActivity) {

                    ((MainActivity) context).getDocument(adapterPosition,ACTION_LIKE_BUTTON_CLICKED);

                } else if  (context instanceof ProfileActivity) {

                    ((ProfileActivity) context).getDocument(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);


                }

            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                onFeedItemClickListener.onProfileClick(view,adapterPosition);
            }
        });

        cellFeedViewHolder.vUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                onFeedItemClickListener.onProfileClick(view,adapterPosition);
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ((CellFeedViewHolder) viewHolder).bindView(postList.get(position),context);

        if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem((LoadingCellFeedViewHolder) viewHolder);
        }
    }

    private void bindLoadingFeedItem(final LoadingCellFeedViewHolder holder) {
        holder.loadingFeedItemView.setOnLoadingFinishedListener(new LoadingFeedItemView.OnLoadingFinishedListener() {
            @Override
            public void onLoadingFinished() {
                showLoadingView = false;
                notifyItemChanged(0);
            }
        });
        holder.loadingFeedItemView.startLoading();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updateItems(boolean animated) {

        if (animated) {
            notifyItemRangeInserted(0, postList.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public void showLoadingView() {
        showLoadingView = true;
        notifyItemChanged(0);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @BindView(R.id.ivFeedBottom)
        TextView ivFeedBottom;
        @BindView(R.id.btnComments)
        ImageButton btnComments;
        @BindView(R.id.btnLike)
        ImageButton btnLike;
        @BindView(R.id.btnMore)
        ImageButton btnMore;
        @BindView(R.id.vBgLike)
        View vBgLike;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;
        @BindView(R.id.ivUserProfile)
        ImageView ivUserProfile;
        @BindView(R.id.vImageRoot)
        FrameLayout vImageRoot;
        @BindView(R.id.ivUserName)
        TextView vUsername;

         PopupWindow pw;
        Post postItem;
        Context context;
        String Profileimg;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bindView(Post postItem, final Context context) {
            this.postItem = postItem;
            this.context  = context;
            int adapterPosition = getAdapterPosition();

            if(!this.postItem.getProfileImageURL().equals("") && this.postItem.getProfileImageURL() != null) {

                Picasso.with(context).load(this.postItem.getProfileImageURL()).fit().centerInside()
                        .into(ivUserProfile);
            }

            if(!this.postItem.getCaption().equals("empty")) {

                ivFeedBottom.setText(postItem.getCaption());
            }

            if (this.postItem.getUid().equals(PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_FIREBASE_UUID))) {

                vUsername.setText(PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_EMAIL));
                btnMore.setVisibility(View.VISIBLE);

            } else {

                btnMore.setVisibility(View.GONE);

                if(this.postItem.getUserName()!= null) {

                    vUsername.setText(this.postItem.getUserName());

                }

            }


            if(!this.postItem.getPhotoURL().equals("failed")) {

                Picasso.with(context).load(this.postItem.getPhotoURL()).fit().centerInside()
                        .into(ivFeedCenter);
            }

            if (this.postItem.getLikes().size() > 0) {

                    if (this.postItem.getLikes().get(PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_FIREBASE_UUID)) != null){

                        Boolean isLiked = this.postItem.getLikes().get(PreferencesHelper.getPreference(context, PreferencesHelper.PREFERENCE_FIREBASE_UUID));

                        if (isLiked) {

                            btnLike.setImageResource(R.drawable.ic_heart_red);

                        } else  {

                            btnLike.setImageResource(R.drawable.ic_heart_outline_grey);

                        }



                    } else {

                        btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
                    }


            }


            tsLikesCounter.setCurrentText(vImageRoot.getResources().getQuantityString(
                    R.plurals.likes_count, postItem.getLikeCount(), postItem.getLikeCount()
            ));
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initiatePopupWindow();
                }
            });
        }

        private  void initiatePopupWindow() {

          Profileimg=postItem.getPhotoURL();
            if(Profileimg!=null){
                Intent intent=new Intent(context, ImageDetailActivity.class);
                intent.putExtra("imagepath",Profileimg.toString());
                context.startActivity(intent);
            }
            else{
                Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show();
            }

        }

        public Post getPostItem() {
            return postItem;
        }


    }

    public static class LoadingCellFeedViewHolder extends CellFeedViewHolder {

        LoadingFeedItemView loadingFeedItemView;

        public LoadingCellFeedViewHolder(LoadingFeedItemView view) {
            super(view);
            this.loadingFeedItemView = view;
        }

        @Override
        public void bindView(Post postItem, Context context) {
            super.bindView(postItem, context);
        }


    }

    public static class FeedItem {
        public int likesCount;
        public boolean isLiked;

        public FeedItem(int likesCount, boolean isLiked) {
            this.likesCount = likesCount;
            this.isLiked = isLiked;
        }
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onProfileClick(View v, int position);
    }

}
