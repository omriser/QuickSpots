package com.example.omri.placesretrofit20;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;

/**
 * Created by omri on 09/12/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    String photoPath,address,placeid,num,name,sharePhotoPath;
    String  key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";
    int rating;
    double lat,lng;
    File imgFile;
    String distance;

    OnBottomReachedListener onBottomReachedListener;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,address,distance;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.list_name);
            address = view.findViewById(R.id.list_addraes);
            distance = view.findViewById(R.id.list_distance);
            imageView = view.findViewById(R.id.list_image);
        }


    }
    ArrayList<SearchResults> searchResults;
    public MyAdapter(ArrayList<SearchResults> data){
        this.searchResults = data;
    }

    View view;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(searchResults.get(position).getName());
        holder.address.setText(searchResults.get(position).getAddress());
        holder.distance.setText(MyDataManager.resources.getString(R.string.distance)+": "+ searchResults.get(position).getDistance() + " "+MyDataManager.milesOrKm);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFavoriteRetrofit(holder,position);
                Intent intent = new Intent(view.getContext(),PlaceActivity.class);
                intent.putExtra("place_index",position);
                view.getContext().startActivity(intent);
            }
        });

        if (position == searchResults.size() - 1){

            onBottomReachedListener.onBottomReached(position);

        }
        if (searchResults.get(position).getPhotoUrl()!= null) {
            Picasso.with(view.getContext()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" + "&photoreference="
                    + searchResults.get(position).getPhotoUrl() + "&key=" + key).resize(350,350).centerCrop().into(holder.imageView);
        }else {
            Picasso.with(view.getContext()).load(R.drawable.noimageavailable).resize(350,350).centerCrop().into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFavoriteRetrofit(holder,position);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final CharSequence[] items = {MyDataManager.resources.getString(R.string.favorite),MyDataManager.resources.getString(R.string.share)};

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder.setTitle(MyDataManager.resources.getString(R.string.select_the_action));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item){
                            case 0:
                                startRetrofit(holder,position);
                                break;
                            case 1:
                                sharePhotoPath = saveImage(holder.imageView,position);
                                imgFile = new File(sharePhotoPath);
                                Intent shareIntent6 = new Intent(android.content.Intent.ACTION_SEND);
                                shareIntent6.setData(Uri.parse("sms:"));
                                String shareBody6 =holder.name.getText().toString()+ System.getProperty("line.separator") +holder.address.getText().toString()
                                        +System.getProperty("line.separator")+"https://www.google.com/maps/search/?api=1&query=Google&query_place_id="+searchResults.get(position).getPlaceid().toString();
                                String shareSub6 =holder.name.getText().toString()+ System.getProperty("line.separator");
                                shareIntent6.putExtra("sms_body", shareBody6 );
                                shareIntent6.putExtra(EXTRA_TEXT, shareBody6 );
                                shareIntent6.putExtra(EXTRA_SUBJECT, shareSub6 );
                                if(imgFile.exists()) {
                                    final Uri myUri = Uri.fromFile(imgFile);
                                    shareIntent6.putExtra(Intent.EXTRA_STREAM, myUri);
                                }
                                shareIntent6.setType("vnd.android-dir/mms-sms");
                                shareIntent6.setType("com.instagram.android");
                                shareIntent6.setType("com.whatsapp");
                                shareIntent6.setType("android.gm");
                                shareIntent6.setType("com.facebook.katana");
                                shareIntent6.setType("text/plain");
                                try {
                                    view.getContext().startActivity(Intent.createChooser(shareIntent6, "Share via"));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(view.getContext(),"Error",Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    private String saveImage(ImageView imageView, int position)  {
        //get bitmap from ImageVIew
        //not always valid, depends on your drawable
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        //always save as
        String fileName = MyDataManager.data.get(position).getPlaceid() + ".jpg";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File storageDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(storageDir + File.separator + fileName);
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes.toByteArray());
            Log.d("MyLog", "path: " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file.getAbsolutePath();
    }
    public void startRetrofit(ViewHolder holder, int position){
        photoPath= saveImage(holder.imageView,position);
        RetrofitMaps myApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);
        myApi.getDetailsResults(searchResults.get(position).getPlaceid(), key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
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
                            Toast.makeText(view.getContext(), name + " "+MyDataManager.resources.getString(R.string.in_favorite), Toast.LENGTH_SHORT).show();
                            throw new Exception("Exist favorite");
                        }
                    }
                    MyDataManager.favorite.add(index++, new FavoritePlace(name,address,placeid,num,rating,photoPath,lat,lng,MyDataManager.distance(MyDataManager.Mylat,MyDataManager.Mylng,lat,lng)));
                    size++;
                    MyDataManager.saveFavorites(view.getContext());
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

    public void startFavoriteRetrofit(ViewHolder holder, final int position) {
        photoPath = saveImage(holder.imageView,position);
        RetrofitMaps myApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);
        myApi.getDetailsResults(searchResults.get(position).getPlaceid(), key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
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
                        num = MyDataManager.resources.getString(R.string.no_phone_num);
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
                    intent.putExtra("place_photo", photoPath);
                    intent.putExtra("place_index", position);
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

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){

        this.onBottomReachedListener = onBottomReachedListener;
    }


}