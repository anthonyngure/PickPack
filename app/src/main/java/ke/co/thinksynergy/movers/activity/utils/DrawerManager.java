package ke.co.thinksynergy.movers.activity.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.activity.BaseActivity;
import ke.co.thinksynergy.movers.model.User;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class DrawerManager {

    private AppCompatActivity mContext;
    private Drawer mDrawer;

    private DrawerManager(AppCompatActivity activity, Toolbar toolbar) {
        this.mContext = activity;
        init(toolbar);
    }

    public static DrawerManager init(AppCompatActivity activity, Toolbar toolbar) {
        return new DrawerManager(activity, toolbar);
    }

    private void init(Toolbar toolbar) {
//User user = getUser();

        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withCloseDrawerOnProfileListClick(false)
                .withNameTypeface(Typeface.DEFAULT_BOLD)
                .withHeaderBackground(new ColorDrawable(BaseUtils.getColor(mContext, R.attr.colorAccent)))
                .withTextColor(Color.WHITE)
                .withActivity(mContext)
                .withThreeSmallProfileImages(true)
                .withAlternativeProfileHeaderSwitching(false)
                .withProfileImagesClickable(false)
                .withSelectionListEnabled(false)
                .withCloseDrawerOnProfileListClick(false);

        //*Add user*//*
        User user = PrefUtils.getInstance().getUser();
        ProfileDrawerItem userProfileDrawerItem = new ProfileDrawerItem()
                .withName(user.getName())
                .withTextColorRes(R.color.colorAccent)
                .withNameShown(true)
                .withEmail(TextUtils.isEmpty(user.getPhone()) ? user.getEmail() : user.getPhone());
        if (!TextUtils.isEmpty(user.getFacebookPictureUrl())){
            userProfileDrawerItem.withIcon(user.getFacebookPictureUrl());
        } else {
            userProfileDrawerItem.withIcon(R.drawable.profile);
        }

        accountHeaderBuilder.addProfiles(userProfileDrawerItem);

        mDrawer = new DrawerBuilder().withActivity(mContext)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withAccountHeader(accountHeaderBuilder.build())
                .withHeaderPadding(true)
                .withCloseOnClick(false)
                .withActionBarDrawerToggle(true)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName("Sign Out").withIcon(R.drawable.ic_sign_out_black_24dp)
                                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                                    ((BaseActivity) mContext).signOut();
                                    return true;
                                }),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("PickPack @ 2018").withIcon(R.drawable.ic_copyright_black_24dp)
                )
                //.withShowDrawerUntilDraggedOpened(true)
                .build();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDrawer.getHeader().setPadding(0, 24, 0, 0);
        }
    }

    public Drawer getDrawer() {
        return mDrawer;
    }
}
