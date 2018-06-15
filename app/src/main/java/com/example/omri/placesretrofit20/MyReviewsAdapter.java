package com.example.omri.placesretrofit20;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by omri on 27/12/2017.
 */

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.ViewHolder> {

    View view;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item_card, null);


        return new MyReviewsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(reviewsData.get(position).getName());
        holder.rating.setNumStars(Integer.parseInt(reviewsData.get(position).getRating()));
        holder.review.setText(reviewsData.get(position).getReview());
        holder.time.setText(reviewsData.get(position).getTime());
        Picasso.with(view.getContext()).load(reviewsData.get(position).getProfilePhotoUrl()).into(holder.reviewImage);
    }
    @Override
    public int getItemCount() {
        return reviewsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,review , time;
        ImageView reviewImage;
        RatingBar rating;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.review_list_name);
            rating = view.findViewById(R.id.review_list_rating);
            review = view.findViewById(R.id.review_list_text);
            reviewImage = view.findViewById(R.id.review_list_image);
            time = view.findViewById(R.id.relative_time);
        }


    }

    ArrayList<ReviewsData> reviewsData;
    public MyReviewsAdapter(ArrayList<ReviewsData> data){
        this.reviewsData = data;
    }


}