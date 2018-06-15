package com.example.omri.placesretrofit20;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omri on 06/02/2018.
 */

public class BestAroundResult implements Parcelable{

    private String name;
    private String type;
    private int rating;
    private String placeId;
    private double lat;
    private double lng;
    private String distance;
    private String address;


    public BestAroundResult(String name, String type, int rating,String placeId,double lat ,double lng,String distance,String address) {
        this.name = name;
        this.type = type;
        this.rating = rating;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
        this.address = address;
    }

    protected BestAroundResult(Parcel in) {
        name = in.readString();
        type = in.readString();
        rating = in.readInt();
        placeId = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        distance = in.readString();
        address = in.readString();
    }

    public static final Creator<BestAroundResult> CREATOR = new Creator<BestAroundResult>() {
        @Override
        public BestAroundResult createFromParcel(Parcel in) {
            return new BestAroundResult(in);
        }

        @Override
        public BestAroundResult[] newArray(int size) {
            return new BestAroundResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(rating);
        dest.writeString(placeId);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(distance);
        dest.writeString(distance);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public int getRating() {
        return rating;
    }

    public String getPlaceId() {
        return placeId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
