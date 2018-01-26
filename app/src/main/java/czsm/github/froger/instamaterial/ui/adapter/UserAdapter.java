package czsm.github.froger.instamaterial.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.Utils;
import czsm.github.froger.instamaterial.ui.Models.Post;
import czsm.github.froger.instamaterial.ui.activity.ImageDetailActivity;
import czsm.github.froger.instamaterial.ui.activity.MainActivity;
import czsm.github.froger.instamaterial.ui.activity.UserProfileActivity;

/**
 * Created by czsm4 on 26/01/18.
 */

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<Post> postList = new ArrayList<>();
    String Profileimge;
    public MainActivity activity;
    Post postItem;
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
    public void onBindViewHolder(UserAdapter.ViewHolder holder, final int position) {

//        holder.image.setImageResource(arr1[position]);
        if(!this.postList.get(position).getPostimage().equals("") && this.postList.get(position).getPostimage() != null) {

            Picasso.with(context).load(this.postList.get(position).getPostimage()).fit()
                    .centerCrop()
                    .into(holder.image);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intialpopup(position);
            }
        });
    }

    private void intialpopup(int position) {
        Profileimge=postList.get(position).getPostimage();
        if(Profileimge!=null){
            Intent intent=new Intent(context, ImageDetailActivity.class);
            intent.putExtra("imagepath",Profileimge.toString());
            context.startActivity(intent);
        }
        else{
            Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.img_rec);
        }
    }
}
