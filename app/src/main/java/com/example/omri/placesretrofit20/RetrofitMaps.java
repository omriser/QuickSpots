package com.example.omri.placesretrofit20;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by omri on 04/12/2017.
 */

public interface RetrofitMaps {
    String BASE_URL = "https://maps.googleapis.com";


    @GET("/maps/api/place/textsearch/json")
    Call<JsonObject> getCityResults(@Query("query") String query, @Query("location") String location, @Query("key") String key,@Query("language") String language,@Query("rankby") String rankby);

    @GET("/maps/api/place/details/json")
    Call<JsonObject> getDetailsResults(@Query("placeid") String placeid, @Query("key") String key,@Query("language") String language);

    @GET("/maps/api/place/textsearch/json")
    Call<JsonObject> getMoreResults(@Query("pagetoken") String pagetoken  ,@Query("key") String key,@Query("language") String language);

    @GET("/maps/api/place/nearbysearch/json")
    Call<JsonObject> getAroundMeResults(@Query("location") String location, @Query("radius") Integer radius, @Query("types") String types, @Query("key") String key, @Query("language") String language);

    @GET("/maps/api/place/nearbysearch/json")
    Call<JsonObject> getNearByResults(@Query("query") String query,@Query("location") String location, @Query("radius") Integer radius,@Query("key") String key, @Query("language") String language);


}
