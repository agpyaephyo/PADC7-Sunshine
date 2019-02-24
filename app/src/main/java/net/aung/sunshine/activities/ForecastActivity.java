package net.aung.sunshine.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.aung.sunshine.R;
import net.aung.sunshine.controllers.ForecastListScreenController;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.fragments.ForecastDetailFragment;
import net.aung.sunshine.fragments.ForecastListFragment;
import net.aung.sunshine.utils.SunshineConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForecastActivity extends BaseActivity
        implements ForecastListScreenController {

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Nullable
    @Bind(R.id.appbar)
    AppBarLayout mAppBar;

    public static Intent createNewIntent(Context context, WeatherStatusVO weatherStatus, int position) {
        Intent intentToForecastDetail = new Intent(context, ForecastActivity.class);
        intentToForecastDetail.putExtra(IE_WEATHER_STATUS_DATE_TIME, weatherStatus.getDateTime());
        intentToForecastDetail.putExtra(IE_WEATHER_STATUS_POSITION, position);
        return intentToForecastDetail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        ButterKnife.bind(this, this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                /* Calling the service with PendingIntent.
                PendingIntent intentToAlarmManager = DataSyncAlarmReceiver.newPendingIntent(getApplicationContext());
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, intentToAlarmManager);
                */

            }
        });

        if (savedInstanceState == null) {
            int weatherStatusPosition = getIntent().getIntExtra(IE_WEATHER_STATUS_POSITION, RecyclerView.NO_POSITION);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, ForecastListFragment.newInstance(weatherStatusPosition), ForecastListFragment.TAG)
                    .commit();
        }

        if (getResources().getBoolean(R.bool.isTwoPane)) {
            //two panes tablets.

            long weatherStatusDateTime = getIntent().getLongExtra(IE_WEATHER_STATUS_DATE_TIME, SunshineConstants.TODAY);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_detail_container,
                            ForecastDetailFragment.newInstance(weatherStatusDateTime), ForecastDetailFragment.TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            /*
            case R.id.action_settings:
                startSettingActivity();
                break;
            case R.id.action_about:
                Snackbar.make(mFab, "About this Project Sunshine is coming soon", Snackbar.LENGTH_SHORT)
                         .setAction("Action", null).show();
                break;
            case R.id.action_help:
                Snackbar.make(mFab, "The help that you gonna need to use this App is coming soon", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
            */
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNavigateToForecastDetail(ImageView ivWeatherIcon, WeatherStatusVO weatherStatus) {
        if (!getResources().getBoolean(R.bool.isTwoPane)) {
            /*
            getSupportFragmentManager().beginTransaction()
                    //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setCustomAnimations(R.anim.screen_enter_horizontal, R.anim.screen_exit_horizontal, R.anim.screen_pop_enter_horizontal, R.anim.screen_pop_exit_horizontal)
                    .replace(R.id.fl_container, ForecastDetailFragment.newInstance(weatherStatus.getDateTime()))
                    .addToBackStack(null)
                    .commit();
                    */

            Intent intentToDetail = ForecastDetailActivity.createNewIntent(this, weatherStatus);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        new Pair(ivWeatherIcon, getString(R.string.detail_icon_transition_name)));
                ActivityCompat.startActivity(this, intentToDetail, activityOptions.toBundle());
            } else {
                startActivity(intentToDetail);
                overridePendingTransition(R.anim.screen_enter_horizontal, R.anim.screen_exit_horizontal);
            }

        } else {
            ForecastDetailFragment detailFragment = (ForecastDetailFragment) getSupportFragmentManager().findFragmentByTag(ForecastDetailFragment.TAG);
            detailFragment.updateForecastDetail(weatherStatus);
        }
    }

    @Override
    public void showCityInGoogleMap(String city) {
        showCityInGoogleMap(city, mFab);
    }

    @Override
    public Toolbar getParallaxToolbar() {
        if (getResources().getBoolean(R.bool.isParallaxScrollSupport)) {
            return mToolbar;
        }

        return null;
    }

    @Override
    public AppBarLayout getAppBar() {
        return mAppBar;
    }
}
