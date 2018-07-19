package ke.co.thinksynergy.movers.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Anthony Ngure on 30/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class FacebookUser {


    @SerializedName("picture")
    private Picture picture;
    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("gender")
    private String gender;

    public Picture getPicture() {
        return picture;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public static class Data {
        @SerializedName("url")
        private String url;
        @SerializedName("is_silhouette")
        private boolean isSilhouette;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public boolean getIsSilhouette() {
            return isSilhouette;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class Picture {
        @SerializedName("data")
        private Data data;

        public Data getData() {
            return data;
        }
    }
}
