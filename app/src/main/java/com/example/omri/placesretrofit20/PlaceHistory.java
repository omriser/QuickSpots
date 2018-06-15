package com.example.omri.placesretrofit20;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by omri on 07/02/2018.
 */

public class PlaceHistory implements Parcelable {
    private String nameHistoryPlace;
    private String photoPathHistoryPlace;
    private String placeId;
    private String address;
    private String distance;
    private double lat;
    private double lng;

    public PlaceHistory(String nameHistoryPlace, String photoPathHistoryPlace, String placeId, String address, String distance, double lat, double lng) {
        this.nameHistoryPlace = nameHistoryPlace;
        this.photoPathHistoryPlace = photoPathHistoryPlace;
        this.placeId = placeId;
        this.address = address;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
    }


    protected PlaceHistory(Parcel in) {
        nameHistoryPlace = in.readString();
        photoPathHistoryPlace = in.readString();
        placeId = in.readString();
        address = in.readString();
        distance  = in.readString();
        lat  = in.readDouble();
        lng  = in.readDouble();
    }

    public static final Creator<PlaceHistory> CREATOR = new Creator<PlaceHistory>() {
        @Override
        public PlaceHistory createFromParcel(Parcel in) {
            return new PlaceHistory(in);
        }

        @Override
        public PlaceHistory[] newArray(int size) {
            return new PlaceHistory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameHistoryPlace);
        dest.writeString(photoPathHistoryPlace);
        dest.writeString(placeId);
        dest.writeString(address);
        dest.writeString(distance);
        dest.writeDouble(lat);
        dest.writeDouble(lng);

    }

    public String getNameHistoryPlace() {
        return nameHistoryPlace;
    }



    public String getPhotoPathHistoryPlace() {
        return photoPathHistoryPlace;
    }



    public String getPlaceId() {
        return placeId;
    }



    public String getAddress() {
        return address;
    }


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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
}
