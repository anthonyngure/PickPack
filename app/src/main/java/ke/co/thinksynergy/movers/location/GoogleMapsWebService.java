package ke.co.thinksynergy.movers.location;

/**
 * Created by Anthony Ngure on 24/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

class GoogleMapsWebService {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";

    private String googleMapsKey;


    GoogleMapsWebService(String googleMapsKey) {
        this.googleMapsKey = googleMapsKey;
    }
}
