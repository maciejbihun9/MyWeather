package com.example.maciejbihun.myweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maciejbihun.myweather.model.ApplicationConstants;
import com.example.maciejbihun.myweather.model.CurrentWeather;
import com.example.maciejbihun.myweather.model.DayWeather;
import com.example.maciejbihun.myweather.model.Icon;
import com.example.maciejbihun.myweather.model.UserLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by MaciekBihun on 2016-03-23.
 */

/**
 * Provides the entry point to Google Play services.
 */

public class DayFragment extends Fragment {

    private static final String TAG = "DayFragment";
    private static final String PAGER_POSITION = "pager_position";

    //widgets
    private TextView temperature;
    private TextView summary;
    private TextView time;
    private ImageView icon;
    private ImageView cityImage;
    private TextView pressure;
    private TextView city;

    private View fragmentView;
    private String lastKnownWeather;
    private boolean firstLaunch = false;


    //returns new DayFragment with position in ViewPager
    public static DayFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putSerializable(PAGER_POSITION, position);

        DayFragment fragment = new DayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check weather data from forecast is already stored
        if(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(ApplicationConstants.WEATHER_RESULT, null) == null){
            Log.i(TAG, "there is no data yet");
            firstLaunch = true;
        } else {
            //if weather is already stored, then get weather string from SharedPreferences.
            lastKnownWeather = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .getString(ApplicationConstants.WEATHER_RESULT, null);
        }
    }

    //Get photo from SharedPreferences
    public void setImage(){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String previouslyEncodedImage = shre.getString(MainActivity.PHOTO_STRING, "");

        //If there is already store some photo then update a layout.
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);

            cityImage.setImageBitmap(bitmap);
            Log.d(TAG, "Działa");
        } else {
            cityImage.setImageDrawable(getResources().getDrawable(R.drawable.clear_day));
        }
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.main_fragment_layout, container, false);

        //get int position from ViewPager
        int pagerPosition = (int)getArguments().getSerializable(PAGER_POSITION);

        //must be first
        initializeWidgets(fragmentView);
        try {
            updateLayoutWeather(lastKnownWeather, pagerPosition);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setImage();
        return fragmentView;
    }

    //initialize all widgets
    private void initializeWidgets(View mView){
        //set values to widgets
        temperature = (TextView) mView.findViewById(R.id.temperature);
        summary = (TextView) mView.findViewById(R.id.summary);
        icon = (ImageView) mView.findViewById(R.id.icon);
        pressure = (TextView) mView.findViewById(R.id.pressure);
        time = (TextView) mView.findViewById(R.id.time);
        city = (TextView) mView.findViewById(R.id.city);
        cityImage = (ImageView) mView.findViewById(R.id.city_photo);

    }

    //update all fragments with data depends on viewPager position
    private void updateLayoutWeather(String weatherConditions, int position) throws JSONException, IOException {

        if(firstLaunch){
            temperature.setText(R.string.zero);
            time.setText(R.string.no_time);
            summary.setText(R.string.no_summary_yet);
            pressure.setText(R.string.no_pressure_yet);
            return;
        }

        //fragment with currentWeather
        ForecastFetchr weather = new ForecastFetchr();
        JSONObject jsonObject = new JSONObject(weatherConditions);
        //for todays weather

        if(position == 0){
            CurrentWeather currentWeather = weather.parseCurrentWeather(jsonObject, getContext());
            time.setText(currentWeather.getTime() + "");
            temperature.setText(currentWeather.getTemperature() + "" + (char) 0x00B0);
            summary.setText(currentWeather.getSummary());

            //set weather icon
            int imageIcon = Icon.getIconId(currentWeather.getIcon());
            icon.setImageResource(imageIcon);

        } else {
            //for later days
            List <DayWeather> weatherList = weather.parseDailyWeather(jsonObject);
            int positionInList = position - 1;

            time.setText(weatherList.get(positionInList).getTime()+"");
            summary.setText(weatherList.get(positionInList).getSummary());
            pressure.setText(weatherList.get(positionInList).getPressure()+" hPa");
            temperature.setText(weatherList.get(positionInList).getTempMax()+""+(char) 0x00B0);

            Log.i("konwertowana data: ", getFormattedTime(weatherList.get(positionInList).getTime())+"");

            //set weather icon
            int imageIcon = Icon.getIconId(weatherList.get(positionInList).getIcon());
            icon.setImageResource(imageIcon);


        }
    }

    public String getFormattedTime(long time) {
        String timezone = Time.getCurrentTimezone();
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        Date dateTime = new Date(time * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

}
