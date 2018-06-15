package com.example.omri.placesretrofit20;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by omri on 09/12/2017.
 */

public class SearchResults implements Parcelable {

    private String name;
    private String address;
    private String distance;
    private String placeid;
    private String photoUrl;
    double lat;
    double lng;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlaceid() {
        return placeid;
    }
    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }



    public SearchResults(String name, String address, String distance, double lat, double lng,String placeid,String photoUrl) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.placeid = placeid;
        this.photoUrl = photoUrl;
    }


    protected SearchResults(Parcel in) {
        name = in.readString();
        address = in.readString();
        distance = in.readString();
        placeid = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        photoUrl = in.readString();
    }

    public static final Creator<SearchResults> CREATOR = new Creator<SearchResults>() {
        @Override
        public SearchResults createFromParcel(Parcel in) {
            return new SearchResults(in);
        }

        @Override
        public SearchResults[] newArray(int size) {
            return new SearchResults[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(distance);
        dest.writeString(placeid);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(photoUrl);
    }
}
