package com.example.omri.placesretrofit20;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by omri on 26/12/2017.
 */

public class MyDataManager {
    public static String key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";
    public static String nextPageToken = null;

    public static double Mylat,Mylng;

    //שמירה של השם פייסבוק והתמונה של המגירה
    public static ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem();
    public static String facebookPhotoUrl;
    public static String facebookUserName;

    public static String googleUserName;
    public static Uri googlePhotoUrl;

    public static String milesOrKm ;
    public static String languageStr ;
    //החלפה בין עברית לאנגלית בחיפוש

    public static ArrayList<String> suggestionArryList = new ArrayList<>();
    public static ArrayList<SearchResults> data = new ArrayList<>();
    public static ArrayList<String> titels = new ArrayList<>();
    public static ArrayList<String> placeIds = new ArrayList<>();
    public static ArrayList<LatLng> latLngs = new ArrayList<>();
    public static ArrayList<MarkerInfo> markerInfo= new ArrayList<>();
    public static ArrayList<ReviewsData> reviewsData = new ArrayList<>();
    public static ArrayList<FavoritePlace> favorite = new ArrayList<>();
    public static ArrayList<String> photoReferences = new ArrayList<>();
    public static ArrayList<String> openingHours = new ArrayList<>();
    public static ArrayList<BestAroundResult> bestAroundResults = new ArrayList<>();
    public static ArrayList<PlaceHistory> placeHistories = new ArrayList<>();

    public static String noPhotoStr;


    public static Resources resources;

    public static void saveGoogleDetails (Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MyDataManager.googlePhotoUrl);
        editor.putString("googlePhotoUrl",json);
        editor.putString("googleUserName",googleUserName);
        editor.apply();
        editor.commit();
    }

    public static void getGoogleDetails (Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String googlePhotoUrl = sharedPrefs.getString("googlePhotoUrl",null);
        Uri recent = Uri.parse(googlePhotoUrl);
        String googleUserName = sharedPrefs.getString("googleUserName",null);
        MyDataManager.googlePhotoUrl =recent;
        MyDataManager.googleUserName = googleUserName;
    }

    public static void saveRecentSearchs(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MyDataManager.suggestionArryList);
        editor.putString("suggestions",json);
        editor.apply();
        editor.commit();
    }

    public static ArrayList<String> readRecentSearchs(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("suggestions", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> recent = gson.fromJson(json, type);
        return recent;
    }

    public static void saveLanguage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String lang = MyDataManager.languageStr;
        editor.putString("lang",lang);
        editor.apply();
        editor.commit();
    }

    public static String readLanguage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPrefs.getString("lang", null);
        return lang;
    }

    public static void changeLanguage(Activity activity, String language) {
        Locale locale = new Locale(language);
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = locale;
        res.updateConfiguration(conf, dm);
    }
    //שמירה של מידת מרחק
    public static void saveUnitOfMeasure(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("distance_units", milesOrKm);
        editor.apply();
        editor.commit();
    }

    public static String getUnitsOfMeasur (Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String milesOrKm = sharedPrefs.getString("distance_units",null);
        return milesOrKm;
    }
    //שמירה של פרטי פייסבוק
    public static void saveFacebookDetails (Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("facebookPhotoUrl",facebookPhotoUrl);
        editor.putString("facebookUserName",facebookUserName);
        editor.apply();
        editor.commit();
    }

    //שמירה של מועדפים לשרד פרפרנס
    public static void saveFavorites(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MyDataManager.favorite);
       // String queryHistory = gson.toJson(MyDataManager.querysHistory);
        editor.putString("fav", json);
        editor.apply();
        editor.commit();
    }

    //קריאה של מועדפים משרד פרפרנס
    public static ArrayList<FavoritePlace> ReadFavorites(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("fav", null);
        Type type = new TypeToken<ArrayList<FavoritePlace>>() {}.getType();
        ArrayList<FavoritePlace> favorites = gson.fromJson(json, type);
        return favorites;
    }
    //
    public static void savePlaces(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String PlaceHistory = gson.toJson(MyDataManager.placeHistories);
        editor.putString("placesHistory",PlaceHistory);
        editor.apply();
        editor.commit();
    }

    public static ArrayList<PlaceHistory> readPlaces(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("placesHistory", null);
        Type type = new TypeToken<ArrayList<PlaceHistory>>() {}.getType();
        ArrayList<PlaceHistory> places = gson.fromJson(json, type);
        return places;
    }

    //בדיקה אם קיים חיבור לאינטרנט
    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public static String distance(double lat1, double lng1, double lat2, double lng2){
        LatLng latLngA = new LatLng(lat1,lng1);
        LatLng latLngB = new LatLng(lat2,lng2);
        Location locationA = new Location("point A");
        locationA.setLatitude(latLngA.latitude);
        locationA.setLongitude(latLngA.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(latLngB.latitude);
        locationB.setLongitude(latLngB.longitude);
        if(MyDataManager.milesOrKm == "mills") {
            double distance1 = locationA.distanceTo(locationB);
            double distanceInMills = distance1 * 0.000621371;
            return String.format("%.2f",distanceInMills);
        }else {
            double distance1 = locationA.distanceTo(locationB);
            double distanceInKm = distance1 / 1000;
            return String.format("%.2f", distanceInKm);
        }
    }
}
