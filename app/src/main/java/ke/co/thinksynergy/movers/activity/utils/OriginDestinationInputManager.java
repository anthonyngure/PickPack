package ke.co.thinksynergy.movers.activity.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.location.DirectionResponse;
import ke.co.thinksynergy.movers.presenters.AutocompletePredictionPresenter;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.views.RoundedView;

/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class OriginDestinationInputManager {

    private final AppCompatActivity mContext;
    @BindView(R.id.destinationET)
    EditText mDestinationET;
    @BindView(R.id.originRV)
    RoundedView mOriginRV;
    @BindView(R.id.destinationRV)
    RoundedView mDestinationRV;
    @BindView(R.id.originET)
    EditText mOriginET;
    private BottomViewManager mBottomViewManager;
    private Autocomplete<AutocompletePrediction> mDestinationAutocomplete;

    private OriginDestinationInputManager(AppCompatActivity activity, BottomViewManager bottomViewManager) {
        this.mContext = activity;
        this.mBottomViewManager = bottomViewManager;
        ButterKnife.bind(this, activity);

        mOriginET.setText(PrefUtils.getInstance().getString(R.string.pref_last_used_origin_location_name));
        initForDestinationET();
    }

    public static OriginDestinationInputManager init(AppCompatActivity activity, BottomViewManager bottomViewManager) {
        return new OriginDestinationInputManager(activity, bottomViewManager);
    }

    private void hideKeyboard() {
        View view = mContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void initForDestinationET() {

        mDestinationRV.setChecked(true);

        AutocompleteCallback<AutocompletePrediction> destinationCallback = new AutocompleteCallback<AutocompletePrediction>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, AutocompletePrediction item) {
                editable.clear();
                editable.append(item.getPrimaryText(new StyleSpan(Typeface.BOLD)));
                mDestinationET.setSelection(0);
                mDestinationRV.postDelayed(() -> mBottomViewManager.onDestinationChanged(item), 500);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
                if (!shown){
                    hideKeyboard();
                }
            }
        };
        mDestinationAutocomplete = Autocomplete.<AutocompletePrediction>on(mDestinationET).with(6f)
                .with(new ColorDrawable(Color.WHITE))
                .with(new AutocompletePredictionPresenter(mContext))
                .with(destinationCallback).build();
    }

    public void onResolveRetrievedLastLocation(String retrievedLastLocationName) {
        PrefUtils.getInstance().writeString(R.string.pref_last_used_origin_location_name, retrievedLastLocationName);
        mOriginET.setText(retrievedLastLocationName);
    }

    public void close() {
        mDestinationAutocomplete.dismissPopup();
    }

    public boolean isOpen() {
        return mDestinationAutocomplete.isPopupShowing();
    }

    public void onDirectionResolved(DirectionResponse.Leg leg) {
        PrefUtils.getInstance().writeString(R.string.pref_last_used_origin_location_name,
                leg.getStartAddress());
        mOriginET.setText(leg.getStartAddress());
        //mDestinationET.setText(leg.getEndAddress());
        //mDestinationET.setSelection(0);
    }
}
