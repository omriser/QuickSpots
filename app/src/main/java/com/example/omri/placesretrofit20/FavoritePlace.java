package com.example.omri.placesretrofit20;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by USER on 04-Jan-18.
 */

public class FavoritePlace implements Parcelable {
    private String name;
    private   String address;
    private String placeid;
    private   String number;
    private int rating;
    private String photoPath;
    private double lat;
    private double lng;
    private String distance;

    public FavoritePlace(String name, String address,String placeid,String number,int rating,String photoPath,double lat ,double lng,String distance) {
        this.name = name;
        this.address = address;
        this.placeid = placeid;
        this.number=number;
        this.rating=rating;
        this.photoPath=photoPath;
        this.lat =lat;
        this.lng =lng;
        this.distance = distance;
    }


    protected FavoritePlace(Parcel in) {
        name = in.readString();
        address = in.readString();
        placeid = in.readString();
        number = in.readString();
        rating = in.readInt();
        photoPath=in.readString();
        lat=in.readDouble();
        lng=in.readDouble();
        distance = in.readString();
    }

    public static final Creator<FavoritePlace> CREATOR = new Creator<FavoritePlace>() {
        @Override
        public FavoritePlace createFromParcel(Parcel in) {
            return new FavoritePlace(in);
        }

        @Override
        public FavoritePlace[] newArray(int size) {
            return new FavoritePlace[size];
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
        dest.writeString(placeid);
        dest.writeString(number);
        dest.writeInt(rating);
        dest.writeString(photoPath);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(distance);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getNumber() {
        return number;
    }

    public int getRating() {
        return rating;
    }

    public String getPhotoPath() {
        return photoPath;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}

