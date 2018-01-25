package czsm.github.froger.instamaterial.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindDimen;
import butterknife.BindString;

/**
 * Created by Miroslaw Stanek on 15.07.15.
 */
public class BaseDrawerActivity extends BaseActivity {



    @BindDimen(czsm.github.froger.instamaterial.R.dimen.global_menu_avatar_size)
    int avatarSize;
    @BindString(czsm.github.froger.instamaterial.R.string.user_profile_photo)
    String profilePhoto;

    //Cannot be bound via Butterknife, hosting view is initialized later (see setupHeader() method)
    private ImageView ivMenuUserProfilePhoto;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(czsm.github.froger.instamaterial.R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(czsm.github.froger.instamaterial.R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();
        setupHeader();

    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
//        if (getToolbar() != null) {
//            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    drawerLayout.openDrawer(Gravity.LEFT);
//                }
//            });
//        }
    }

    private void setupHeader() {
//        View headerView = vNavigation.getHeaderView(0);
//        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
//        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onGlobalMenuHeaderClick(v);
//            }
//        });
//
//        Picasso.with(this)
//                .load(profilePhoto)
//                .placeholder(R.drawable.img_circle_placeholder)
//                .resize(avatarSize, avatarSize)
//                .centerCrop()
//                .transform(new CircleTransformation())
//                .into(ivMenuUserProfilePhoto);
    }

    public void onGlobalMenuHeaderClick(final View v) {
//        drawerLayout.closeDrawer(Gravity.LEFT);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int[] startingLocation = new int[2];
//                v.getLocationOnScreen(startingLocation);
//                startingLocation[0] += v.getWidth() / 2;
//                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
//                overridePendingTransition(0, 0);
//            }
//        }, 200);
    }

}
