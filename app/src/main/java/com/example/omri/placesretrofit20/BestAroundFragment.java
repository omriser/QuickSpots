package com.example.omri.placesretrofit20;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BestAroundFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BestAroundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BestAroundFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BestAroundFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BestAroundFragment.
     */
    public static BestAroundFragment newInstance(String param1, String param2) {
        BestAroundFragment fragment = new BestAroundFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View view;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Context context;
    public  static BestAroundAdapter adapter;
    LocationListener locationListener;
    LocationManager locationManager;
    RetrofitMaps myApi;
    int  TAG_CODE_PERMISSION_LOCATION = 3000;
    Retrofit retrofit;
    String nextPageToken;
    String key;
    String loc;
    double lat, lng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_best_around, container, false);
        BestAroundFragment.adapter = (new BestAroundAdapter(MyDataManager.bestAroundResults));

        recyclerView = view.findViewById(R.id.best_around_recycler);
        context = view.getContext();
        linearLayoutManager = new LinearLayoutManager(context);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
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

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMaps.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(RetrofitMaps.class);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        BestAroundFragment.adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                //your code goes here
                if(nextPageToken != null) {
                    Snackbar snackbar = Snackbar.make(view.findViewById(R.id.best_around_coordinator), MyDataManager.resources.getString(R.string.more_results), Snackbar.LENGTH_SHORT);
                    snackbar.setAction(MyDataManager.resources.getString(R.string.load_more), new NextPageListener());
                    snackbar.show();
                }
            }
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

            }
        }

        adapter.notifyDataSetChanged();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
                                if(searchResults.get("next_page_token").getAsString() != BestAroundFragment.this.nextPageToken) {
                                    BestAroundFragment.this.nextPageToken = searchResults.get("next_page_token").getAsString();
                                }else {
                                    BestAroundFragment.this.nextPageToken = null;
                                }
                            }else {
                                BestAroundFragment.this.nextPageToken = null;
                            }
                            if (searchResults != null) {
                                if (searchResults.has("results") ) {
                                    int size = searchResults.get("results").getAsJsonArray().size();
                                    int startIndex = MyDataManager.bestAroundResults.size()-1;
                                    int rating = 0;
                                    for (int i = 0; i < size; i++) {
                                        if(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().has("rating")){
                                            rating = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("rating").getAsInt();
                                        }
                                        double lat2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                                        double lng2 = searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();
                                        MyDataManager.bestAroundResults.add(new BestAroundResult(searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString(),
                                                searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString(),
                                                rating,searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("place_id").getAsString(),lat2,lng2,
                                                MyDataManager.distance(lat,lng,lat2,lng2),searchResults.get("results").getAsJsonArray().get(i).getAsJsonObject().get("vicinity").getAsString()));
                                        MyDataManager.markerInfo.add( new MarkerInfo(new LatLng(MyDataManager.bestAroundResults.get(i+startIndex).getLat(), MyDataManager.bestAroundResults.get(i+startIndex).getLng()), MyDataManager.bestAroundResults.get(i+startIndex).getName(), MyDataManager.bestAroundResults.get(i+startIndex).getPlaceId(), i+startIndex));
                                        MyDataManager.latLngs.add( new LatLng(MyDataManager.bestAroundResults.get(i+startIndex).getLat(), MyDataManager.bestAroundResults.get(i+startIndex).getLng()));
                                        MyDataManager.titels.add( MyDataManager.bestAroundResults.get(i+startIndex).getName());
                                        MyDataManager.placeIds.add( MyDataManager.bestAroundResults.get(i+startIndex).getPlaceId());
                                    }
                                }
                                BestAroundFragment.adapter.notifyDataSetChanged();
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

    public void checkPermission () {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            gpsInit();
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
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
        locationManager = (LocationManager) context
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainActivity.NextPageEvent event) {
        nextPageToken = event.message;
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}
