package ke.co.thinksynergy.movers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.synnapps.carouselview.CarouselView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.model.FacebookUser;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.networking.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.logging.BeeLog;

public class SignInActivity extends BaseActivity implements FacebookCallback<LoginResult> {


    private static final String TAG = "SignInActivity";
    private static final int SLIDE_INTERVAL = 3000;
    @BindView(R.id.carouselView)
    CarouselView carouselView;
    @BindView(R.id.phoneMET)
    MaterialEditText phoneMET;

    @BindView(R.id.slideTV)
    TextView slideTV;

    int[] moveWithOptions = new int[]{
            R.string.motor_bike,
            R.string.pick_up,
            R.string.small_truck,
            R.string.medium_truck,
            R.string.big_truck,
            R.string.trailer_20ft,
            R.string.trailer_40ft,
    };

    int[] moveWithOptionImages = new int[]{
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
    };

    private static final String[] readPermissions = new String[]{"email", "public_profile"};
    @BindView(R.id.continueBtn)
    Button continueBtn;
    @BindView(R.id.facebookLogoutBtn)
    ImageView facebookLogoutBtn;
    @BindView(R.id.facebookLoginBtn)
    TextView facebookLoginBtn;

    private CallbackManager mCallbackManager;

    LoginManager mLoginManager;
    AccessTokenTracker mAccessTokenTracker;
    private boolean phoneSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                updateFacebookButtonUI();
            }
        };

        mLoginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager.registerCallback(mCallbackManager, this);

        carouselView.setPageCount(moveWithOptionImages.length);
        carouselView.setSlideInterval(SLIDE_INTERVAL);
        carouselView.setPageCount(moveWithOptionImages.length);
        carouselView.setImageListener((position, imageView) -> {
            imageView.setImageResource(moveWithOptionImages[position]);
        });


        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String text = getString(moveWithOptions[position]);
                slideTV.setText("Transport with: " + text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        slideTV.setText("Transport with: " + getString(moveWithOptions[0]));

        //BaseUtils.cacheInput(phoneMET, R.string.pref_phone, PrefUtils.getInstance());

        phoneMET.addValidator(BaseUtils.createRequiredValidator(getString(R.string.error_field_is_required)));
        phoneMET.addValidator(BaseUtils.createLengthValidator(10));
        phoneMET.addValidator(BaseUtils.createPhoneValidator(getString(R.string.error_phone)));

        updateFacebookButtonUI();

    }

    @Override
    protected void setUpStatusBarColor() {
        //super.setUpStatusBarColor();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SignInActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.continueBtn)
    public void onContinueBtnClicked(View view) {
        if (phoneMET.validate()) {
            phoneSignIn = true;
            connect();
        } else {
            Snackbar.make(view, R.string.error_phone_number, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, view1 -> {
                    }).show();
        }
    }

    @OnClick(R.id.facebookLogoutBtn)
    public void onFacebookLogoutBtnClicked() {
        mLoginManager.logOut();
        PrefUtils.getInstance().remove(R.string.pref_facebook_user);
        updateFacebookButtonUI();
    }

    @OnClick(R.id.facebookLoginBtn)
    public void onFacebookLoginBtnClicked() {

        if (AccessToken.getCurrentAccessToken() != null) {
            if (PrefUtils.getInstance().getFacebookUser() != null) {
                phoneSignIn = false;
                connect();
            } else {
                readUserFBData();
            }
        } else {
            mAccessTokenTracker.startTracking();
            showProgressDialog();
            mLoginManager.logInWithReadPermissions(this, Arrays.asList(readPermissions));
        }
    }

    private void updateFacebookButtonUI() {
        if (PrefUtils.getInstance().getFacebookUser() != null && AccessToken.getCurrentAccessToken() != null) {
            facebookLoginBtn.setText(getString(R.string.continue_as,
                    PrefUtils.getInstance().getFacebookUser().getName()));
            facebookLogoutBtn.setVisibility(View.VISIBLE);
        } else {
            facebookLoginBtn.setText(getString(R.string.sign_in_with_facebook));
            facebookLogoutBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void readUserFBData() {
        showProgressDialog();
        Bundle params = new Bundle();
        params.putString("fields", "email,id,name,picture");
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), (object, response) -> {
                    BeeLog.i(TAG, String.valueOf(response));
                    FacebookUser user = BaseUtils.getSafeGson().fromJson(response.getJSONObject().toString(), FacebookUser.class);
                    BeeLog.i(TAG, String.valueOf(user));
                    PrefUtils.getInstance().saveFacebookUser(user);
                    updateFacebookButtonUI();
                    hideProgressDialog();
                    //Connect to sign in to the api if the facebook user data exists
                    if (PrefUtils.getInstance().getFacebookUser() != null) {
                        phoneSignIn = false;
                        connect();
                    }
                });

        request.setParameters(params);
        request.executeAsync();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        toastDebug("Facebook Login onSuccess");
        hideProgressDialog();
        readUserFBData();
    }

    @Override
    public void onCancel() {
        toastDebug("Facebook Login onCancel");
        hideProgressDialog();
    }

    @Override
    public void onError(FacebookException error) {
        BeeLog.e(TAG, error);
        toastDebug("Facebook Login onFailure " + String.valueOf(error));
        hideProgressDialog();
    }

    public void connect() {
        super.connect();
        RequestParams params = new RequestParams();
        if (phoneSignIn) {
            params.put(BackEnd.Params.PHONE, phoneMET.getText().toString());
            Client.getInstance().getClient()
                    .post(Client.absoluteUrl(BackEnd.EndPoints.PHONE_SIGN_IN),
                            params, new ResponseHandler(this));
        } else {
            FacebookUser user = PrefUtils.getInstance().getFacebookUser();
            params.put(BackEnd.Params.EMAIL, user.getEmail());
            params.put(BackEnd.Params.NAME, user.getName());
            params.put(BackEnd.Params.FACEBOOK_ID, user.getId());
            params.put(BackEnd.Params.FACEBOOK_PICTURE_URL, user.getPicture().getData().getUrl());
            Client.getInstance().getClient()
                    .post(Client.absoluteUrl(BackEnd.EndPoints.FACEBOOK_SIGN_IN),
                            params, new ResponseHandler(this));
        }

    }

    @Override
    protected void onSuccessResponse(JSONObject data, JSONObject meta) {
        super.onSuccessResponse(data, meta);
        if (phoneSignIn) {
            try {
                boolean smsSent = data.getBoolean(BackEnd.Params.SMS_SENT);
                String phone = data.getString(BackEnd.Params.PHONE);
                PrefUtils.getInstance().writeString(R.string.pref_phone, phone);
                PrefUtils.getInstance().writeBoolean(R.string.pref_sms_sent, smsSent);
                PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, smsSent);
                PhoneVerificationActivity.start(this);
                this.finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Go to code input/ then if the user has no name ask for the name
        } else {
            onAuthSuccessful(data, meta);
        }
    }


}
