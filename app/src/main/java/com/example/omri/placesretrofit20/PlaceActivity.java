package com.example.omri.placesretrofit20;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PlaceActivity extends AppCompatActivity {
    TextView phoneNum;
    public RecyclerView placeRecyclerView;
    MyReviewsAdapter reviewsAdapter;
    ImageButton callBtn;
    Button wazeBtn;
    public static int REQUEST_PHONE_CALL = 420;
    FloatingActionButton mapBtn;
    final String key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";
    Button openingHoursSpinner;
    TextView openNow;
    ViewPager photoPager;
   // PhotoViewPagerAdapter photoViewPagerAdapter;
    RetrofitMaps myApi;
    Retrofit retrofit;
    String recantPlacePhotoUrl;
    ConstraintLayout constraintLayout;
    String photoPath,address,num,name;
    int rating;
    double lat,lng;
    ViewPager  viewPager;
    PhotoViewPagerAdapter  photoViewPagerAdapter;
    NoPhotoViewPagerAdapter noPhotoViewPagerAdapter;
    String placeid;
    ImageView addToFavorite;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_place_layout);
       // MyDataManager.languageStr = MyDataManager.readLanguage(this);
        MyDataManager.readLanguage(this);
        MyDrawerManager.addDrawer(this);
        TextView name = findViewById(R.id.name);
        TextView addres = findViewById(R.id.addres);
        TextView distance = findViewById(R.id.distance);
        phoneNum = findViewById(R.id.phon_number);
        callBtn = findViewById(R.id.call_btn);
        wazeBtn = findViewById(R.id.waze_btn);
        mapBtn = findViewById(R.id.place_map_btn);
        openingHoursSpinner = findViewById(R.id.spinner);
        openNow = findViewById(R.id.open_now_text);
        constraintLayout = findViewById(R.id.main_activity);
        addToFavorite = findViewById(R.id.add_to_fav);


        // Find the toolbar view inside the activity layout
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setDisplayShowTitleEnabled(false);

      //  ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PlaceActivity.this,android.R.layout.simple_list_item_1,MyDataManager.openingHours);
       // spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // openingHoursSpinner.setAdapter(spinnerAdapter);

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(PlaceActivity.this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PlaceActivity.this, android.R.layout.select_dialog_singlechoice,MyDataManager.openingHours);

        openingHoursSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(PlaceActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.show();
                    }
                });
                builderSingle.show();
            }
        });



        placeRecyclerView = findViewById(R.id.place_recycler);
        placeRecyclerView.setHasFixedSize(true);
        placeRecyclerView.setLayoutManager(new LinearLayoutManager(PlaceActivity.this));
        PlaceActivity.this.reviewsAdapter = (new MyReviewsAdapter(MyDataManager.reviewsData));
        placeRecyclerView.setAdapter(reviewsAdapter);



        final int index = getIntent().getIntExtra("place_index",-1);
        final int index2 =getIntent().getIntExtra("place_index_favorite",-1);
        final int index3 = getIntent().getIntExtra("best_around_place_index",-1);
        final int index4 = getIntent().getIntExtra("HistoryPlace",0);
        if(index > -1) {
            final SearchResults searchResults = MyDataManager.data.get(index);
            name.setText(searchResults.getName());
            addres.setText(searchResults.getAddress());
            distance.setText(MyDataManager.resources.getString(R.string.distance)+ " "+ searchResults.getDistance() +" "+ MyDataManager.milesOrKm);
            placeid = searchResults.getPlaceid();
            startRetrofitPlace(searchResults.getPlaceid());
        }else if (index2 > -1) {
            final FavoritePlace favoritePlace = MyDataManager.favorite.get(index2);
            name.setText(favoritePlace.getName());
            addres.setText(favoritePlace.getAddress());
            distance.setText(MyDataManager.resources.getString(R.string.distance)+ " " + favoritePlace.getDistance());
            placeid = favoritePlace.getPlaceid();
            startRetrofitPlace(favoritePlace.getPlaceid());
        }else if (index3 > -1) {
            final BestAroundResult bestAroundResult = MyDataManager.bestAroundResults.get(index3);
            name.setText(bestAroundResult.getName());
            addres.setText(bestAroundResult.getAddress());
            distance.setText(MyDataManager.resources.getString(R.string.distance)+ " " + bestAroundResult.getDistance());
            placeid = bestAroundResult.getPlaceId();
            startRetrofitPlace(bestAroundResult.getPlaceId());
        }else {
            final PlaceHistory placeHistory = MyDataManager.placeHistories.get(index4);
            name.setText(placeHistory.getNameHistoryPlace());
            addres.setText(placeHistory.getAddress());
            distance.setText(MyDataManager.resources.getString(R.string.distance)+ " " + placeHistory.getDistance());
            placeid = placeHistory.getPlaceId();
            startRetrofitPlace(placeHistory.getPlaceId());
        }


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceActivity.this,MapsActivity.class);
                if(index > -1) {
                    MarkerInfo markerInfo = MyDataManager.markerInfo.get(index);
                    intent.putExtra("marker", markerInfo);
                    startActivity(intent);
                }else if (index2 > -1) {
                    MarkerInfo markerInfo = MyDataManager.markerInfo.get(index2);
                    intent.putExtra("marker", markerInfo);
                    startActivity(intent);
                }else if (index3 > -1)  {
                    MarkerInfo markerInfo = MyDataManager.markerInfo.get(index3);
                    intent.putExtra("marker",markerInfo);
                }else {
                    MarkerInfo markerInfo = MyDataManager.markerInfo.get(index4);
                    intent.putExtra("marker",markerInfo);
                }
            }
        });


        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNum.getText().toString()));
                if (ContextCompat.checkSelfPermission(PlaceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PlaceActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    if (phoneNum.getText().toString() == "No number"){
                        Toast.makeText(PlaceActivity.this,MyDataManager.resources.getString(R.string.no_phone_num),Toast.LENGTH_SHORT).show();
                    }else {
                        startActivity(callIntent);
                    }
                }
            }
        });

        wazeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index>-1) {
                    SearchResults searchResults = MyDataManager.data.get(index);
                    String uri = "waze://?ll="+searchResults.lat+","+ searchResults.lng +"&navigate=yes";
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }else if (index2 >-1){
                    FavoritePlace favoritePlace = MyDataManager.favorite.get(index2);
                    String uri = "waze://?ll="+favoritePlace.getLat()+","+ favoritePlace.getLng() +"&navigate=yes";
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }else if (index3 > -1) {
                    BestAroundResult bestAroundResult = MyDataManager.bestAroundResults.get(index3);
                    String uri = "waze://?ll="+bestAroundResult.getLat()+","+ bestAroundResult.getLng() +"&navigate=yes";
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }else {
                    PlaceHistory placeHistory = MyDataManager.placeHistories.get(index4);
                    String uri = "waze://?ll="+placeHistory.getLat()+","+ placeHistory.getLng() +"&navigate=yes";
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }

            }
        });


        placeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mapBtn.hide();

            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState ==  RecyclerView.SCROLL_STATE_IDLE) {
                    mapBtn.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        addToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRetrofit(photoViewPagerAdapter.imageView,placeid);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                if (MyDrawerManager.drawer.isDrawerOpen()){
                    MyDrawerManager.drawer.closeDrawer();
                }
                MyDrawerManager.drawer.openDrawer();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void startRetrofitPlace (final String placeId){
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);


        viewPager = findViewById(R.id.view_pager_photo);
        photoViewPagerAdapter = new PhotoViewPagerAdapter (this,MyDataManager.photoReferences);
        noPhotoViewPagerAdapter = new NoPhotoViewPagerAdapter(this);


            myApi.getDetailsResults(placeId, key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    String str;
                    JsonObject searchResults = response.body();
                    Boolean available;
                    int weekSize;
                    String houres;
                    String historyName;
                    String place_id;
                    String address;
                    double lat,lng;
                    MyDataManager.openingHours.clear();
                    MyDataManager.reviewsData.clear();
                    MyDataManager.photoReferences.clear();
                    try {
                        historyName = searchResults.get("result").getAsJsonObject().get("name").getAsString();
                        address =searchResults.get("result").getAsJsonObject().get("formatted_address").getAsString();
                        lat =searchResults.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                        lng = searchResults.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                        String num = searchResults.get("result").getAsJsonObject().get("formatted_phone_number").getAsString();
                        int size = searchResults.get("result").getAsJsonObject().get("reviews").getAsJsonArray().size();
                        if (searchResults.get("result").getAsJsonObject().has("photos")) {
                            int photoSize = searchResults.get("result").getAsJsonObject().get("photos").getAsJsonArray().size();
                            recantPlacePhotoUrl = searchResults.get("result").getAsJsonObject().get("photos").getAsJsonArray().get(0).getAsJsonObject().get("photo_reference").getAsString();
                            if (MyDataManager.placeHistories.size()>30){
                                MyDataManager.placeHistories.remove(30);
                            }
                            for(int i = 0;i<MyDataManager.placeHistories.size(); i++) {
                                if (MyDataManager.placeHistories.get(i).getPlaceId().contains(placeId)) {
                                    MyDataManager.placeHistories.remove(i);
                                }
                            }
                            MyDataManager.placeHistories.add(0,new PlaceHistory(historyName,recantPlacePhotoUrl,placeId,address,MyDataManager.distance(MyDataManager.Mylat,MyDataManager.Mylng,lat,lng),lat,lng));
                            for (int i = 0; i < photoSize; i++) {
                                MyDataManager.photoReferences.add(searchResults.get("result").getAsJsonObject().get("photos").getAsJsonArray().get(i).getAsJsonObject().get("photo_reference").getAsString());

                            }
                            str = searchResults.get("result").getAsJsonObject().get("photos").getAsJsonArray().get(0).getAsJsonObject().get("photo_reference").getAsString();
                        } else {
                            viewPager.setAdapter(noPhotoViewPagerAdapter);
                            str = null;
                        }
                        if (str != null) {
                            viewPager.setAdapter(photoViewPagerAdapter);
                        }
                        if (searchResults.get("result").getAsJsonObject().has("opening_hours")) {
                            available = searchResults.get("result").getAsJsonObject().get("opening_hours").getAsJsonObject().get("open_now").getAsBoolean();
                            weekSize = searchResults.get("result").getAsJsonObject().get("opening_hours").getAsJsonObject().get("weekday_text").getAsJsonArray().size();
                            houres = searchResults.get("result").getAsJsonObject().get("opening_hours").getAsJsonObject().get("weekday_text").getAsJsonArray().get(6).toString();
                            MyDataManager.openingHours.add(new String(houres));
                            for (int i = 0; i < weekSize - 1; i++) {
                                houres = searchResults.get("result").getAsJsonObject().get("opening_hours").getAsJsonObject().get("weekday_text").getAsJsonArray().get(i).toString();
                                MyDataManager.openingHours.add(new String(houres));
                            }


                        } else {
                            available = false;
                        }

                        if (size >= 0) {
                            for (int i = 0; i < size; i++) {
                                MyDataManager.reviewsData.add(i, new ReviewsData(
                                        searchResults.get("result").getAsJsonObject().get("reviews").getAsJsonArray().get(i).getAsJsonObject().get("author_name").getAsString(),
                                        searchResults.get("result").getAsJsonObject().get("reviews").getAsJsonArray().get(i).getAsJsonObject().get("profile_photo_url").getAsString(),
                                        searchResults.get("result").getAsJsonObject().get("reviews").getAsJsonArray().get(i).getAsJsonObject().get("rating").getAsString(),
                                        searchResults.get("result").getAsJsonObject().get("reviews").getAsJsonArray().get(i).getAsJsonObject().get("text").getAsString(),
                                        searchResults.get("result").getAsJsonObject().get("reviews").getAsJsonArray().get(i).getAsJsonObject().get("relative_time_description").getAsString()));
                            }
                        }
                        reviewsAdapter.notifyDataSetChanged();

                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new MyTimerTaskPlace(viewPager), 3000, 4000);

                        changeVibrateState(available);

                        phoneNum.setText(num);
                    } catch (Exception e) {
                        Log.d("phoneNumLog", "Error " + e);
                        phoneNum.setText(MyDataManager.resources.getString(R.string.no_phone_num));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

    }


    public void changeVibrateState( boolean doOpen){
        if (doOpen == true){
            openNow.setText(MyDataManager.resources.getString(R.string.open_now));
        }else{
            openNow.setText(MyDataManager.resources.getString(R.string.close_now));
        }
    }

    public class MyTimerTaskPlace extends TimerTask {
        ViewPager viewPager;
        int size = MyDataManager.photoReferences.size();
        int index=0;
        public MyTimerTaskPlace(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        public void run() {

            PlaceActivity.this.runOnUiThread(new Runnable() {

                public void run() {
                    if (index<size){
                        if (viewPager.getCurrentItem() ==index){
                            viewPager.setCurrentItem(index+1);
                        }
                        index++;
                    }else{
                        index=0;
                        viewPager.setCurrentItem(index);

                    }
                }

            });
        }
    }

    @Override
    protected void onStop() {
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onPause();
    }

    private String saveImage(ImageView imageView, String placeid)  {
        //get bitmap from ImageVIew
        //not always valid, depends on your drawable
        Bitmap bitmap = null;
        if(((BitmapDrawable)imageView.getDrawable()).getBitmap() != null) {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
        //always save as
        String fileName = placeid + ".jpg";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File storageDir = getBaseContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    public void startRetrofit(ImageView imageView,  String placeid){
        photoPath= saveImage(imageView,placeid);
        RetrofitMaps myApi;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);
        myApi.getDetailsResults(placeid, key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject searchResults = response.body();
                int index = 0;
                try {
                    lat = searchResults.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                    lng = searchResults.get("result").getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                    name = searchResults.get("result").getAsJsonObject().get("name").getAsString();
                    address = searchResults.get("result").getAsJsonObject().get("formatted_address").getAsString();
                    final String placeid2 = searchResults.get("result").getAsJsonObject().get("place_id").getAsString();
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
                    if(MyDataManager.favorite == null){
                        MyDataManager.favorite = new ArrayList<>();
                    }
                    int size = MyDataManager.favorite.size();
                    for (int i = 0; i < size; i++) {
                        if (MyDataManager.favorite.get(i).getPlaceid().contains(placeid2)) {
                            Toast.makeText(getBaseContext(), name + " "+MyDataManager.resources.getString(R.string.in_favorite), Toast.LENGTH_SHORT).show();
                            throw new Exception("Exist favorite");
                        }
                    }
                    MyDataManager.favorite.add(index++, new FavoritePlace(name,address,placeid2,num,rating,photoPath,lat,lng,MyDataManager.distance(MyDataManager.Mylat,MyDataManager.Mylng,lat,lng)));
                     Toast.makeText(getBaseContext(), name + " "+MyDataManager.resources.getString(R.string.added_to_fav), Toast.LENGTH_SHORT).show();
                    size++;
                    MyDataManager.saveFavorites(getBaseContext());
                    notify();
                    Intent intent = new Intent(getBaseContext(), FavoritActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    Log.d("FavoriteLog","להוסיף פונקציה על המספר");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }
}
