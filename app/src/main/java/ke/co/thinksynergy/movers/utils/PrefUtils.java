/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ke.co.thinksynergy.movers.App;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.model.FacebookUser;
import ke.co.thinksynergy.movers.model.User;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.basecode.utils.PrefUtilsImpl;


/**
 * Created by Anthony Ngure on 7/1/2016.
 * Email : anthonyngure25@gmail.com.
 */
public class PrefUtils extends PrefUtilsImpl {

    private static final String TAG = PrefUtils.class.getSimpleName();
    private static volatile PrefUtils mInstance;
    private SharedPreferences mSharedPreferences;
    private User user;
    private FacebookUser mFacebookUser;

    private PrefUtils() {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    }

    public static PrefUtils getInstance() {
        if (mInstance == null) {
            mInstance = new PrefUtils();
        }
        return mInstance;
    }


    public void signOut() {
        mSharedPreferences.edit().clear().apply();
        invalidate();
    }


    public void saveUser(User user) {
        writeString(R.string.pref_user, BaseUtils.getSafeGson().toJson(user, User.class));
        invalidate();
    }

    public User getUser() {
        if (user == null) {
            String userJson = getString(R.string.pref_user, "");
            if (userJson.isEmpty()) {
                return null;
            } else {
                this.user = BaseUtils.getSafeGson().fromJson(userJson, User.class);
                return user;
            }
        }
        return user;
    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected Context getContext() {
        return App.getInstance();
    }


    public void saveFacebookUser(FacebookUser user) {
        writeString(R.string.pref_facebook_user, BaseUtils.getSafeGson().toJson(user, FacebookUser.class));
        invalidate();
    }

    public FacebookUser getFacebookUser() {
        if (mFacebookUser == null) {
            String userJson =getString(R.string.pref_facebook_user, "");
            if (userJson.isEmpty()) {
                return null;
            } else {
                this.mFacebookUser = BaseUtils.getSafeGson().fromJson(userJson, FacebookUser.class);
                return mFacebookUser;
            }
        }
        return mFacebookUser;
    }


    /**
     * Load shared prefs again
     */
    @Override
    protected void invalidate() {
        mInstance = null;
        mSharedPreferences = null;
    }
}
