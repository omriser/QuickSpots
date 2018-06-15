package com.example.omri.placesretrofit20;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResultActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, ResultCallback<LocationSettingsResult>, GoogleApiClient.OnConnectionFailedListener {

    String queryUser;
    String loc;
    int radius;
    String key;
    SearchView searchBar;
    double lat;
    double lng;
    LocationListener locationListener;
    LocationManager locationManager;
    public static RecyclerView recyclerView;
    public static FloatingActionButton mapBtn;
    Retrofit retrofit;
    MyAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    int  TAG_CODE_PERMISSION_LOCATION = 3000;
    RetrofitMaps myApi;
    public static String nextPageToken;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;
    TabLayout searchTabLayout;
    ViewPager searchViewPager;
    String language;
    String distance;

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        distance = "distance";
        language = MyDataManager.readLanguage(this);
        if(language == null){
            language = "iw";
        }
        MyDataManager.changeLanguage(this,language);

        MyDrawerManager.addDrawer(this);

        mapBtn = findViewById(R.id.floatingMap);
        //recyclerView = findViewById(R.id.recycleView);
        linearLayoutManager = new LinearLayoutManager(this);

        searchTabLayout = findViewById(R.id.tabLayout);
        searchViewPager = findViewById(R.id.search_view_pager);

        key = "AIzaSyC9cHqBkI3JwnRWcG1y1N7AaRe_38oT2wQ";
        radius = 100;
        searchBar = findViewById(R.id.search_view);
        queryUser = getIntent().getExtras().getString("main_screen_query","");
        searchBar.setQuery(queryUser,true);


        searchViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        notifyDataSetChanged();
                        return SearchResultFragment.newInstance("", "");
                    case 1:
                        notifyDataSetChanged();
                        return  RecantSearchFragment.newInstance("","");
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
                        return getString(R.string.result_tab);
                    case 1:
                        return getString(R.string.search_history_tab);
                }
                return null;
            }
        });
        searchTabLayout.setupWithViewPager(searchViewPager);



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

        Boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Location location;
        if(networkEnabled){
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                locationManager.requestLocationUpdates(getProviderName(), 0, 0, locationListener);
            }
            if (location == null){
                location = getLastKnownLocation();
            }
            if(location != null){
                loc = location.getLatitude() + ", "+ location.getLongitude();
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);

        if(queryUser != null){
            startRetrofit();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MAPintent = new Intent(getBaseContext(),MapsActivity.class);
                MAPintent.putParcelableArrayListExtra("markers",MyDataManager.markerInfo);
                startActivity(MAPintent);
            }
        });


        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                MyDataManager.data.clear();
                queryUser = s;
                if(!MyDataManager.isConnected(SearchResultActivity.this)){
                    buildConnactionDialog(SearchResultActivity.this).show();
                }else {
                    startRetrofit();
                }

                if(MyDataManager.suggestionArryList == null){
                    MyDataManager.suggestionArryList = new ArrayList<>();
                }
                if (MyDataManager.suggestionArryList.size() > 30) {
                    MyDataManager.suggestionArryList.remove(30);
                }
                for (int i = 0; i < MyDataManager.suggestionArryList.size(); i++) {
                    if (MyDataManager.suggestionArryList.get(i).contains(s)) {
                        MyDataManager.suggestionArryList.remove(i);
                    }
                }
                MyDataManager.suggestionArryList.add(0, new String(s));

                SearchResultFragment.adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


    }



    public void startRetrofit(){
        if(loc != null) {
            myApi = retrofit.create(RetrofitMaps.class);
                myApi.getCityResults(queryUser, loc, key,language,distance).enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject searchResults = response.body();
                        Boolean available;
                        int size = searchResults.get("results").getAsJsonArray().size();
                        if (searchResults.has("next_page_token")) {
                            SearchResultActivity.nextPageToken = searchResults.get("next_page_token").getAsString();
                            EventBus.getDefault().post(new MessageEvent(searchResults.get("next_page_token").getAsString()));
                        } else {
                            SearchResultActivity.nextPageToken = null;
                        }
                        MyDataManager.data.clear();
                        MyDataManager.markerInfo.clear();
                        MyDataManager.latLngs.clear();
                        MyDataManager.titels.clear();
                        MyDataManager.placeIds.clear();
                        for (int i = 0; i < size; i++) {
                            String str = null;
                            if (searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().has("photos")) {
                                str = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("photos").getAsJsonArray().get(0).getAsJsonObject().get("photo_reference").getAsString();
                            } else {
                                str = null;
                            }
                            String address = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("formatted_address").getAsString();
                            int y = address.indexOf(',', 1 + address.indexOf(",", address.indexOf(',')));
                            String firstPart = address.substring(0, y+1);
                            String secondPart = address.substring(y+1);
                            double lat2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                            double lng2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                            Log.d("MyLog", "Distance is: " + distance(lat, lng, lat2, lng2));
                            MyDataManager.data.add(i, new SearchResults(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString(),
                                   firstPart + "\n" + secondPart ,
                                    distance(lat, lng, lat2, lng2),
                                    lat2, lng2, searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("place_id").getAsString(), str
                            ));
                            MyDataManager.markerInfo.add(i, new MarkerInfo(new LatLng(MyDataManager.data.get(i).lat, MyDataManager.data.get(i).lng), MyDataManager.data.get(i).getName(), MyDataManager.data.get(i).getPlaceid(), i));
                            MyDataManager.latLngs.add(i, new LatLng(MyDataManager.data.get(i).lat, MyDataManager.data.get(i).lng));
                            MyDataManager.titels.add(i, MyDataManager.data.get(i).getName());
                            MyDataManager.placeIds.add(i, MyDataManager.data.get(i).getPlaceid());
                        }
                        SearchResultFragment.adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

        }
    }

    public void gpsInit(){
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException e){
            Log.d("Mylog", "securityException : " + e);
        }
    }

    public String distance(double lat1, double lng1, double lat2, double lng2){
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

            }
        });

        return builder;
    }

    public class NextPageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(loc != null) {
                myApi = retrofit.create(RetrofitMaps.class);
                if(nextPageToken != null ){
                    myApi.getMoreResults(nextPageToken,key,MyDataManager.languageStr).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject searchResults = response.body();
                            if(searchResults.has("next_page_token")) {
                                if(searchResults.get("next_page_token").getAsString() != SearchResultActivity.nextPageToken) {
                                    SearchResultActivity.nextPageToken = searchResults.get("next_page_token").getAsString();
                                }else {
                                    SearchResultActivity.nextPageToken = null;
                                }
                            }else {
                                SearchResultActivity.nextPageToken = null;
                            }
                            if (searchResults != null) {
                                if (searchResults.has("results") ) {
                                    int size = searchResults.get("results").getAsJsonArray().size();
                                    int startIndex = MyDataManager.data.size()-1;
                                    for (int i = 0; i <  size; i++) {
                                        String str = null;
                                        if(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().has("photos")){
                                            str  = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("photos").getAsJsonArray().get(0).getAsJsonObject().get("photo_reference").getAsString();
                                        }else{
                                            str = null;
                                        }
                                        double lat2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                                        double lng2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                                        Log.d("MyLog", "Distance is: " + distance(lat, lng, lat2, lng2));
                                        MyDataManager.data.add(new SearchResults(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString(),
                                                searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("formatted_address").getAsString(),
                                                distance(lat, lng, lat2, lng2),
                                                lat2, lng2, searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("place_id").getAsString(),str
                                        ));
                                        MyDataManager.markerInfo.add(new MarkerInfo(new LatLng(MyDataManager.data.get(i+startIndex).lat, MyDataManager.data.get(i+startIndex).lng), MyDataManager.data.get(i+startIndex).getName(), MyDataManager.data.get(i+startIndex).getPlaceid(), i+startIndex));
                                        MyDataManager.latLngs.add(new LatLng(MyDataManager.data.get(i+startIndex).lat, MyDataManager.data.get(i+startIndex).lng));
                                        MyDataManager.titels.add(MyDataManager.data.get(i+startIndex).getName());
                                        MyDataManager.placeIds.add(MyDataManager.data.get(i+startIndex).getPlaceid());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });

                }

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }

        }



            if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchBar.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        MyDataManager.saveRecentSearchs(this);
        MyDataManager.saveFacebookDetails(this);
        MyDataManager.saveFavorites(this);
        MyDataManager.saveLanguage(this);
        MyDataManager.saveUnitOfMeasure(this);
        MyDataManager.savePlaces(this);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
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

    public static class MessageEvent {
        public final String message;

        public MessageEvent (String message){
            this.message=message;
        }
    }

    String getProviderName() {
        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
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

}