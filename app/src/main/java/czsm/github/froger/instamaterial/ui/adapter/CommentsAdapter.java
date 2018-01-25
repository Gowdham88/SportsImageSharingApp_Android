package czsm.github.froger.instamaterial.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import czsm.github.froger.instamaterial.R;
import czsm.github.froger.instamaterial.ui.Models.Comment;

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
    Comment commentlis;

    public CommentsAdapter(Context context, List<Comment> commeList) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
        this.commeList=commeList;
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


        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;
        String str=commeList.get(position).getComment();
        holder.tvComment.setText(str);
        String namestr=commeList.get(position).getuserName();
        holder.tvname.setText(namestr);

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
                    .placeholder(R.drawable.logo_ic)
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


}
