package com.example.maciejbihun.myweather;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.maciejbihun.myweather.backgroundServices.DownloadWeatherService;
import com.example.maciejbihun.myweather.model.UserLocation;

/**
 * Created by MaciekBihun on 2016-03-28.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);

        //initialize toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //download weather from Forecast and store it in SharedPreferences.
        DownloadWeatherService.setServiceAlarm(this, true);

        //set current user location on separed thread.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                new UserLocation(MainActivity.this).setUserLocation();
            }
        });

        //initialize ViewPager
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.getCurrentItem();
        mViewPager.setOffscreenPageLimit(3);
    }

    //adapt fragments scroling
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DayFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            //return mCrimes.size();
            return 5;
        }
    }

}
