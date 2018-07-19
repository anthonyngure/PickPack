package ke.co.thinksynergy.movers.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anthony Ngure on 08/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class DriverOption implements Parcelable {


    private String name;
    private double length;
    private double width;
    private double height;
    private double weight;
    private double baseCost;
    private double costPerMinute;
    private double costPerKilometer;
    private Driver driver;
    private boolean selected;

    public DriverOption() {
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getCostPerMinute() {
        return costPerMinute;
    }

    public double getCostPerKilometer() {
        return costPerKilometer;
    }

    public Driver getDriver() {
        return driver;
    }

    public static class Driver implements Parcelable {
        private double latitude;
        private double longitude;
        private double distance;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getDistance() {
            return distance;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(this.latitude);
            dest.writeDouble(this.longitude);
            dest.writeDouble(this.distance);
        }

        public Driver() {
        }

        protected Driver(Parcel in) {
            this.latitude = in.readDouble();
            this.longitude = in.readDouble();
            this.distance = in.readDouble();
        }

        public static final Parcelable.Creator<Driver> CREATOR = new Parcelable.Creator<Driver>() {
            @Override
            public Driver createFromParcel(Parcel source) {
                return new Driver(source);
            }

            @Override
            public Driver[] newArray(int size) {
                return new Driver[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.length);
        dest.writeDouble(this.width);
        dest.writeDouble(this.height);
        dest.writeDouble(this.weight);
        dest.writeDouble(this.baseCost);
        dest.writeDouble(this.costPerMinute);
        dest.writeDouble(this.costPerKilometer);
        dest.writeParcelable(this.driver, flags);
    }

    protected DriverOption(Parcel in) {
        this.name = in.readString();
        this.length = in.readDouble();
        this.width = in.readDouble();
        this.height = in.readDouble();
        this.weight = in.readDouble();
        this.baseCost = in.readDouble();
        this.costPerMinute = in.readDouble();
        this.costPerKilometer = in.readDouble();
        this.driver = in.readParcelable(Driver.class.getClassLoader());
    }

    public static final Parcelable.Creator<DriverOption> CREATOR = new Parcelable.Creator<DriverOption>() {
        @Override
        public DriverOption createFromParcel(Parcel source) {
            return new DriverOption(source);
        }

        @Override
        public DriverOption[] newArray(int size) {
            return new DriverOption[size];
        }
    };
}
