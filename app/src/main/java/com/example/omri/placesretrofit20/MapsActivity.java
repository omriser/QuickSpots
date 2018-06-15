package com.example.omri.placesretrofit20;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;
    int TAG_CODE_PERMISSION_LOCATION =1000;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    String TAG;
    int index;

    MarkerInfo singelMarkerInfo;
    ArrayList<MarkerInfo> markerInfos = MyDataManager.markerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MyDrawerManager.addDrawer(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        singelMarkerInfo = (MarkerInfo) getIntent().getExtras().get("marker");
        markerInfos = getIntent().getExtras().getParcelableArrayList("markers");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("MyLog" , location.getLatitude()+" , "+location.getLongitude());
                LatLng current = new LatLng(location.getLatitude() , location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(current).title(getString(R.string.currant_titel)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)).zIndex(3500));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(current)
                        .zoom(15)
                        .bearing(90)
                        .tilt(30)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            initGps();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        initGps();
    }

    public void initGps (){
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException e){
            Log.d("MyLog", e.getMessage());
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(singelMarkerInfo == null) {
            for (int i = 0; i < MyDataManager.markerInfo.size(); i++) {
                LatLng latLng = (MyDataManager.markerInfo.get(i).getLatLng());
                Log.d("AddMarkerEvent", "Marker added on " + MyDataManager.latLngs.get(i).toString());
                mMap.addMarker(new MarkerOptions().position(latLng).title(MyDataManager.titels.get(i)).zIndex(i));
            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    for (int i = 0; i < MyDataManager.markerInfo.size(); i++) {
                        MarkerInfo markerInfo = MyDataManager.markerInfo.get(i);
                        if (markerInfo.getIndex() == marker.getZIndex()) {
                            index = markerInfo.getIndex();
                        }
                    }

                    if (marker.getZIndex() != 3500) {
                        Intent intent = new Intent(getBaseContext(), PlaceActivity.class);
                        intent.putExtra("place_index", index);
                        startActivity(intent);
                    }
                }
            });
        }else {
            LatLng latLng = (singelMarkerInfo.getLatLng());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(singelMarkerInfo.getTitel());
            mMap.addMarker(markerOptions);
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker.getZIndex() != 3500) {
                        Intent intent = new Intent(getBaseContext(), PlaceActivity.class);
                        index = singelMarkerInfo.getIndex();
                        intent.putExtra("place_index", index);
                        startActivity(intent);
                    }
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        mMap.clear();
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
}
