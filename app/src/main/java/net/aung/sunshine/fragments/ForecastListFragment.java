package net.aung.sunshine.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.activities.SettingsActivity;
import net.aung.sunshine.adapters.ForecastListAdapter;
import net.aung.sunshine.components.RecyclerViewWithEmptyView;
import net.aung.sunshine.controllers.ForecastListScreenController;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.events.DataEvent;
import net.aung.sunshine.mvp.presenters.ForecastListPresenter;
import net.aung.sunshine.mvp.views.ForecastListView;
import net.aung.sunshine.utils.NetworkUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForecastListFragment extends BaseFragment
        implements ForecastListView,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_SELECTED_ROW = "ARG_SELECTED_ROW";
    public static final String TAG = ForecastListFragment.class.getSimpleName();

    @Bind(R.id.rv_forecasts)
    RecyclerViewWithEmptyView rvForecasts;

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @Bind(R.id.vp_empty_forecasts)
    TextView tvEmptyForecasts;

    private View rootView;

    private ForecastListAdapter adapter;
    private ForecastListPresenter presenter;
    private ForecastListScreenController controller;

    private int mSelectedRow = RecyclerView.NO_POSITION;
    private boolean mLanguageSettingChange = false;
    private List<WeatherStatusVO> mWeatherStatusList = null;

    public static ForecastListFragment newInstance(int position) {
        ForecastListFragment fragment = new ForecastListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SELECTED_ROW, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ForecastListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ForecastListScreenController) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ForecastListPresenter(this);
        presenter.onCreate();

        setHasOptionsMenu(true);
    }

    @Override
    protected void readArguments(Bundle bundle) {
        super.readArguments(bundle);
        mSelectedRow = bundle.getInt(ARG_SELECTED_ROW);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_forecast_list, container, false);
        ButterKnife.bind(this, rootView);

        adapter = ForecastListAdapter.newInstance(controller, rvForecasts);

        rvForecasts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvForecasts.setAdapter(adapter);
        rvForecasts.setEmptyView(tvEmptyForecasts);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark);

        swipeContainer.setRefreshing(true);

        final Toolbar parallaxToolbar = controller.getParallaxToolbar();
        if (parallaxToolbar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            rvForecasts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int max = parallaxToolbar.getHeight();
                    if (dy > 0) {
                        parallaxToolbar.setTranslationY(Math.max(-max,
                                parallaxToolbar.getTranslationY() - dy / 3));
                    } else {
                        parallaxToolbar.setTranslationY(Math.min(0,
                                parallaxToolbar.getTranslationY() - dy / 3));
                    }
                }
            });
        }

        final AppBarLayout appBar = controller.getAppBar();
        if (appBar != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rvForecasts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (rvForecasts.computeVerticalScrollOffset() == 0) {
                        appBar.setElevation(0);
                    } else {
                        appBar.setElevation(getResources().getDimension(R.dimen.toolbar_elevation));
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SunshineConstants.FORECAST_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forecast_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*
            case R.id.action_filter:
                Snackbar.make(rootView, "Later, you will be able to filter the list of dates that has specific weathers", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                break;
            */
            case R.id.action_settings:
                startSettingActivity();
                break;
            case R.id.action_show_city:
                String city = SettingsUtils.retrieveUserCity();
                controller.showCityInGoogleMap(city);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSettingActivity() {
        Intent intentToSettings = SettingsActivity.newIntent(getActivity());
        startActivity(intentToSettings);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLanguageSettingChange) {
            mLanguageSettingChange = false;
            adapter = ForecastListAdapter.newInstance(controller, rvForecasts);
            rvForecasts.setAdapter(adapter);
            adapter.setStatusList(mWeatherStatusList);
        }
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
        if (rvForecasts != null) {
            rvForecasts.clearOnScrollListeners();
        }
    }

    @Override
    public void refreshNewWeatherData() {
        //won't display when the data is coming back from db.
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
            Snackbar.make(rootView, getString(R.string.msg_new_data_has_refreshed), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void displayErrorMessage(DataEvent.LoadedWeatherStatusListErrorEvent event) {
        if (swipeContainer.isRefreshing()) {
            swipeContainer.setRefreshing(false);
        }

        //will show the error message from server directly. mostly because api server is 3rd party.
        String errorMsg = getString(R.string.format_no_weather_information, event.getError());

        Snackbar.make(rootView, errorMsg, Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show();

        // if we own the api server (or know every error response), will show tailored error message based on the error type.
        switch (event.getStatus()) {
            case SunshineConstants.STATUS_SERVER_INVALID:
                break;
            case SunshineConstants.STATUS_SERVER_DOWN:
                break;
            case SunshineConstants.STATUS_SERVER_CITY_NOT_FOUND:
                break;
        }

        tvEmptyForecasts.setText(errorMsg);
    }

    @Override
    public void onRefresh() {
        presenter.forceRefresh();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String city = SettingsUtils.retrieveUserCity();
        if (city != null) {
            Log.d(SunshineApplication.TAG, "Retrieving weather data for city (from db) : " + city);

            return new CursorLoader(getActivity(),
                    WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY),
                    null, //projections
                    null, //selection
                    null, //selectionArgs
                    WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorWeather) {
        mWeatherStatusList = new ArrayList<>();
        if (cursorWeather.moveToFirst()) {
            do {
                mWeatherStatusList.add(WeatherStatusVO.parseFromCursor(cursorWeather));
            } while (cursorWeather.moveToNext());
        }

        adapter.setStatusList(mWeatherStatusList);

        if (mWeatherStatusList.size() == 0 && !NetworkUtils.isOnline(getContext())) {
            tvEmptyForecasts.setText(getString(R.string.error_no_network));
        }

        if (mSelectedRow != RecyclerView.NO_POSITION) {
            if (getResources().getBoolean(R.bool.isTwoPane)) {
                adapter.setSelectedRow(mSelectedRow);
            }
            rvForecasts.smoothScrollToPosition(mSelectedRow);
        } else {
            if (getResources().getBoolean(R.bool.isTwoPane)) {
                adapter.setSelectedRow(0);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setStatusList(new ArrayList<WeatherStatusVO>());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            outState.putInt(ARG_SELECTED_ROW, adapter.getSelectedRow());
        } else {
            if (mSelectedRow != RecyclerView.NO_POSITION) {
                outState.putInt(ARG_SELECTED_ROW, mSelectedRow);
            }
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        //all of the following could also be done by reading the bundle inside onCreateView.
        if (savedInstanceState != null) {
            mSelectedRow = savedInstanceState.getInt(ARG_SELECTED_ROW, RecyclerView.NO_POSITION);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
        if (key.equals(getString(R.string.pref_language_key))) {
            mLanguageSettingChange = true;
        }
    }

    public void onEventMainThread(DataEvent.PreferenceCityChangeEvent event) {
        getLoaderManager().restartLoader(SunshineConstants.FORECAST_LIST_LOADER, null, this);
    }
}
