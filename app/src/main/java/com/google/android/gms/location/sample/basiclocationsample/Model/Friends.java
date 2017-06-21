package com.google.android.gms.location.sample.basiclocationsample.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HIMANSHU on 4/18/2017.
 * Model Class for storing Friends objects.
 * It contains all the setter and getter required for this object.
 */


public class Friends {

    @Expose
    @SerializedName("username")
    private String username;
    @Expose
    @SerializedName("latitude")
    private String latitude;
    @Expose
    @SerializedName("longitude")
    private String longitude;

public Friends(String username, String latitude,String longitude)
{
    this.username=username;
    this.latitude=latitude;
    this.longitude=longitude;
}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
