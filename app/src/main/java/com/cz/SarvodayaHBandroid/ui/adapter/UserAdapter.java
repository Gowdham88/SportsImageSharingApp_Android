package com.cz.SarvodayaHBandroid.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.cz.SarvodayaHBandroid.ui.Models.Post;
import com.cz.SarvodayaHBandroid.ui.activity.ImageDetailActivity;
import com.cz.SarvodayaHBandroid.ui.activity.MainActivity;
import com.cz.SarvodayaHBandroid.ui.activity.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import com.cz.SarvodayaHBandroid.R;

/**
 * Created by czsm4 on 26/01/18.
 */

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<Post> postList = new ArrayList<>();
    String Profileimge;
    public MainActivity activity;
    Post postItem;
    String name="name";
    private boolean isLoading=true;
//    private final int cellSize;
    int arr1[]={R.drawable.camera,R.drawable.camera,R.drawable.camera,R.drawable.camera,};

    public UserAdapter(Context context, List<Post> postList) {
        this.context  = context;
        this.postList = postList;
//        this.cellSize = Utils.getScreenWidth(context)/3;
    }


    public  void addData(List<Post> stringArrayList){
        postList.addAll(stringArrayList);
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_content, parent, false);

        //view.setOnClickListener(MainActivity.myOnClickListener);
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
//        layoutParams.height = cellSize;
//        layoutParams.width = cellSize;
////        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {

//        holder.image.setImageResource(arr1[position]);
        if(!this.postList.get(position).getPhotoURL().equals("") && this.postList.get(position).getPhotoURL() != null) {

            Picasso.with(context).load(this.postList.get(position).getPhotoURL()).fit()
                    .centerCrop()
                    .into(holder.image);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                intialpopup(position);
                int adapterPosition = holder.getAdapterPosition();
                if(context instanceof UserProfileActivity){
                    ((UserProfileActivity) context).intialpopup(adapterPosition);
                }
            }
        });
    }

//    private void intialpopup(int position) {
//        Profileimge=postList.get(position).getPhotoURL();
//        if(Profileimge!=null){
//            Intent intent=new Intent(context, ImageDetailActivity.class);
//            intent.putExtra("imagepath",Profileimge.toString());
//            intent.putExtra("name",isLoading);
//            context.startActivity(intent);
////            ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.stay);
//        }
//        else{
//            Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

//    public void updateItems(boolean animated) {
//
//        if (animated) {
//            notifyItemRangeInserted(0, postList.size());
//        } else {
//            notifyDataSetChanged();
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.img_rec);
        }
    }
}
