package ke.co.thinksynergy.movers.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.activity.utils.BottomViewManager;
import ke.co.thinksynergy.movers.activity.utils.DrawerManager;
import ke.co.thinksynergy.movers.activity.utils.OriginDestinationInputManager;
import ke.co.thinksynergy.movers.fragment.GetDriverFragment;
import ke.co.thinksynergy.movers.location.DirectionResponse;
import ke.co.thinksynergy.movers.location.GeoUtil;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.utils.Utils;
import ke.co.toshngure.logging.BeeLog;

public class MainActivity extends BaseActivity implements
        BottomViewManager.Listener,
        OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    protected static final int DEFAULT_ZOOM = 14;
    protected GoogleMap mGoogleMap;

    protected Location mLastLocation;
    protected Marker mOriginMapMarker;
    protected Polyline mPolyLine;

    private List<DriverOption> mDriverOptions = new ArrayList<>();
    private ArrayList<Marker> mNearbyDriversMarkerList = new ArrayList<>();
    private DrawerManager mDrawerManager;
    private Marker mDestinationMarker;
    private BottomViewManager mBottomViewManager;
    private OriginDestinationInputManager mOriginDestinationInputManager;
    private DirectionResponse.Leg mLeg;
    private DirectionResponse mDirectionResponse;
    private boolean mFittingMarkers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDrawerManager = DrawerManager.init(this, getToolbar());

        mBottomViewManager = BottomViewManager.init(this, this);

        mOriginDestinationInputManager = OriginDestinationInputManager.init(this, mBottomViewManager);

        //Check If Google Services Is Available

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ke.co.thinksynergy.movers", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                BeeLog.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        //mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

        // mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.blue_essense));
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.copy_style));

        //Add our default location
        mBottomViewManager.showNotification("Adding default location", false);
        updateOriginMarker(GeoUtil.getInstance().getDefaultLocation());

        //Get last location
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    mBottomViewManager.showNotification("getLastLocation,addOnSuccessListener",
                            false);
                    if (location != null) {
                        // Assign the new location
                        mLastLocation = location;

                        // We are going to get the address for the new position
                        mBottomViewManager.onLocationChanged(location);

                        //Add origin pointer to the map at location
                        updateOriginMarker(new LatLng(location.getLatitude(), location.getLongitude()));

                    } else {
                        mBottomViewManager.onError("Couldn't get your location Make sure location is enabled on the device");
                    }

                });

    }


    private void updateOriginMarker(LatLng latLng) {
        if (mLeg == null && mGoogleMap != null) {

            if (mOriginMapMarker != null) {
                mOriginMapMarker.remove();
            }
            mOriginMapMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    @Nullable
    @Override
    public Location getLastLocation() {
        return mLastLocation;
    }

    @Override
    public void onResolveRetrievedLastLocation(String retrievedLastLocationName) {
        mOriginDestinationInputManager.onResolveRetrievedLastLocation(retrievedLastLocationName);
    }

    @OnClick(R.id.fab)
    public void onFabClicked(FloatingActionButton fab) {
        BeeLog.i(TAG, "mFittingMarkers = "+mFittingMarkers);
        if (mFittingMarkers) {
            if (mLastLocation != null) {
                LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                if (mLeg != null) {
                    fab.setImageResource(R.drawable.ic_directions_black_24dp);
                } else {
                    fab.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
                }
                mFittingMarkers = false;
            }
        } else {
            fab.setImageResource(R.drawable.ic_gps_fixed_black_24dp);
            fitMarkers();
        }
    }

    @Override
    public void onRouteChanged(@NonNull DirectionResponse directionResponse,
                               BottomViewManager.ShowRoutesListener listener) {

        mDirectionResponse = directionResponse;
        mLeg = directionResponse.getRoutes().get(0).getLegs().get(0);
        mOriginDestinationInputManager.onDirectionResolved(mLeg);

        //Remove poly line
        if (mPolyLine != null) {
            mPolyLine.remove();
        }

        if (mOriginMapMarker != null) {
            mOriginMapMarker.remove();
        }

        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
        }

        /*Add markers*/

        List<LatLng> points = GeoUtil.decodePolyline(directionResponse.getRoutes().get(0)
                .getOverviewPolyline().getPoints());

        MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .position(points.get(points.size() - 1));
        mDestinationMarker = mGoogleMap.addMarker(destinationMarkerOptions);

        MarkerOptions originMarkerOptions = new MarkerOptions()
                .position(points.get(0));
        mOriginMapMarker = mGoogleMap.addMarker(originMarkerOptions);


        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.addAll(points);

        mPolyLine = mGoogleMap.addPolyline(polylineOptions);

        new Handler().postDelayed(this::fitMarkers, 1000);


        listener.onFinish();
    }

    @Override
    public void onDriverOptionsLoaded(List<DriverOption> driverOptions) {
        mBottomViewManager.showNotification("onDriverOptionsLoaded", false);
        mDriverOptions.clear();
        mDriverOptions.addAll(driverOptions);

        if (mGoogleMap != null) {
            /*Remove current nearby drivers markers*/
            for (Marker marker : mNearbyDriversMarkerList) {
                marker.remove();
            }
            mNearbyDriversMarkerList.clear();

            /*Add new services markers*/
            for (DriverOption driverOption : mDriverOptions) {
                BeeLog.i(TAG, "Adding Marker for " + driverOption.getName());
                LatLng latLong = new LatLng(driverOption.getDriver().getLatitude(),
                        driverOption.getDriver().getLongitude());
                Marker marker = mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(latLong)
                                .snippet(Utils.toDistanceString(driverOption.getDriver().getDistance()))
                                .title(driverOption.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(createMakerIcon(Utils.getDriverDrawable(driverOption)))));
                mNearbyDriversMarkerList.add(marker);
            }
            fitMarkers();
        }
    }


    private Bitmap createMakerIcon(@DrawableRes int iconRes) {
        //Set Custom BitMap for Pointer
        int height = 36;
        int width = 24;
        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(iconRes);
        bitmapDraw.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC);
        Bitmap b = bitmapDraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mDrawerManager.getDrawer() != null && mDrawerManager.getDrawer().isDrawerOpen()) {
            mDrawerManager.getDrawer().closeDrawer();
        } else if (mBottomViewManager.isOpen()) {
            if (mDestinationMarker != null) {
                mDestinationMarker.remove();
            }
            if (mPolyLine != null) {
                mPolyLine.remove();
            }
            updateOriginMarker(mOriginMapMarker.getPosition());
            mBottomViewManager.close();
        } else if (mOriginDestinationInputManager.isOpen()) {
            mOriginDestinationInputManager.close();
        } else {
            super.onBackPressed();
        }
    }


    private void fitMarkers() {
        BeeLog.i(TAG, "fitMarkers");
        /*Show route markers pr*/
        FrameLayout mMapContainer = findViewById(R.id.mapContainer);
        View bottomLL = findViewById(R.id.bottomLL);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mOriginMapMarker != null) {
            builder.include(mOriginMapMarker.getPosition());
        }
        if (mDestinationMarker != null) {
            builder.include(mDestinationMarker.getPosition());
        }
        if (mDirectionResponse != null) {
            DirectionResponse.Northeast northeast = mDirectionResponse.getRoutes().get(0).getBounds().getNortheast();
            DirectionResponse.Southwest southwest = mDirectionResponse.getRoutes().get(0).getBounds().getSouthwest();
            builder.include(new LatLng(northeast.getLat(), northeast.getLng()));
            builder.include(new LatLng(southwest.getLat(), southwest.getLng()));
        }
        for (DriverOption driverOption : mDriverOptions) {
            LatLng latLng = new LatLng(driverOption.getDriver().getLatitude(),
                    driverOption.getDriver().getLongitude());
            builder.include(latLng);
        }

        LatLngBounds bounds = builder.build();

        //int width = getResources().getDisplayMetrics().widthPixels;
        int width = mMapContainer.getWidth();
        BeeLog.i(TAG, "Map Container Width " + width);
        BeeLog.i(TAG, "Bottom Sheet Height " + bottomLL.getHeight());
        BeeLog.i(TAG, "Map Container Height " + mMapContainer.getHeight());
        int height = mMapContainer.getHeight() - bottomLL.getHeight();
        int padding = (int) (width * 0.10); //
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mGoogleMap.animateCamera(cameraUpdate);

        mFittingMarkers = true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConfirmPickUpActivity.REQUEST_CONFIRM_PICKUP && resultCode == RESULT_OK) {

            // TODO: 19/01/2018 If the origin is changed, here you should get the new route and update the current
            DriverOption selectedDriverOption = data.getParcelableExtra(ConfirmPickUpActivity.EXTRA_SELECTED_DRIVER_OPTION);
            LatLng origin = data.getParcelableExtra(ConfirmPickUpActivity.EXTRA_CONFIRMED_ORIGIN);
            LatLng destination = mDestinationMarker.getPosition();
            int distance = mLeg.getDistance().getValue();
            int duration = mLeg.getDuration().getValue();
            GetDriverFragment.newInstance(selectedDriverOption, origin, destination, distance,
                    duration).show(getSupportFragmentManager(), GetDriverFragment.class.getSimpleName());
            mBottomViewManager.close();
            new Handler().postDelayed(this::fitMarkers, 1000);
        }
    }
}
