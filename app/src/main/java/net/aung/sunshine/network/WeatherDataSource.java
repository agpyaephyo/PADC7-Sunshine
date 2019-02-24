package net.aung.sunshine.network;

/**
 * Created by aung on 12/14/15.
 */
public interface WeatherDataSource {
    void getWeatherForecastList(String cityName);
    void getWeatherForecastListByLatLng(String lat, String lng);
}
