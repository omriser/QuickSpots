package com.example.omri.placesretrofit20;

/**
 * Created by omri on 27/12/2017.
 */

public class ReviewsData {
    private String name;
    private String profilePhotoUrl;
    private String rating;
    private String review;
    private String time;

    public ReviewsData(String name, String profilePhotoUrl, String rating, String review,String time) {
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.rating = rating;
        this.review = review;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
