package ke.co.thinksynergy.movers.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Anthony Ngure on 11/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

@Entity(nameInDb = "users")
public class User implements Parcelable {


    private long id;
    private String name;
    private String email;
    private String phone;
    private String facebookId;
    private String facebookPictureUrl;
    private String accountType;
    private double latitude;
    private double longitude;
    private double distance;
    private String createdAt;
    private String updatedAt;
    private String token;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getFacebookPictureUrl() {
        return facebookPictureUrl;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getToken() {
        return token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
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
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.facebookId);
        dest.writeString(this.facebookPictureUrl);
        dest.writeString(this.accountType);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.distance);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeString(this.token);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setFacebookPictureUrl(String facebookPictureUrl) {
        this.facebookPictureUrl = facebookPictureUrl;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setToken(String token) {
        this.token = token;
    }

    protected User(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.facebookId = in.readString();
        this.facebookPictureUrl = in.readString();
        this.accountType = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distance = in.readDouble();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.token = in.readString();
    }

    @Generated(hash = 2002803623)
    public User(long id, String name, String email, String phone, String facebookId,
            String facebookPictureUrl, String accountType, double latitude,
            double longitude, double distance, String createdAt, String updatedAt,
            String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.facebookId = facebookId;
        this.facebookPictureUrl = facebookPictureUrl;
        this.accountType = accountType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.token = token;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
