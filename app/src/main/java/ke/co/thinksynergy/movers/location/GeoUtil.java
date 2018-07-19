package ke.co.thinksynergy.movers.location;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ke.co.thinksynergy.movers.network.Client;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.logging.BeeLog;

/**
 * Created by Anthony Ngure on 18/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class GeoUtil {

    private static final String TAG = "GeoUtil";
    private static final String GEOCODE_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    private static GeoUtil mInstance;
    private static String mGoogleMapsKey;
    private LatLng defaultLocation;

    private GeoUtil(LatLng defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public static void init(LatLng defaultLocation, String googleMapsKey) {
        if (mInstance == null) {
            mInstance = new GeoUtil(defaultLocation);
            mGoogleMapsKey = googleMapsKey;
        } else {
            throw new IllegalArgumentException("GeoUtil should only be initialized once," +
                    " most probably in the Application class");
        }
    }

    public static GeoUtil getInstance() {
        if (mInstance == null) {
            throw new IllegalArgumentException("GeoUtil has not been initialized," +
                    " it should be initialized once, most probably in the Application class");
        }
        return mInstance;
    }

    public LatLng getDefaultLocation() {
        return defaultLocation;
    }


    public static void geocode(AutocompletePrediction autocompletePrediction, GeocodeListener geocodeListener) {
        RequestParams params = new RequestParams();
        params.put("address", autocompletePrediction.getFullText(null));
        params.put("region", "KE");
        params.put("key", mGoogleMapsKey);
        Client.getInstance().getClient().get(GEOCODE_BASE_URL, params, new GeocodeResponseHandler(geocodeListener));
    }

    public static void reverseGeocode(LatLng latLng, GeocodeListener geocodeListener) {
        RequestParams params = new RequestParams();
        params.put("latlng", latLng.latitude + "," + latLng.longitude);
        params.put("key", mGoogleMapsKey);
        Client.getInstance().getClient().get(GEOCODE_BASE_URL, params, new GeocodeResponseHandler(geocodeListener));

    }

    private static class GeocodeResponseHandler extends JsonHttpResponseHandler {
        private GeocodeListener mGeocodeListener;

        private GeocodeResponseHandler(GeocodeListener geocodeListener) {
            this.mGeocodeListener = geocodeListener;
        }

        @Override
        public void onStart() {
            super.onStart();
            mGeocodeListener.onStart();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            GeocodeResponse geocodeResponse = BaseUtils.getSafeGson()
                    .fromJson(response.toString(), GeocodeResponse.class);
            mGeocodeListener.onSuccess(geocodeResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            mGeocodeListener.onFailure(statusCode, responseString);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            mGeocodeListener.onFailure(statusCode, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            mGeocodeListener.onFailure(statusCode, errorResponse);
        }
    }

    public interface GeocodeListener {

        void onStart();

        void onSuccess(GeocodeResponse geocodeResponse);

        void onFailure(int statusCode, Object response);
    }

    public static void directions(LatLng origin, LatLng destination, DirectionListener directionListener) {
        directions(origin.latitude + "," + origin.longitude,
                destination.latitude + "," + destination.longitude, directionListener );
    }

    public static void directions(AutocompletePrediction destination, LatLng origin, DirectionListener directionListener) {
        directions(origin.latitude + "," + origin.longitude,
                "place_id:" + destination.getPlaceId(), directionListener );
    }

    private static void directions(String originParam, String destinationParam, DirectionListener directionListener) {
        RequestParams params = new RequestParams();
        params.put("origin", originParam);
        params.put("destination", destinationParam);
        params.put("mode", "driving");
        params.put("region", "KE");
        params.put("key", mGoogleMapsKey);
        BeeLog.i(TAG, "Params = "+params);
        Client.getInstance().getClient().get(DIRECTIONS_BASE_URL,
                params, new DirectionsResponseHandler(directionListener));
    }

    private static class DirectionsResponseHandler  extends JsonHttpResponseHandler{

        private DirectionListener mDirectionListener;

        public DirectionsResponseHandler(DirectionListener directionListener) {
            this.mDirectionListener = directionListener;
        }

        @Override
        public void onStart() {
            super.onStart();
            mDirectionListener.onStart();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            DirectionResponse directionResponse = BaseUtils.getSafeGson()
                    .fromJson(response.toString(), DirectionResponse.class);
            mDirectionListener.onSuccess(directionResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            mDirectionListener.onFailure(statusCode, responseString);

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            mDirectionListener.onFailure(statusCode, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            mDirectionListener.onFailure(statusCode, errorResponse);
        }

    }

    public interface DirectionListener {

        void onStart();

        void onSuccess(DirectionResponse directionResponse);

        void onFailure(int statusCode, Object response);
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    public static List<LatLng> decodePolyline(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


}
