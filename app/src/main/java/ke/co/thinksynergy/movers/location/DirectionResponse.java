package ke.co.thinksynergy.movers.location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Anthony Ngure on 26/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class DirectionResponse {


    @SerializedName("geocoded_waypoints")
    private List<GeocodedWaypoint> geocodedWaypoints;
    @SerializedName("routes")
    private List<Route> routes;
    @SerializedName("status")
    private String status;

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public String getStatus() {
        return status;
    }

    public static class GeocodedWaypoint {
        @SerializedName("geocoder_status")
        private String geocoderStatus;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("types")
        private List<String> types;

        public String getGeocoderStatus() {
            return geocoderStatus;
        }

        public String getPlaceId() {
            return placeId;
        }

        public List<String> getTypes() {
            return types;
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

    public static class Bounds {
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

    public static class Distance {
        @SerializedName("text")
        private String text;
        @SerializedName("value")
        private int value;

        public String getText() {
            return text;
        }

        public int getValue() {
            return value;
        }
    }

    public static class Duration {
        @SerializedName("text")
        private String text;
        @SerializedName("value")
        private int value;

        public String getText() {
            return text;
        }

        public int getValue() {
            return value;
        }
    }

    public static class EndLocation {
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

    public static class StartLocation {
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

    public static class Polyline {
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }
    }

    public static class Step {
        @SerializedName("distance")
        private Distance distance;
        @SerializedName("duration")
        private Duration duration;
        @SerializedName("end_location")
        private EndLocation endLocation;
        @SerializedName("html_instructions")
        private String htmlInstructions;
        @SerializedName("polyline")
        private Polyline polyline;
        @SerializedName("start_location")
        private StartLocation startLocation;
        @SerializedName("travel_mode")
        private String travelMode;

        public Distance getDistance() {
            return distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public EndLocation getEndLocation() {
            return endLocation;
        }

        public String getHtmlInstructions() {
            return htmlInstructions;
        }

        public Polyline getPolyline() {
            return polyline;
        }

        public StartLocation getStartLocation() {
            return startLocation;
        }

        public String getTravelMode() {
            return travelMode;
        }
    }


    public static class Leg {
        @SerializedName("distance")
        private Distance distance;
        @SerializedName("duration")
        private Duration duration;
        @SerializedName("end_address")
        private String endAddress;
        @SerializedName("end_location")
        private EndLocation endLocation;
        @SerializedName("start_address")
        private String startAddress;
        @SerializedName("start_location")
        private StartLocation startLocation;
        @SerializedName("steps")
        private List<Step> steps;
        @SerializedName("traffic_speed_entry")

        public Distance getDistance() {
            return distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public EndLocation getEndLocation() {
            return endLocation;
        }

        public String getStartAddress() {
            return startAddress;
        }

        public StartLocation getStartLocation() {
            return startLocation;
        }

        public List<Step> getSteps() {
            return steps;
        }

    }

    public static class OverviewPolyline {
        @SerializedName("points")
        private String points;

        public String getPoints() {
            return points;
        }
    }


    public static class Route {
        @SerializedName("bounds")
        private Bounds bounds;
        @SerializedName("copyrights")
        private String copyrights;
        @SerializedName("legs")
        private List<Leg> legs;
        @SerializedName("overview_polyline")
        private OverviewPolyline overviewPolyline;

        public Bounds getBounds() {
            return bounds;
        }

        public String getCopyrights() {
            return copyrights;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }
    }
}
