package net.aung.sunshine.controllers;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import net.aung.sunshine.data.vos.WeatherStatusVO;

/**
 * Created by aung on 12/15/15.
 */
public interface ForecastListScreenController extends BaseController {

    void onNavigateToForecastDetail(ImageView ivWeatherIcon, WeatherStatusVO weatherStatus);

    void showCityInGoogleMap(String city);

    Toolbar getParallaxToolbar();

    AppBarLayout getAppBar();
}
