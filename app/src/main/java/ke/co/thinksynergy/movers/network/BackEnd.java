package ke.co.thinksynergy.movers.network;

/**
 * Created by Anthony Ngure on 04/06/2017.
 * Email : anthonyngure25@gmail.com.
 * http://www.toshngure.co.ke
 */

public class BackEnd {

    //public static final String BASE_URL = "http://pickpack.thinksynergy.co.ke/api/v1";
    public static final String BASE_URL = "https://pickpack.toshngure.co.ke/api/v1";

    public static final class EndPoints {
        public static final String NEARBY_DRIVERS = "/drivers/nearby";
        public static final String DEVICE_TOKEN = "/deviceToken";
        public static final String PHONE_SIGN_IN = "/auth/phoneSignIn";
        public static final String VERIFY_PHONE = "/auth/verifyPhone";
        public static final String FACEBOOK_SIGN_IN = "/auth/facebookSignIn";
        public static final String USER_ADD_NAME = "/users/name";
        public static final String OPTION_DRIVERS = "/drivers/options";
    }

    public static final class Params {
        public static final String CODE = "code";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String PHONE = "phone";
        public static final String SMS_SENT = "smsSent";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String FACEBOOK_PICTURE_URL = "facebookPictureUrl";
        public static final String FACEBOOK_ID = "facebookId";
        public static final String ORIGIN_LATITUDE = "originLatitude";
        public static final String ORIGIN_LONGITUDE = "originLongitude";
        public static final String DESTINATION_LATITUDE = "destinationLatitude";
        public static final String DESTINATION_LONGITUDE = "destinationLongitude";
        public static final String DISTANCE = "distance";
        public static final String TIME = "time";
    }

    public static final class Response {
        public static final String DATA = "data";
        public static final String META = "meta";
    }
}
