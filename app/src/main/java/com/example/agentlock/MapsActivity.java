package com.example.agentlock;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Button b1;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker CurrentUserLocationMarker;
    private static final int request_user_location_Code = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_maps);

        b1 = (Button)findViewById (R.id.button);

        b1.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                startActivity (new Intent (MapsActivity.this,Jobs.class));
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission ();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById (R.id.map);
        mapFragment.getMapAsync (this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {

            buildgoogleApiClient ();
            mMap.setMyLocationEnabled (true);
        }


    }

    public  Boolean checkUserLocationPermission()
    {
        if(ContextCompat.checkSelfPermission (this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale (this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_user_location_Code);
            }
            else
            {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_user_location_Code);
            }
            return false;

        }
        else
        {
            return  true;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode)
        {
            case request_user_location_Code :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(googleApiClient == null)
                        {
                            buildgoogleApiClient();
                        }
                        mMap.setMyLocationEnabled (true);
                    }
                }
                else
                {
                    Toast.makeText (this, "Permission Denied", Toast.LENGTH_SHORT).show ();
                }
                return;
        }

    }

    protected  synchronized void buildgoogleApiClient(){

        googleApiClient = new GoogleApiClient.Builder (this)
                .addConnectionCallbacks (this)
                .addOnConnectionFailedListener (this)
                .addApi (LocationServices.API)
                .build ();

        googleApiClient.connect ();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest =new LocationRequest ();
        locationRequest.setInterval (1100);
        locationRequest.setFastestInterval (1100);
        locationRequest.setPriority (LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission (this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {

            LocationServices.FusedLocationApi.requestLocationUpdates (googleApiClient,locationRequest,this);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if(CurrentUserLocationMarker != null){
            CurrentUserLocationMarker.remove ();
        }

        LatLng latLng = new LatLng (location.getLatitude (), location.getLongitude ());

        MarkerOptions markerOptions = new MarkerOptions ();
        markerOptions.position (latLng);
        markerOptions.title ("You are Here");
        markerOptions.icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_GREEN));
        CurrentUserLocationMarker = mMap.addMarker (markerOptions);

        mMap.moveCamera (CameraUpdateFactory.newLatLng (latLng));
        mMap.animateCamera (CameraUpdateFactory.zoomBy (14));

        if(googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates (googleApiClient,this);
        }


        String userId = FirebaseAuth.getInstance ().getCurrentUser ().getUid ();
        DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ("Driver location");

        GeoFire geoFire = new GeoFire (ref);
        geoFire.setLocation (userId , new GeoLocation (location.getLatitude () , location.getLongitude ()));


    }

    /*@Override
    protected void onStop() {
        super.onStop ();

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Driver location");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);


    }*/
}
