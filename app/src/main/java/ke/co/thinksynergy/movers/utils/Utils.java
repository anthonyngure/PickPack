package ke.co.thinksynergy.movers.utils;

import android.widget.EditText;

import java.util.Locale;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.model.DriverOption;

/**
 * Created by Anthony Ngure on 08/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class Utils {

    public static String toDistanceString(double distance) {
        String distanceString;
        if (distance < 0.5) {
            distanceString = String.format(Locale.ENGLISH, "%.2f", distance * 1000) + " m";
        } else {
            distanceString = String.format(Locale.ENGLISH, "%.2f", distance) + " km";
        }

        return distanceString;
    }

    public static void updateWithLastUsedOrigin(EditText originET){

    }

    public static int getDriverDrawable(DriverOption driverOption){
        return R.drawable.icon_car;
    }
}
