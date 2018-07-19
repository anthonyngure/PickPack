package ke.co.thinksynergy.movers.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.thinksynergy.movers.utils.Spanny;
import ke.co.toshngure.basecode.networking.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;

public class PhoneVerificationActivity extends BaseActivity {

    //private static final long RESEND_DELAY = 30 * 1000; //30 seconds
    private static final long RESEND_DELAY = 60 * 1000; //1 minute
    // private static final long RESEND_DELAY = 60 * 5 * 1000; //5 minutes
    @BindView(R.id.codeGuideTV)
    TextView codeGuideTV;
    @BindView(R.id.codeMET)
    MaterialEditText codeMET;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.resendTV)
    TextView resendTV;
    @BindView(R.id.resendPB)
    ProgressBar resendPB;
    @BindView(R.id.resendBtn)
    Button resendBtn;
    //private SmsVerifyCatcher smsVerifyCatcher;
    private boolean isResendingCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        ButterKnife.bind(this);

        /*smsVerifyCatcher = new SmsVerifyCatcher(this, message -> {
            String code = message.replaceAll("[^0-9]", "");//Parse verification code
            codeMET.setText(code);//set code in edit text
            codeMET.setSelection(code.length());
            PrefUtils.getInstance().writeString(R.string.pref_verification_code, code);
            //then you can send verification code to server
            isResendingCode = false;
            connect();
        });

        smsVerifyCatcher.setFilter("PickPack");*/

        Spanny guide = new Spanny(getString(R.string.guide_enter_verification_code))
                .append(" ")
                .append(PrefUtils.getInstance().getString(R.string.pref_phone),
                        new StyleSpan(Typeface.BOLD),
                        new UnderlineSpan());
        codeGuideTV.setText(guide);

        codeMET.addValidator(BaseUtils.createLengthValidator(4));

        showResendCounter();
    }


    public static void start(Context context) {
        Intent starter = new Intent(context, PhoneVerificationActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //smsVerifyCatcher.onStop();
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @OnClick(R.id.fab)
    public void onFabClicked() {
        isResendingCode = false;
        connect();
    }

    private void showResendCounter() {

        resendPB.setProgress(0);
        resendBtn.setEnabled(false);
        resendPB.setVisibility(View.VISIBLE);
        resendTV.setVisibility(View.VISIBLE);
        resendTV.setText("");

        new CountDownTimer(RESEND_DELAY, 1000) {

            /**
             * Callback fired on regular interval.
             *
             * @param millisUntilFinished The amount of time until finished.
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingSeconds = millisUntilFinished / 1000;
                if (remainingSeconds > 60) {
                    int minutes = (int) (remainingSeconds / 60);
                    int seconds = (int) (remainingSeconds - (60 * minutes));
                    resendTV.setText("Resend code in " + minutes + " min " + seconds + " secs");
                } else {
                    resendTV.setText("Resend code in " + remainingSeconds + " secs");
                }
                float elapsed = (float) (RESEND_DELAY - millisUntilFinished);
                resendPB.setProgress(Math.round((elapsed / RESEND_DELAY) * 100));
            }

            /**
             * Callback fired when the time is up.
             */
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                resendPB.setProgress(100);
                resendTV.setText("Did\'t receive code ? Tap resend");
                resendBtn.setEnabled(true);
            }
        }.start();
    }

    @OnClick(R.id.resendBtn)
    public void onResendBtnClicked() {
        isResendingCode = true;
        connect();
    }

    @Override
    public void connect() {
        super.connect();
        RequestParams params = new RequestParams();
        if (isResendingCode){
            params.put(BackEnd.Params.PHONE, PrefUtils.getInstance().getString(R.string.pref_phone));
            Client.getInstance().getClient()
                    .post(Client.absoluteUrl(BackEnd.EndPoints.PHONE_SIGN_IN),
                            params, new ResponseHandler(this));
        } else {
            params.put(BackEnd.Params.CODE, codeMET.getText().toString());
            params.put(BackEnd.Params.PHONE, PrefUtils.getInstance().getString(R.string.pref_phone));
            Client.getInstance().getClient()
                    .post(Client.absoluteUrl(BackEnd.EndPoints.VERIFY_PHONE),
                            params, new ResponseHandler(this));
        }
    }

    @Override
    protected void onSuccessResponse(JSONObject data, JSONObject meta) {
        super.onSuccessResponse(data, meta);
        if (isResendingCode){
            isResendingCode = false;
            try {
                boolean smsSent = data.getBoolean(BackEnd.Params.SMS_SENT);
                String phone = data.getString(BackEnd.Params.PHONE);
                PrefUtils.getInstance().writeString(R.string.pref_phone, phone);
                PrefUtils.getInstance().writeBoolean(R.string.pref_sms_sent, smsSent);
                PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, smsSent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showResendCounter();
        } else {
            PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, false);
            onAuthSuccessful(data, meta);
        }
    }
}
