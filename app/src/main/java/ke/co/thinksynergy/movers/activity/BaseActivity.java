/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ke.co.thinksynergy.movers.App;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.database.Database;
import ke.co.thinksynergy.movers.fragment.AddNameFragment;
import ke.co.thinksynergy.movers.model.User;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.app.DialogAsyncTask;
import ke.co.toshngure.basecode.networking.ConnectionListener;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.logging.BeeLog;


/**
 * Created by Anthony Ngure on 7/1/2016.
 * Email : anthonyngure25@gmail.com.
 */

@SuppressLint("Registered")
public class BaseActivity extends BaseAppActivity implements ConnectionListener {

    private static final String TAG = BaseActivity.class.getSimpleName();


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Client.getInstance().getClient().cancelRequests(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDebuggable() {
        return BeeLog.DEBUG;
    }

    public boolean isLoggedIn() {
        return (getUser() != null);
    }

    protected User getUser() {
        return PrefUtils.getInstance().getUser();
    }

    public void onAuthSuccessful(JSONObject data, JSONObject meta) {

        BeeLog.i(TAG, "onAuthSuccessful");

        User user = BaseUtils.getSafeGson().fromJson(data.toString(), User.class);
        PrefUtils.getInstance().saveUser(user);

        Client.getInstance().invalidate();

        if (TextUtils.isEmpty(PrefUtils.getInstance().getUser().getName())) {
            FragmentActivity.start(this, AddNameFragment.newInstance(), getString(R.string.add_your_name));
        } else {
            startNewTaskActivity(new Intent(this, MainActivity.class));
        }

        //toast(meta.message);

        this.finish();
    }

    @SuppressLint("StaticFieldLeak")
    public void signOut() {

        new DialogAsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                //mLocalUserConnectedRef.setValue(ServerValue.TIMESTAMP);
                /*try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                User user = PrefUtils.getInstance().getUser();
                PrefUtils.getInstance().signOut();
                App.getInstance().getJobManager().clear();
                Database.getInstance().clean();
                Client.getInstance().invalidate();
                //PrefUtils.getInstance().writeString(R.string.pref_email, user.getEmail());
                //PrefUtils.getInstance().writeString(R.string.pref_phone, user.getPhone());
                return null;
            }

            @Override
            protected Activity getActivity() {
                return getThis();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startNewTaskActivity(new Intent(getActivity(), SignInActivity.class));
                getActivity().finish();
            }
        }.execute();
    }

    @Override
    public void connect() {

    }

    @Override
    public void onConnectionStarted() {
        showProgressDialog();
    }


    @Override
    public void onConnectionFailed(int statusCode, JSONObject response) {
        BeeLog.d(TAG, "Connection failed! " + statusCode + ", " + String.valueOf(response));
        hideProgressDialog();
        if ((statusCode == 0) || (statusCode == 408)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.connection_timed_out)
                    .setMessage(R.string.error_connection)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.retry, (dialog, which) -> connect()).create().show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true)
                    .setTitle(R.string.server_error)
                    .setMessage(response.toString())
                    .setNegativeButton(R.string.report, (dialog, which) -> {

                    })
                    .setPositiveButton(android.R.string.ok, null);
            builder.create().show();
        }

    }

    protected void showErrorAlertDialog(String message) {
        new AlertDialog.Builder(this).setCancelable(true)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
    }

    protected void onSuccessResponse(JSONObject data, JSONObject meta) {

    }

    protected void onSuccessResponse(JSONArray data, JSONObject meta) {

    }

    protected void onErrorResponse(JSONObject meta) {
        //showErrorAlertDialog(meta.message);
    }

    @Override
    public void onConnectionSuccess(JSONObject response) {
        BeeLog.d(TAG, "onConnectionSuccess, Response = " + String.valueOf(response));
        hideProgressDialog();
        try {
            if (response.get(BackEnd.Response.DATA) instanceof JSONObject) {
                //Data is Object
                onSuccessResponse(response.getJSONObject(BackEnd.Response.DATA), response.getJSONObject(BackEnd.Response.META));
            } else {
                //Data is Array
                onSuccessResponse(response.getJSONArray(BackEnd.Response.DATA), response.getJSONObject(BackEnd.Response.META));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionProgress(int progress) {

    }

    @Override
    public Context getListenerContext() {
        return this;
    }

}
