package ke.co.thinksynergy.movers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.fragment.AddNameFragment;
import ke.co.thinksynergy.movers.location.LocationState;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.logging.BeeLog;


public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (isLoggedIn()) {
            if (TextUtils.isEmpty(PrefUtils.getInstance().getUser().getName())) {
                FragmentActivity.start(this, AddNameFragment.newInstance(), getString(R.string.add_your_name));
                SplashActivity.this.finish();
            } else {
                BeeLog.i(TAG, "isAnyProviderAvailable = " + LocationState.with(this).isAnyProviderAvailable());
                BeeLog.i(TAG, "isGpsAvailable = " + LocationState.with(this).isGpsAvailable());
                BeeLog.i(TAG, "isNetworkAvailable = " + LocationState.with(this).isNetworkAvailable());
                BeeLog.i(TAG, "isPassiveAvailable = " + LocationState.with(this).isPassiveAvailable());
                BeeLog.i(TAG, "locationServicesEnabled = " + LocationState.with(this).locationServicesEnabled());
                startActivity(new Intent(this, MainActivity.class));
                SplashActivity.this.finish();
            }
        } else if (PrefUtils.getInstance().getBoolean(R.string.pref_pending_phone_verification)) {
            PhoneVerificationActivity.start(this);
            SplashActivity.this.finish();
        } else {
            SignInActivity.start(this);
            SplashActivity.this.finish();
        }

        /*BeeLog.i(TAG, "isAnyProviderAvailable = " + LocationState.with(this).isAnyProviderAvailable());
        BeeLog.i(TAG, "isGpsAvailable = " + LocationState.with(this).isGpsAvailable());
        BeeLog.i(TAG, "isNetworkAvailable = " + LocationState.with(this).isNetworkAvailable());
        BeeLog.i(TAG, "isPassiveAvailable = " + LocationState.with(this).isPassiveAvailable());
        BeeLog.i(TAG, "locationServicesEnabled = " + LocationState.with(this).locationServicesEnabled());
        startActivity(new Intent(this, MainActivity.class));
        SplashActivity.this.finish();*/

    }



    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

}
