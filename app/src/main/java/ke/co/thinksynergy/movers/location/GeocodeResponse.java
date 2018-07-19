package ke.co.thinksynergy.movers.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Anthony Ngure on 26/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class GeocodeResponse {


    @SerializedName("results")
    private List<Result> results;
    @SerializedName("status")
    private String status;

    public List<Result> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public static class AddressComponent {
        @SerializedName("long_name")
        private String longName;
        @SerializedName("short_name")
        private String shortName;
        @SerializedName("types")
        private List<String> types;

        public String getLongName() {
            return longName;
        }

        public String getShortName() {
            return shortName;
        }

        public List<String> getTypes() {
            return types;
        }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public static class Northeast {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public static class Southwest {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public static class Viewport {
        @SerializedName("northeast")
        private Northeast northeast;
        @SerializedName("southwest")
        private Southwest southwest;

        public Northeast getNortheast() {
            return northeast;
        }

        public Southwest getSouthwest() {
            return southwest;
        }
    }

    public static class Geometry {
        @SerializedName("location")
        private Location location;
        @SerializedName("location_type")
        private String locationType;
        @SerializedName("viewport")
        private Viewport viewport;

        public Location getLocation() {
            return location;
        }

        public String getLocationType() {
            return locationType;
        }

        public Viewport getViewport() {
            return viewport;
        }
    }

    public static class Result {
        @SerializedName("address_components")
        private List<AddressComponent> addressComponents;
        @SerializedName("formatted_address")
        private String formattedAddress;
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("types")
        private List<String> types;

        public List<AddressComponent> getAddressComponents() {
            return addressComponents;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public String getPlaceId() {
            return placeId;
        }

        public List<String> getTypes() {
            return types;
        }
    }
}
