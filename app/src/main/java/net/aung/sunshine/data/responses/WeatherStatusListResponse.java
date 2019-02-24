package net.aung.sunshine.data.responses;

import com.google.gson.annotations.SerializedName;

import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.data.vos.CityVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aung on 12/14/15.
 */
public class WeatherStatusListResponse {

    @SerializedName("city")
    private CityVO city;

    @SerializedName("cod")
    private int cod;

    @SerializedName("message")
    private String message;

    @SerializedName("cnt")
    private int count;

    @SerializedName("list")
    private ArrayList<WeatherStatusVO> weatherStatusList;

    public CityVO getCity() {
        return city;
    }

    public int getCod() {
        return cod;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public ArrayList<WeatherStatusVO> getWeatherStatusList() {
        return weatherStatusList;
    }

    public static WeatherStatusListResponse createFromCache(ArrayList<WeatherStatusVO> weatherStatusList, CityVO city) {
        WeatherStatusListResponse response = new WeatherStatusListResponse();
        response.weatherStatusList = weatherStatusList;
        response.city = city;
        return response;
    }
}
