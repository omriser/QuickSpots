package com.example.omri.placesretrofit20;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by omri on 24/12/2017.
 */

public class MarkerInfo implements Parcelable {
    private LatLng latLng;
    private String titel;
    private String placeId;
    private int index;

    public MarkerInfo(LatLng latLng, String titel, String placeId,int index) {
        this.latLng = latLng;
        this.titel = titel;
        this.placeId = placeId;
        this.index = index;
    }


    protected MarkerInfo(Parcel in) {
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        titel = in.readString();
        placeId = in.readString();
        index = in.readInt();
    }

    public static final Creator<MarkerInfo> CREATOR = new Creator<MarkerInfo>() {
        @Override
        public MarkerInfo createFromParcel(Parcel in) {
            return new MarkerInfo(in);
        }

        @Override
        public MarkerInfo[] newArray(int size) {
            return new MarkerInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latLng, flags);
        dest.writeString(titel);
        dest.writeString(placeId);
        dest.writeInt(index);
    }

    public LatLng getLatLng() {
        return latLng;
    }



    public String getTitel() {
        return titel;
    }


    public String getPlaceId() {
        return placeId;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
