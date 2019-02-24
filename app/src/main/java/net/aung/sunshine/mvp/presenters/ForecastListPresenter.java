package net.aung.sunshine.mvp.presenters;

import android.content.Context;
import android.util.Log;

import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.models.WeatherStatusModel;
import net.aung.sunshine.events.DataEvent;
import net.aung.sunshine.mvp.views.ForecastListView;
import net.aung.sunshine.services.TodayWidgetIntentService;
import net.aung.sunshine.sync.SunshineSyncAdapter;
import net.aung.sunshine.utils.NotificationUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;

/**
 * Created by aung on 12/14/15.
 */
public class ForecastListPresenter extends BasePresenter {

    private ForecastListView forecastListView;

    public ForecastListPresenter(ForecastListView forecastListView) {
        this.forecastListView = forecastListView;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    public void onEventMainThread(DataEvent.RefreshNewWeatherDataEvent event) {
        SettingsUtils.saveServerResponseStatus(SunshineConstants.STATUS_SERVER_OK);
        forecastListView.refreshNewWeatherData();

        SunshineSyncAdapter.syncImmediately(SunshineApplication.getContext());
    }

    public void onEventMainThread(DataEvent.LoadedWeatherStatusListErrorEvent event) {
        SettingsUtils.saveServerResponseStatus(event.getStatus());
        forecastListView.displayErrorMessage(event);
    }

    public void forceRefresh() {
        Log.d(SunshineApplication.TAG, "Force refresh weather data.");
        WeatherStatusModel.getInstance().loadWeatherStatusList(true);
    }

}
