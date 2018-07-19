package ke.co.thinksynergy.movers.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.fragment.UpdateCostEstimateFragment;
import ke.co.thinksynergy.movers.location.GeoUtil;
import ke.co.thinksynergy.movers.location.GeocodeResponse;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.presenters.AutocompletePredictionPresenter;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.logging.BeeLog;

public class ConfirmPickUpActivity extends BaseActivity
        implements GoogleMap.OnCameraIdleListener, OnMapReadyCallback, UpdateCostEstimateFragment.Listener {

    private static final String TAG = "ConfirmPickUpActivity";
    public static final int DEFAULT_ZOOM = 14;
    private static final String EXTRA_ORIGIN = "extra_origin_latitude";
    private static final String EXTRA_DESTINATION = "extra_origin_longitude";
    public static final int REQUEST_CONFIRM_PICKUP = 1050;
    public static final String EXTRA_SELECTED_DRIVER_OPTION = "extra_selected_driver_option";
    public static final String EXTRA_CONFIRMED_ORIGIN = "extra_confirmed_origin";

    @BindView(R.id.originET)
    EditText originET;
    @BindView(R.id.loadingTV)
    TextView loadingTV;
    @BindView(R.id.confirmBtn)
    Button confirmBtn;
    @BindView(R.id.topView)
    FrameLayout topView;
    private Autocomplete<AutocompletePrediction> mOriginAutocomplete;
    private String mLastUsedOriginLocationName;
    private String mResolvedLocationName;
    private GoogleMap mGoogleMap;
    private LatLng mOrigin;
    private LatLng mDestination;
    private LatLng mNewOrigin;
    private int mCameraUpdates;
    private DriverOption mSelectedDriverOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pick_up);
        ButterKnife.bind(this);

        mOrigin = getIntent().getParcelableExtra(EXTRA_ORIGIN);
        mNewOrigin = getIntent().getParcelableExtra(EXTRA_ORIGIN);

        mDestination = getIntent().getParcelableExtra(EXTRA_DESTINATION);

        mSelectedDriverOption = getIntent().getParcelableExtra(EXTRA_SELECTED_DRIVER_OPTION);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initOriginET();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        //mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

        // mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.blue_essense));
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.copy_style));

        mGoogleMap.setOnCameraIdleListener(this);

        animateCamera();
    }


    private void animateCamera() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mNewOrigin)
                .zoom(DEFAULT_ZOOM)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static void start(AppCompatActivity context, @NonNull LatLng origin, @NonNull LatLng destination,
                             @NonNull DriverOption selectedDriverOption) {
        Intent starter = new Intent(context, ConfirmPickUpActivity.class);
        starter.putExtra(EXTRA_ORIGIN, origin);
        starter.putExtra(EXTRA_DESTINATION, destination);
        starter.putExtra(EXTRA_SELECTED_DRIVER_OPTION, selectedDriverOption);
        context.startActivityForResult(starter, REQUEST_CONFIRM_PICKUP);
    }

    private void initOriginET() {

        mLastUsedOriginLocationName = PrefUtils.getInstance().getString(R.string.pref_last_used_origin_location_name);

        originET.setText(mLastUsedOriginLocationName);

        //BaseUtils.cacheInput(originET, R.string.pref_last_used_origin_location_name, PrefUtils.getInstance());

        //Since the last used origin location name will be inserted in the

        AutocompleteCallback<AutocompletePrediction> originCallback = new AutocompleteCallback<AutocompletePrediction>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, AutocompletePrediction item) {
                editable.clear();
                editable.append(item.getPrimaryText(new StyleSpan(Typeface.BOLD)));
                originET.setSelection(0);
                //resolvePredictedOrigin(item);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
                if (!shown) {
                    hideKeyboardFrom(originET);
                }
            }
        };

        AutocompletePolicy originAutocompletePolicy = new AutocompletePolicy() {
            @Override
            public boolean shouldShowPopup(Spannable text, int cursorPos) {
                return !text.toString().equalsIgnoreCase(mLastUsedOriginLocationName) &&
                        !text.toString().equalsIgnoreCase(mResolvedLocationName);
            }

            @Override
            public boolean shouldDismissPopup(Spannable text, int cursorPos) {
                return text.toString().equalsIgnoreCase(mLastUsedOriginLocationName)
                        || text.toString().equalsIgnoreCase(mResolvedLocationName);
            }

            @Override
            public CharSequence getQuery(Spannable text) {
                return text;
            }

            @Override
            public void onDismiss(Spannable text) {
            }
        };

        mOriginAutocomplete = Autocomplete.<AutocompletePrediction>on(originET).with(6f)
                .with(new ColorDrawable(Color.WHITE))
                .with(originAutocompletePolicy)
                .with(new AutocompletePredictionPresenter(this))
                .with(originCallback).build();
    }

    private void onFinishLoading() {
        originET.setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);
        loadingTV.setVisibility(View.GONE);
    }

    private void onStartLoading() {
        originET.setVisibility(View.GONE);
        confirmBtn.setVisibility(View.GONE);
        loadingTV.setVisibility(View.VISIBLE);
    }

    public synchronized void showNotification(Object msg) {
        BeeLog.i(TAG, String.valueOf(msg));
        /*Snackbar.make(originET, String.valueOf(msg), Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, view -> {
                }).show();*/
    }

    @Override
    public void onCameraIdle() {
        mCameraUpdates++;
        if (mCameraUpdates == 1){
            return;
        }

        mNewOrigin = mGoogleMap.getCameraPosition().target;

        Location location = new Location("");
        location.setLatitude(mNewOrigin.latitude);
        location.setLongitude(mNewOrigin.longitude);

        GeoUtil.reverseGeocode(new LatLng(mNewOrigin.latitude, mNewOrigin.longitude), new GeoUtil.GeocodeListener() {
            @Override
            public void onStart() {
                onStartLoading();
            }

            @Override
            public void onSuccess(GeocodeResponse geocodeResponse) {
                List<GeocodeResponse.Result> resultList = geocodeResponse.getResults();
                if (resultList.size() > 0){
                    mResolvedLocationName = resultList.get(0).getFormattedAddress();
                    originET.setText(resultList.get(0).getFormattedAddress());
                    originET.setSelection(0);
                    onFinishLoading();
                }
            }

            @Override
            public void onFailure(int statusCode, Object response) {
                showNotification("CustomReverseGeocodingListener, onFailure");
                BeeLog.e(TAG, response);
                onFinishLoading();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mOriginAutocomplete.isPopupShowing()) {
            mOriginAutocomplete.dismissPopup();
            return;
        }
        setResult(RESULT_CANCELED);
        this.finish();
        //super.onBackPressed();
    }

    @OnClick(R.id.confirmBtn)
    public void onConfirmBtnClicked() {
        if ((mNewOrigin.latitude == mOrigin.latitude) && (mNewOrigin.longitude == mOrigin.longitude)) {
            Intent data = new Intent();
            data.putExtra(EXTRA_SELECTED_DRIVER_OPTION, mSelectedDriverOption);
            data.putExtra(EXTRA_CONFIRMED_ORIGIN, mOrigin);
            setResult(RESULT_OK, data);
            this.finish();
        } else {
            UpdateCostEstimateFragment.newInstance(mSelectedDriverOption, mNewOrigin, mDestination)
                    .show(getSupportFragmentManager(),
                    UpdateCostEstimateFragment.class.getSimpleName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            setResult(RESULT_CANCELED);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewCostEstimateAccepted() {
        Intent data = new Intent();
        data.putExtra(EXTRA_SELECTED_DRIVER_OPTION, mSelectedDriverOption);
        data.putExtra(EXTRA_CONFIRMED_ORIGIN, mNewOrigin);
        setResult(RESULT_OK, data);
        this.finish();
    }

    @Override
    public void onNewCostEstimateRejected() {

    }

}
