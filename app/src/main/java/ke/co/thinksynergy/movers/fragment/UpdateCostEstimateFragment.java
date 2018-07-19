package ke.co.thinksynergy.movers.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.location.DirectionResponse;
import ke.co.thinksynergy.movers.location.GeoUtil;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.utils.Spanny;
import ke.co.toshngure.logging.BeeLog;
import ke.co.toshngure.views.NetworkImage;

/**
 * Created by Anthony Ngure on 18/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class UpdateCostEstimateFragment extends BottomSheetDialogFragment {

    private static final String TAG = "UpdateCostEstimateFragm";
    public static final String ARG_SELECTED_DRIVER_OPTION = "arg_selected_driver_option";
    public static final String ARG_ORIGIN = "arg_origin";
    public static final String ARG_DESTINATION = "arg_destination";
    @BindView(R.id.avatarIV)
    NetworkImage avatarIV;
    @BindView(R.id.nameTV)
    TextView nameTV;
    @BindView(R.id.volumeDetailsTV)
    TextView volumeDetailsTV;
    @BindView(R.id.volumeTV)
    TextView volumeTV;
    @BindView(R.id.acceptBtn)
    Button acceptBtn;
    Unbinder unbinder;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.costTV)
    TextView costTV;
    @BindView(R.id.titleTV)
    TextView titleTV;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;

    private LatLng mOrigin;
    private LatLng mDestination;
    private DriverOption mSelectedDriverOption;
    private Listener mListener;

    public UpdateCostEstimateFragment() {
    }

    public static UpdateCostEstimateFragment newInstance(DriverOption selectedDriverOption, LatLng origin, LatLng destination) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_SELECTED_DRIVER_OPTION, selectedDriverOption);
        args.putParcelable(ARG_ORIGIN, origin);
        args.putParcelable(ARG_DESTINATION, destination);
        UpdateCostEstimateFragment fragment = new UpdateCostEstimateFragment();
        fragment.setCancelable(false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        } else {
            throw new IllegalArgumentException(TAG + " must be add to an activity that implements " + TAG + ".Listener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedDriverOption = getArguments().getParcelable(ARG_SELECTED_DRIVER_OPTION);
        mOrigin = getArguments().getParcelable(ARG_ORIGIN);
        mDestination = getArguments().getParcelable(ARG_DESTINATION);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_cost_estimate, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameTV.setText(mSelectedDriverOption.getName());
        volumeDetailsTV.setText(new Spanny("L x W x H", new AbsoluteSizeSpan(10, false)));
        Spanny volumeSpanny = new Spanny()
                .append(String.valueOf(mSelectedDriverOption.getLength()))
                .append(" x ").append(String.valueOf(mSelectedDriverOption.getWidth()))
                .append(" x ").append(String.valueOf(mSelectedDriverOption.getHeight()));
        volumeTV.setText(volumeSpanny);

        GeoUtil.directions(mOrigin, mDestination, new GeoUtil.DirectionListener() {

            @Override
            public void onStart() {
                showNotification("onRoutingStart", false);
                onStartLoading();
            }

            @Override
            public void onSuccess(DirectionResponse directionResponse) {
                showNotification("onRoutingSuccess "+String.valueOf(directionResponse), false);
                if (directionResponse.getRoutes().size() > 0) {
                    DirectionResponse.Leg leg = directionResponse.getRoutes().get(0).getLegs().get(0);
                    double timeInMinutes = leg.getDuration().getValue() / 60;
                    double timeCost = timeInMinutes * mSelectedDriverOption.getCostPerMinute();

                    double distanceInKM = leg.getDistance().getValue() / 1000;
                    double distanceCost = distanceInKM * mSelectedDriverOption.getCostPerKilometer();

                    double cost = mSelectedDriverOption.getBaseCost() + timeCost + distanceCost;
                    Spanny costSpanny = new Spanny("KES ")
                            .append(String.valueOf(cost), new StyleSpan(Typeface.BOLD));
                    costTV.setText(costSpanny);
                    acceptBtn.setEnabled(true);
                    onFinishLoading();
                } else {
                    showNotification("Unable to find a route", true);
                }
            }

            @Override
            public void onFailure(int statusCode, Object response) {
                showNotification("Failed to resolve directions, " + String.valueOf(response), true);
                onFinishLoading();
            }
        });;
    }

    private void onStartLoading() {
        progressBar.setVisibility(View.VISIBLE);
        titleTV.setVisibility(View.VISIBLE);
    }

    private void onFinishLoading() {
        progressBar.setVisibility(View.GONE);
        titleTV.setVisibility(View.GONE);
    }

    public synchronized void showNotification(Object msg, boolean showSnackbar) {
        BeeLog.i(TAG, String.valueOf(msg));
        if (showSnackbar) {
            Snackbar.make(volumeDetailsTV, String.valueOf(msg), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, view -> {
                    }).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.cancelBtn)
    public void onCancelBtnClicked() {
        mListener.onNewCostEstimateRejected();
        dismissAllowingStateLoss();
    }

    @OnClick(R.id.acceptBtn)
    public void onAcceptBtnClicked() {
        dismissAllowingStateLoss();
        mListener.onNewCostEstimateAccepted();
    }


    public interface Listener {
        void onNewCostEstimateAccepted();

        void onNewCostEstimateRejected();
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialog;
    }
}
