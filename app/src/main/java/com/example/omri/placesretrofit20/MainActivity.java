package com.example.omri.placesretrofit20;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, ResultCallback<LocationSettingsResult>, GoogleApiClient.OnConnectionFailedListener {

    ImageView mainImageView;
    SearchView mainScreenSearch;
    TabLayout mainScreenTabLayout;
    ViewPager mainScreenViewPager;
    LocationListener locationListener;
    LocationManager locationManager;
    String query, loc ;
    double lat, lng;
    int  TAG_CODE_PERMISSION_LOCATION = 3000;
    Retrofit retrofit;
    RetrofitMaps myApi;
    String key;
    int radius;
    static String nextPageToken;
    String types;
    int REQUEST_CHECK_SETTINGS = 100;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    CheckBox aroundMeBtn;
    String language , distanceUnits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MyDataManager.resources = getResources();
        MyDataManager.suggestionArryList = MyDataManager.readRecentSearchs(this);
        MyDataManager.favorite = MyDataManager.ReadFavorites(this);
        language = MyDataManager.readLanguage(this);
        MyDataManager.saveLanguage(this);
        if(language == null){
            language = "iw";
        }
        distanceUnits = MyDataManager.getUnitsOfMeasur(this);
        if(distanceUnits == null){
            distanceUnits = "km";
        }
        MyDataManager.changeLanguage(this,language);
        MyDataManager.milesOrKm =MyDataManager.getUnitsOfMeasur(this);



        key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";
        types = "food|restaurant";
        radius = 2500;
        mainImageView = findViewById(R.id.main_screen_photo);
        mainScreenTabLayout = findViewById(R.id.main_screen_tablayout);
        mainScreenSearch = findViewById(R.id.main_screen_searchView);
        mainScreenViewPager = findViewById(R.id.main_screen_view_pager);

        aroundMeBtn = findViewById(R.id.around_my_btn);
        aroundMeBtn.setChecked(true);
        MyDrawerManager.addDrawer(this);

        mainScreenSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchResultActivity.class);
                startActivity(intent);
                return false;
            }
        });

        aroundMeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    mainScreenSearch.setQueryHint(getString(R.string.search_hint_around_me));
                }else {
                    mainScreenSearch.setQueryHint(getString(R.string.serarc_hint_all_over));
                }
            }
        });



        //הצבה של תמונות במסך ראשי
        Picasso.with(this).load(switchPhoto()).fit().centerCrop().into(mainImageView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("MyLog", "location: "+ location.getLatitude() + ", "+ location.getLongitude() );
                loc = location.getLatitude() + "," + location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        checkPermission();


        mainScreenSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainScreenSearch.setIconified(false);            }
        });

        Boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location;
        if (networkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                locationManager.requestLocationUpdates(getProviderName(), 0, 0, locationListener);
            }
            if (location == null){
                location = getLastKnownLocation();
            }
            if (location != null) {
                loc = location.getLatitude() + ", " + location.getLongitude();
                lat = location.getLatitude();
                lng = location.getLongitude();
                MyDataManager.Mylat = lat;
                MyDataManager.Mylng = lng;

            }
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        if(loc != null){
            startBestAroundRetrofit();
        }



        mainScreenViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        notifyDataSetChanged();
                        return BestAroundFragment.newInstance("","");
                    case 1:
                        notifyDataSetChanged();
                        return RecantPlacesFragment.newInstance("","");

                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case 0:
                        return getString(R.string.Around_Me_Tab);
                    case 1:
                        return getString(R.string.History_Tab);

                }
                return null;
            }
        });
        mainScreenTabLayout.setupWithViewPager(mainScreenViewPager);

        mainScreenSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!MyDataManager.isConnected(MainActivity.this)){
                    buildConnactionDialog(MainActivity.this).show();
                }else {

                }
                query = s;
                if(MyDataManager.suggestionArryList != null) {
                    if (MyDataManager.suggestionArryList.size() > 30) {
                        MyDataManager.suggestionArryList.remove(30);
                    }
                    for (int i = 0; i < MyDataManager.suggestionArryList.size(); i++) {
                        if (MyDataManager.suggestionArryList.get(i).contains(s)) {
                            MyDataManager.suggestionArryList.remove(i);
                        }
                    }
                    MyDataManager.suggestionArryList.add(0, new String(s));
                }


                Intent intent = new Intent(MainActivity.this,SearchResultActivity.class);
                intent.putExtra("main_screen_query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
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

    public static int switchPhoto (){
        int[] photos = new int[]{R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,R.drawable.a6,R.drawable.a7,R.drawable.a9,R.drawable.a10};
        final int random = new Random().nextInt(8);
        switch (random){
            case 0:
                return photos [0];
            case 1:
                return photos [1];
            case 2:
                return photos [2];
            case 3:
                return photos [3];
            case 4:
                return photos [4];
            case 5:
                return photos [5];
            case 6:
                return photos [6];
            case 7:
                return photos [7];
        }
        return 0;
    }

    public void checkPermission () {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            gpsInit();
        }else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }
    }

    public void gpsInit(){
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException e){
            Log.d("Mylog", "securityException : " + e);
        }
    }

    String getProviderName() {
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        return locationManager.getBestProvider(criteria, true);
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            checkPermission();
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d("LocationLog","last known location, provider: %s, location: %s" + provider + location);
            if (location == null) {
                continue;
            }
            if (bestLocation == null
                    || location.getAccuracy() < bestLocation.getAccuracy()) {
                Log.d("LocationLog","found best last known location: %s" + location);
                bestLocation = location;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    public void startBestAroundRetrofit(){
        if(loc != null) {
            myApi = retrofit.create(RetrofitMaps.class);

            myApi.getAroundMeResults(loc,radius,types,key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject searchResults = response.body();
                    int size = searchResults.get("results").getAsJsonArray().size();
                    if (searchResults.has("next_page_token")) {
                        MainActivity.nextPageToken = searchResults.get("next_page_token").getAsString();
                        EventBus.getDefault().post(new NextPageEvent(searchResults.get("next_page_token").getAsString()));
                    } else {
                        MainActivity.nextPageToken = null;
                    }
                    int rating = 0;
                    String type = null;
                    MyDataManager.markerInfo.clear();
                    MyDataManager.latLngs.clear();
                    MyDataManager.titels.clear();
                    MyDataManager.placeIds.clear();
                    for (int i = 0; i < size; i++) {
                        if(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().has("rating")){
                            rating = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("rating").getAsInt();
                        }
                        if(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString().toString().contains("_")){
                            String tamp = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString();
                            type = tamp.replace("_"," ");
                        }else {
                            type = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString();
                        }
                        double lat2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                        double lng2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                        MyDataManager.bestAroundResults.add(i,new BestAroundResult(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString(),
                                type,
                                rating,searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("place_id").getAsString(),lat2,lng2,
                                MyDataManager.distance(lat,lng,lat2,lng2),searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("vicinity").getAsString()));
                        MyDataManager.markerInfo.add(i, new MarkerInfo(new LatLng(MyDataManager.bestAroundResults.get(i).getLat(), MyDataManager.bestAroundResults.get(i).getLng()), MyDataManager.bestAroundResults.get(i).getName(), MyDataManager.bestAroundResults.get(i).getPlaceId(), i));
                        MyDataManager.latLngs.add(i, new LatLng(MyDataManager.bestAroundResults.get(i).getLat(), MyDataManager.bestAroundResults.get(i).getLng()));
                        MyDataManager.titels.add(i, MyDataManager.bestAroundResults.get(i).getName());
                        MyDataManager.placeIds.add(i, MyDataManager.bestAroundResults.get(i).getPlaceId());
                    }
                    BestAroundFragment.adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });

        }
    }

    public static class NextPageEvent {
        public final String message;

        public NextPageEvent(String message){
            this.message=message;
        }
    }

    @Override
    protected void onPause() {
        MyDataManager.saveRecentSearchs(this);
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        MyDataManager.saveRecentSearchs(this);
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        MyDataManager.saveRecentSearchs(this);
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  GPS turned off, Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {

                    //failed to show dialog
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public AlertDialog.Builder buildConnactionDialog(final Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(Resources.getSystem().getString(R.string.no_internet));
        builder.setMessage(Resources.getSystem().getString(R.string.connaction_dialog_msg));

        builder.setPositiveButton(Resources.getSystem().getString(R.string.try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDataManager.isConnected(c);
            }
        });
        builder.setNegativeButton(Resources.getSystem().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        return builder;
    }

    @Override
    protected void onResume() {
        MyDataManager.ReadFavorites(this);
        super.onResume();
    }

    @Override
    protected void onStart() {
        MyDataManager.ReadFavorites(this);
        super.onStart();
    }
}
