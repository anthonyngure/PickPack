package ke.co.thinksynergy.movers.activity.utils;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.location.DirectionResponse;
import ke.co.thinksynergy.movers.location.GeoUtil;
import ke.co.thinksynergy.movers.location.GeocodeResponse;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.toshngure.logging.BeeLog;

/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class BottomViewManager {

    private static final String TAG = "BottomViewManager";
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.mapContainer)
    FrameLayout mMapContainer;
    @BindView(R.id.errorTV)
    TextView mErrorTV;
    @BindView(R.id.optionsLL)
    LinearLayout optionsLL;
    private Listener mListener;
    private BottomViewOptionsManager mBottomViewOptionsManager;

    private BottomViewManager(AppCompatActivity activity, Listener listener) {
        this.mListener = listener;
        this.mBottomViewOptionsManager = BottomViewOptionsManager.init(activity, listener);
        ButterKnife.bind(this, activity);
    }

    private void onStartLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorTV.setVisibility(View.GONE);
    }

    private void onFinishLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void onError(Object error) {
        mErrorTV.setVisibility(View.VISIBLE);
        mErrorTV.setText(String.valueOf(error));
    }

    public static BottomViewManager init(AppCompatActivity appCompatActivity, Listener listener) {
        return new BottomViewManager(appCompatActivity, listener);
    }

    public void onLocationChanged(Location newLocation) {
        mBottomViewOptionsManager.onLocationUpdated(newLocation);
        showNotification("onLocationChanged", false);
        LatLng latLng = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());
        GeoUtil.reverseGeocode(latLng, new GeoUtil.GeocodeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(GeocodeResponse geocodeResponse) {
                List<GeocodeResponse.Result> resultList = geocodeResponse.getResults();
                if (resultList.size() > 0) {
                    mListener.onResolveRetrievedLastLocation(resultList.get(0).getFormattedAddress());
                }
            }

            @Override
            public void onFailure(int statusCode, Object response) {
                onError("Unable to resolve Retrieved Last Location");
                BeeLog.e(TAG, "Unable to resolve Retrieved Last Location, onFailure");
                BeeLog.e(TAG, response);
            }
        });
    }

    void onDestinationChanged(AutocompletePrediction destinationPrediction) {
        onStartLoading();
        optionsLL.setVisibility(View.VISIBLE);
        mBottomViewOptionsManager.onDestinationChanged();
        showNotification("onDestinationChanged", false);
        Location originLocation = mListener.getLastLocation();
        if (originLocation == null) {
            onError("Last location not yet determined!");
            return;
        }
        LatLng latLng = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
        GeoUtil.directions(destinationPrediction, latLng, new GeoUtil.DirectionListener() {

            @Override
            public void onStart() {
                showNotification("onRoutingStart", false);
                onStartLoading();
            }

            @Override
            public void onSuccess(DirectionResponse directionResponse) {
                showNotification("onRoutingSuccess "+String.valueOf(directionResponse), false);
                if (directionResponse.getRoutes().size() > 0) {
                    mBottomViewOptionsManager.onRouteChanged(directionResponse);
                    //Now the map is at the top, request listener to show the route
                    mListener.onRouteChanged(directionResponse, () -> onFinishLoading());
                } else {
                    onError("Unable to find a route");
                }
            }

            @Override
            public void onFailure(int statusCode, Object response) {
                showNotification("Failed to resolve directions, " + String.valueOf(response), true);
                onFinishLoading();
            }
        });
    }




    public synchronized void showNotification(Object msg, boolean showSnackbar) {
        BeeLog.i(TAG, String.valueOf(msg));
        if (showSnackbar) {
            Snackbar.make(mMapContainer, String.valueOf(msg), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, view -> {
                    }).show();
        }
    }

    public void close() {
        optionsLL.setVisibility(View.GONE);
    }

    public boolean isOpen() {
        return optionsLL.getVisibility() == View.VISIBLE;
    }

    public interface Listener {

        void onResolveRetrievedLastLocation(String location);

        void onRouteChanged(@NonNull DirectionResponse directionResponse, ShowRoutesListener showRoutesListener);

        void onDriverOptionsLoaded(List<DriverOption> driverOptions);

        Location getLastLocation();
    }

    //Used to listen when {Lis}
    public interface ShowRoutesListener {
        void onFinish();
    }

}
