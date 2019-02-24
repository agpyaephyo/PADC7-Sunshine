package net.aung.sunshine.mvp.presenters;

import net.aung.sunshine.mvp.views.ForecastDetailView;

/**
 * Created by aung on 12/15/15.
 */
public class ForecastDetailPresenter extends BasePresenter {

    private ForecastDetailView forecastDetailView;
    private long dateForWeatherDetail;

    public ForecastDetailPresenter(ForecastDetailView forecastDetailView, long dateForWeatherDetail) {
        this.forecastDetailView = forecastDetailView;
        this.dateForWeatherDetail = dateForWeatherDetail;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    public void onEventMainThread(Object event) {

    }
}
