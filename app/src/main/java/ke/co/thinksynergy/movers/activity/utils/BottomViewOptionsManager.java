package ke.co.thinksynergy.movers.activity.utils;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleRecyclerView;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.activity.ConfirmPickUpActivity;
import ke.co.thinksynergy.movers.cell.DriverOptionCell;
import ke.co.thinksynergy.movers.location.DirectionResponse;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.thinksynergy.movers.tasks.ParseDriverOptionsTask;
import ke.co.toshngure.basecode.networking.ConnectionListener;
import ke.co.toshngure.basecode.networking.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.logging.BeeLog;

/**
 * Created by Anthony Ngure on 09/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class BottomViewOptionsManager implements
        SimpleCell.OnCellClickListener<DriverOption>,
        ConnectionListener {

    private static final String TAG = "BottomSheetOptionsManag";
    @BindView(R.id.optionsRV)
    SimpleRecyclerView mOptionsRV;
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn;
    @BindView(R.id.distanceTimeTV)
    TextView mDistanceTimeTV;
    private List<DriverOption> mDriverOptions = new ArrayList<>();
    private AppCompatActivity mContext;
    private DirectionResponse.Leg mLeg;
    private View loadingLL;
    private TextView loadingTV;
    private View errorLL;
    private DriverOption mSelectedDriverOption;
    private double mOriginLatitude;
    private double mOriginLongitude;
    private BottomViewManager.Listener mBottomViewManagerListener;

    private BottomViewOptionsManager(AppCompatActivity activity, BottomViewManager.Listener listener) {
        this.mContext = activity;
        ButterKnife.bind(this, activity);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.view_empty_options_simple_recycler_view, null);
        emptyView.setMinimumWidth(BaseUtils.getScreenWidth(mContext));
        this.loadingTV = emptyView.findViewById(R.id.loadingTV);
        this.loadingLL = emptyView.findViewById(R.id.loadingLL);
        this.errorLL = emptyView.findViewById(R.id.errorLL);
        this.errorLL.setOnClickListener(view -> connect());
        this.mOptionsRV.setEmptyStateView(emptyView);
        this.mBottomViewManagerListener = listener;
    }

    static BottomViewOptionsManager init(AppCompatActivity activity, BottomViewManager.Listener listener) {
        return new BottomViewOptionsManager(activity, listener);
    }

    void onRouteChanged(DirectionResponse directionResponse) {
        BeeLog.i(TAG, "onRouteChanged, DriverOptions = "+mDriverOptions.size());
        loadingTV.setText(R.string.message_waiting);
        mDistanceTimeTV.setVisibility(View.VISIBLE);
        this.mLeg = directionResponse.getRoutes().get(0).getLegs().get(0);
        mDistanceTimeTV.setText(mContext.getString(R.string.distance_time,
                mLeg.getDistance().getText(), mLeg.getDuration().getText()));
        if (mDriverOptions.size() == 0) {
            //Connect to the api to get options
            mOriginLatitude = mLeg.getStartLocation().getLat();
            mOriginLongitude = mLeg.getStartLocation().getLng();
            connect();
        } else {

            onCellClicked(mDriverOptions.get(0));
        }

    }

    void onLocationUpdated(Location location) {
        loadingTV.setText(R.string.finding_directions);
        BeeLog.i(TAG, "onLocationUpdated, location = "+String .valueOf(location));
        mOriginLatitude = location.getLatitude();
        mOriginLongitude = location.getLongitude();

        //Update options and nearby drivers
        connect();
    }

    void onDestinationChanged(){
        loadingTV.setText(R.string.finding_directions);
        mLeg = null;
        mOptionsRV.removeAllCells();
        mDistanceTimeTV.setVisibility(View.GONE);
        mConfirmBtn.setText(R.string.confirm);
        mConfirmBtn.setEnabled(false);
    }

    @Override
    public void onCellClicked(@NonNull DriverOption driverOption) {
        mSelectedDriverOption = driverOption;
        mConfirmBtn.setEnabled(false);
        List<DriverOptionCell> cells = new ArrayList<>();
        for (DriverOption option : mDriverOptions) {
            option.setSelected(option.getName().equalsIgnoreCase(driverOption.getName()));
            DriverOptionCell cell = new DriverOptionCell(option, mLeg);
            cell.setOnCellClickListener(this);
            cells.add(cell);
        }
        mOptionsRV.removeAllCells();
        mOptionsRV.addCells(cells);
        mConfirmBtn.setEnabled(true);
        mConfirmBtn.setText(mContext.getString(R.string.confirm_driver_option, driverOption.getName()));

    }

    @Override
    public void connect() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.OPTION_DRIVERS);
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.ORIGIN_LATITUDE, mOriginLatitude);
        params.put(BackEnd.Params.ORIGIN_LONGITUDE, mOriginLongitude);
        Client.getInstance().getClient().get(url, params, new ResponseHandler(this));
        BeeLog.i(TAG, "Params: " + String.valueOf(params));
    }

    @Override
    public void onConnectionStarted() {
        errorLL.setVisibility(View.GONE);
        loadingLL.setVisibility(View.VISIBLE);
        mOptionsRV.showEmptyStateView();
    }

    @Override
    public void onConnectionFailed(int i, JSONObject jsonObject) {
        errorLL.setVisibility(View.VISIBLE);
        loadingLL.setVisibility(View.GONE);
    }

    @Override
    public void onConnectionSuccess(JSONObject jsonObject) {
        new ParseDriverOptionsTask(driverOptions -> {
            mBottomViewManagerListener.onDriverOptionsLoaded(driverOptions);
            mDriverOptions.clear();
            mDriverOptions.addAll(driverOptions);
            mOptionsRV.removeAllCells();
            if (mDriverOptions.size() > 0 && mLeg != null) {
                //Select option one by default
                onCellClicked(mDriverOptions.get(0));
            }

        }).execute(jsonObject);
    }

    @Override
    public void onConnectionProgress(int i) {

    }

    @Override
    public Context getListenerContext() {
        return mContext;
    }


    @OnClick(R.id.confirmBtn)
    public void onConfirmBtnClicked(View view) {
        LatLng origin = new LatLng(mLeg.getStartLocation().getLat(), mLeg.getStartLocation().getLng());
        LatLng destination = new LatLng(mLeg.getEndLocation().getLat(), mLeg.getEndLocation().getLng());
        ConfirmPickUpActivity.start(mContext, origin, destination, mSelectedDriverOption);
    }
}
