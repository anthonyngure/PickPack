package ke.co.thinksynergy.movers.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.jaychang.srv.Updatable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.location.DirectionResponse;
import ke.co.thinksynergy.movers.model.DriverOption;
import ke.co.thinksynergy.movers.utils.Spanny;
import ke.co.toshngure.views.NetworkImage;

/**
 * Created by Anthony Ngure on 04/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class DriverOptionCell extends SimpleCell<DriverOption, DriverOptionCell.DriverOptionViewHolder>
        implements Updatable<DriverOption> {

    private final DirectionResponse.Leg mLeg;

    public DriverOptionCell(@NonNull DriverOption item, DirectionResponse.Leg leg) {
        super(item);
        this.mLeg = leg;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cell_driver_option;
    }

    @NonNull
    @Override
    protected DriverOptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull View view) {
        return new DriverOptionViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull DriverOptionViewHolder driverOptionViewHolder, int i,
                                    @NonNull Context context, Object o) {
        driverOptionViewHolder.bind(getItem(), mLeg);
    }

    @Override
    public boolean areContentsTheSame(@NonNull DriverOption driverOption) {
        return getItem().isSelected() && driverOption.isSelected();
    }

    @Override
    public Object getChangePayload(@NonNull DriverOption driverOption) {
        return driverOption;
    }

    static class DriverOptionViewHolder extends SimpleViewHolder {

        @BindView(R.id.avatarIV)
        NetworkImage avatarIV;
        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.volumeTV)
        TextView volumeTV;
        @BindView(R.id.volumeDetailsTV)
        TextView volumeDetailsTV;
        @BindView(R.id.costTV)
        TextView costTV;
        @BindView(R.id.checkedIV)
        ImageView checkedIV;

        DriverOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bind(DriverOption item, DirectionResponse.Leg leg) {

            nameTV.setText(item.getName());

            double timeInMinutes = leg.getDuration().getValue() / 60;
            double timeCost = timeInMinutes * item.getCostPerMinute();

            double distanceInKM = leg.getDistance().getValue() / 1000;
            double distanceCost = distanceInKM * item.getCostPerKilometer();

            double cost = item.getBaseCost() + timeCost + distanceCost;
            Spanny costSpanny = new Spanny("KES ")
                    .append(String.valueOf(cost), new StyleSpan(Typeface.BOLD));
            costTV.setText(costSpanny);

            volumeDetailsTV.setText(new Spanny("L x W x H", new AbsoluteSizeSpan(10, false)));
            Spanny volumeSpanny = new Spanny()
                    .append(String.valueOf(item.getLength()))
                    .append(" x ").append(String.valueOf(item.getWidth()))
                    .append(" x ").append(String.valueOf(item.getHeight()));
            volumeTV.setText(volumeSpanny);

            checkedIV.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
        }
    }
}
