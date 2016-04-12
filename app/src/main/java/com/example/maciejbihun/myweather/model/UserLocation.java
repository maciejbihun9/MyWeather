package com.example.maciejbihun.myweather.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by MaciekBihun on 2016-03-25.
 */
public class UserLocation {

    private static final float LOCATION_REFRESH_DISTANCE = 100000;
    private static final long LOCATION_REFRESH_TIME = 100000;
    private static final String TAG = "UserLocation";
    private Context mContext;

    public UserLocation(Context mContext){
        this.mContext = mContext;
    }
    //runs GPS, gets user location and store it in SharedPreferences.
    public void setUserLocation(){
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);
    }


    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //store current user location in SharedPreferences
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putFloat(ApplicationConstants.LATITUDE, (float)location.getLatitude())
                    .putFloat(ApplicationConstants.LONGITUDE, (float) location.getLongitude())
                    .commit();
            Log.i("Storing...:",location.getLatitude() + " " + location.getLongitude() );
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
