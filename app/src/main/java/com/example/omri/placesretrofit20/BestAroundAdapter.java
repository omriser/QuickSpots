package com.example.omri.placesretrofit20;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by omri on 06/02/2018.
 */
public class BestAroundAdapter extends RecyclerView.Adapter<BestAroundAdapter.ViewHolder> {

    String  key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";
    String name,address,placeid,num,photoPath;
    int rating;
    double lat , lng;
    OnBottomReachedListener onBottomReachedListener;


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name,type,address;
        RatingBar ratingBar;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.best_around_name);
            type = view.findViewById(R.id.best_around_type);
            address = view.findViewById(R.id.best_around_address);
            ratingBar = view.findViewById(R.id.best_around_rating);
        }


    }

    ArrayList<BestAroundResult> bestAroundResults;
    public BestAroundAdapter(ArrayList<BestAroundResult> bestAroundResults){
        this.bestAroundResults = bestAroundResults;
    }
    View view;
    @Override
    public BestAroundAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.around_me_item, null);
        return new BestAroundAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.name.setText(bestAroundResults.get(position).getName());
        holder.type.setText(bestAroundResults.get(position).getType());
        holder.address.setText(bestAroundResults.get(position).getAddress());
        holder.ratingBar.setNumStars(bestAroundResults.get(position).getRating());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFavoriteRetrofit(holder,position);
                Intent intent = new Intent(view.getContext(),PlaceActivity.class);
                intent.putExtra("best_around_place_index",position);
                view.getContext().startActivity(intent);
            }
        });

        if (position == bestAroundResults.size() - 1){

            onBottomReachedListener.onBottomReached(position);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFavoriteRetrofit(holder,position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return bestAroundResults.size();
    }

    public void startFavoriteRetrofit(BestAroundAdapter.ViewHolder holder, final int position) {
        RetrofitMaps myApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);
        myApi.getDetailsResults(bestAroundResults.get(position).getPlaceId(), key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject searchResults = response.body();
                try {
                    name = searchResults.get("result").getAsJsonObject().get("name").getAsString();
                    address = searchResults.get("result").getAsJsonObject().get("formatted_address").getAsString();
                    placeid = searchResults.get("result").getAsJsonObject().get("place_id").getAsString();
                    if (searchResults.get("result").getAsJsonObject().has("formatted_phone_number")) {
                        num = searchResults.get("result").getAsJsonObject().get("formatted_phone_number").getAsString();
                    } else {
                        num = "No number";
                    }
                    if (searchResults.get("result").getAsJsonObject().has("rating")) {
                        rating = searchResults.get("result").getAsJsonObject().get("rating").getAsInt();
                    } else {
                        rating = 0;
                    }
                    Log.d("LOG", num);
                    Log.d("LOG", rating + " !");
                    Intent intent = new Intent(view.getContext(), PlaceActivity.class);
                    intent.putExtra("place_phone", num);
                    intent.putExtra("place_rating", rating);
                    intent.putExtra("best_around_place_index", position);
                    view.getContext().startActivity(intent);
                } catch (Exception e) {

                }
                notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });

    }

    public void startRetrofit(MyAdapter.ViewHolder holder, int position){
     //   photoPath= saveImage(holder.imageView,position);
        RetrofitMaps myApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);
        myApi.getDetailsResults(bestAroundResults.get(position).getPlaceId(), key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject searchResults = response.body();
                int index = 0;
                try {
                    lat = searchResults.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                    lng = searchResults.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                    name = searchResults.get("result").getAsJsonObject().get("name").getAsString();
                    address = searchResults.get("result").getAsJsonObject().get("formatted_address").getAsString();
                    placeid = searchResults.get("result").getAsJsonObject().get("place_id").getAsString();
                    if(searchResults.get("result").getAsJsonObject().has("formatted_phone_number")){
                        num = searchResults.get("result").getAsJsonObject().get("formatted_phone_number").getAsString();
                    }else{
                        num = MyDataManager.resources.getString(R.string.no_phone_num);
                    }
                    if(searchResults.get("result").getAsJsonObject().has("rating")) {
                        rating = searchResults.get("result").getAsJsonObject().get("rating").getAsInt();
                    }else{
                        rating = 0;
                    }
                    int size = MyDataManager.favorite.size();
                    for (int i = 0; i < size; i++) {
                        if (MyDataManager.favorite.get(i).getPlaceid().toString().contains(placeid.toString())) {
                            Toast.makeText(view.getContext(), name + MyDataManager.resources.getString(R.string.in_favorite), Toast.LENGTH_SHORT).show();
                            throw new Exception("Exist favorite");
                        }}
                    MyDataManager.favorite.add(index++, new FavoritePlace(name,address,placeid,num,rating,photoPath,lat,lng,address));
                    size++;
                    notifyDataSetChanged();
                    Intent intent = new Intent(view.getContext(), FavoritActivity.class);
                    view.getContext().startActivity(intent);
                }catch (Exception e){
                    Log.d("FavoriteLog","להוסיף פונקציה על המספר");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }


}
