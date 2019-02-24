package net.aung.sunshine.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.events.DataEvent;
import net.aung.sunshine.mvp.presenters.ForecastDetailPresenter;
import net.aung.sunshine.mvp.views.ForecastDetailView;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;
import net.aung.sunshine.views.ViewPodWeatherHPW;
import net.aung.sunshine.views.ViewPodWeatherInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForecastDetailFragment extends BaseFragment
        implements ForecastDetailView,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_DT = "ARG_DT";

    public static final String TAG = ForecastDetailFragment.class.getSimpleName();

    private long dateTime;
    private ForecastDetailPresenter presenter;

    @Bind(R.id.vp_weather_info)
    View vpWeatherInfoView;

    @Bind(R.id.vp_weather_hpw)
    View vpWeatherHPWView;

    @Bind(R.id.iv_status_art)
    ImageView ivWeatherIcon;

    private View rootView;
    private ViewPodWeatherInfo vpWeatherInfo;
    private ViewPodWeatherHPW vpWeatherHPW;

    private ShareActionProvider mShareActionProvider;
    private WeatherStatusVO mStatus;

    public ForecastDetailFragment() {

    }

    public static ForecastDetailFragment newInstance(long dateTime) {
        ForecastDetailFragment fragment = new ForecastDetailFragment();
        fragment.dateTime = dateTime;

        Bundle args = new Bundle();
        args.putLong(ARG_DT, dateTime);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ForecastDetailPresenter(this, dateTime);
        presenter.onCreate();

        setHasOptionsMenu(true);
    }

    @Override
    protected void readArguments(Bundle bundle) {
        super.readArguments(bundle);
        dateTime = bundle.getLong(ARG_DT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_forecast_detail, container, false);
        ButterKnife.bind(this, rootView);

        vpWeatherInfo = new ViewPodWeatherInfo(vpWeatherInfoView);
        vpWeatherHPW = new ViewPodWeatherHPW(vpWeatherHPWView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ivWeatherIcon.setTransitionName(getString(R.string.detail_icon_transition_name));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SunshineConstants.FORECAST_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_detail, menu);

        MenuItem shareMenuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Snackbar.make(rootView, "ShareActionProvider is being null. Why ?", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String city = SettingsUtils.retrieveUserCity();
        Log.d(SunshineApplication.TAG, "Retrieving weather detail data for city (from db) : " + city);

        if (dateTime == SunshineConstants.TODAY) {
            return new CursorLoader(getActivity(),
                    WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY),
                    null, //projections
                    null, //selection
                    null, //selectionArgs
                    WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        } else {
            return new CursorLoader(getActivity(),
                    WeatherContract.WeatherEntry.buildWeatherUri(city, dateTime),
                    null, //projections
                    null, //selection
                    null, //selectionArgs
                    null); //sort
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorWeather) {
        WeatherStatusVO weatherStatusDetail = null;
        if (cursorWeather.moveToFirst()) {
            weatherStatusDetail = WeatherStatusVO.parseFromCursor(cursorWeather);

            this.mStatus = weatherStatusDetail;

            vpWeatherInfo.bind(weatherStatusDetail);
            vpWeatherHPW.bind(weatherStatusDetail);

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.supportStartPostponedEnterTransition(); //Start the shared transition only after the data is being loaded in detail screen.
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //what to show in detail when the cursor to uri got reset ?
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_DT, dateTime);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            dateTime = savedInstanceState.getLong(ARG_DT, SunshineConstants.TODAY);
            getLoaderManager().restartLoader(SunshineConstants.FORECAST_DETAIL_LOADER, null, this);
        }
    }

    public void updateForecastDetail(WeatherStatusVO newWeatherStatus) {
        if (newWeatherStatus != null) {
            dateTime = newWeatherStatus.getDateTime();
            this.mStatus = newWeatherStatus;

            vpWeatherInfo.bind(newWeatherStatus);
            vpWeatherHPW.bind(newWeatherStatus);
        }
    }

    public void onEventMainThread(DataEvent.PreferenceCityChangeEvent event) {
        getLoaderManager().restartLoader(SunshineConstants.FORECAST_DETAIL_LOADER, null, this);
    }

    private Intent createShareIntent() {
        Intent myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        myShareIntent.setType("text/plain");
        myShareIntent.putExtra(Intent.EXTRA_TEXT, "Hi, my name is Sunshine.");
        return myShareIntent;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        if (key.equals(getString(R.string.pref_icon_key))) {
            vpWeatherInfo.bind(mStatus);
        }
    }
}
