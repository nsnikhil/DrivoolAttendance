package com.nrs.nsnik.drivoolattendance.fragments.dialogFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nrs.nsnik.drivoolattendance.interfaces.GetLocationInterface;


public class GetLocation extends android.support.v4.app.DialogFragment implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private static final String LOG_TAG = GetLocation.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private double mLat, mLng;
    private Location mLastLocation;
    GetLocationInterface getLocationInterface;
    private static final int LOCATION_PERMISSION = 56;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationInterface = (GetLocationInterface) getTargetFragment();
        initialize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    private void initialize() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }


    @Override
    public void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    private void setLocationValue(Location location) {
        getLocationInterface.getLocation(location);
        mLat = location.getLatitude();
        mLng = location.getLongitude();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            setLocationValue(mLastLocation);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void startLocationUpdates() {
        long UPDATE_INTERVAL = 10 * 1000;
        long FASTEST_INTERVAL = 2000;
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationValue(location);
    }

}
