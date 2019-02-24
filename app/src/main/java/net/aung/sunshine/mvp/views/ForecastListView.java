package net.aung.sunshine.mvp.views;

import net.aung.sunshine.events.DataEvent;

/**
 * Created by aung on 12/14/15.
 */
public interface ForecastListView {
    void refreshNewWeatherData();

    void displayErrorMessage(DataEvent.LoadedWeatherStatusListErrorEvent event);
}
