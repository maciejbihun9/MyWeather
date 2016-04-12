package com.example.maciejbihun.myweather.backgroundServices;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.maciejbihun.myweather.ForecastFetchr;
import com.example.maciejbihun.myweather.model.ApplicationConstants;

/**
 * Created by MaciekBihun on 2016-03-24.
 */
public class DownloadWeatherService extends IntentService {

    private static final int POLL_INTERVAL = 10000 * 60; // 60 seconds
    private static final String TAG = "DownloadWeatherService";

    //returns new intent which is going to start this service
    public static Intent newIntent(Context context) {
        return new Intent(context, DownloadWeatherService.class);
    }

    public DownloadWeatherService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //if network is not available then exit this method.
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        //get last known user location from SharedPreferences
        double latitude =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getFloat(ApplicationConstants.LATITUDE, 16);
        double longitude =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getFloat(ApplicationConstants.LONGITUDE, 50);

        //download weather with last known location
        ForecastFetchr currentWeather = new ForecastFetchr(latitude, longitude);
        String resultWeather = currentWeather.getWeatherConditions();

        //save downloaded weather to SharedPreferences.
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(ApplicationConstants.WEATHER_RESULT, resultWeather)
                .commit();
    }

    //checks if network is available.
    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }

    //runs service in about 60 seconds
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = DownloadWeatherService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            //if alarmmanager is on then send intents to pending intent.
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

}
/*
IntentService wrzuca wszytkie zdarzenia do kolejki. Są one wykonywane po kolei.
 */