package ke.co.thinksynergy.movers.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.utils.Spanny;
import ke.co.toshngure.views.NetworkImage;

/**
 * Created by Anthony Ngure on 18/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class GetDriverFragment extends BottomSheetDialogFragment {

    public static final String ARG_SELECTED_DRIVER_OPTION = "arg_selected_driver_option";
    private static final String ARG_DURATION = "arg_duration";
    private static final String ARG_DISTANCE = "arg_distance";
    public static final String ARG_ORIGIN = "arg_origin";
    public static final String ARG_DESTINATION = "arg_destination";
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.titleTV)
    TextView titleTV;
    @BindView(R.id.avatarIV)
    NetworkImage avatarIV;
    @BindView(R.id.nameTV)
    TextView nameTV;
    @BindView(R.id.volumeDetailsTV)
    TextView volumeDetailsTV;
    @BindView(R.id.volumeTV)
    TextView volumeTV;
    @BindView(R.id.costTV)
    TextView costTV;

    private LatLng mOrigin;
    private LatLng mDestination;

    Unbinder unbinder;

    private DriverOption mSelectedDriverOption;
    private int mDuration;
    private int mDistance;

    public GetDriverFragment() {
    }

    public static GetDriverFragment newInstance(DriverOption selectedDriverOption, LatLng origin,
                                                LatLng destination, int duration, int distance) {

        Bundle args = new Bundle();
        GetDriverFragment fragment = new GetDriverFragment();
        args.putParcelable(ARG_SELECTED_DRIVER_OPTION, selectedDriverOption);
        args.putParcelable(ARG_ORIGIN, origin);
        args.putParcelable(ARG_DESTINATION, destination);
        args.putInt(ARG_DURATION, duration);
        args.putInt(ARG_DISTANCE, distance);
        //fragment.setCancelable(false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedDriverOption = getArguments().getParcelable(ARG_SELECTED_DRIVER_OPTION);
        mOrigin = getArguments().getParcelable(ARG_ORIGIN);
        mDestination = getArguments().getParcelable(ARG_DESTINATION);
        mDuration = getArguments().getInt(ARG_DURATION, 0);
        mDistance = getArguments().getInt(ARG_DISTANCE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_driver, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTV.setText(new Spanny("Connecting you to the nearest ").append(mSelectedDriverOption.getName()));

        nameTV.setText(mSelectedDriverOption.getName());
        volumeDetailsTV.setText(new Spanny("L x W x H", new AbsoluteSizeSpan(10, false)));
        Spanny volumeSpanny = new Spanny()
                .append(String.valueOf(mSelectedDriverOption.getLength()))
                .append(" x ").append(String.valueOf(mSelectedDriverOption.getWidth()))
                .append(" x ").append(String.valueOf(mSelectedDriverOption.getHeight()));
        volumeTV.setText(volumeSpanny);

        double timeInMinutes = mDuration / 60;
        double timeCost = timeInMinutes * mSelectedDriverOption.getCostPerMinute();

        double distanceInKM = mDistance / 1000;
        double distanceCost = distanceInKM * mSelectedDriverOption.getCostPerKilometer();

        double cost = mSelectedDriverOption.getBaseCost() + timeCost + distanceCost;
        Spanny costSpanny = new Spanny("KES ")
                .append(String.valueOf(cost), new StyleSpan(Typeface.BOLD));
        costTV.setText(costSpanny);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
