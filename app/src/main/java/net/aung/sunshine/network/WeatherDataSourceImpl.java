package net.aung.sunshine.network;

import android.content.Context;

import net.aung.sunshine.BuildConfig;
import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.responses.WeatherStatusListResponse;
import net.aung.sunshine.events.DataEvent;
import net.aung.sunshine.utils.CommonInstances;
import net.aung.sunshine.utils.SunshineConstants;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by aung on 12/14/15.
 */
public class WeatherDataSourceImpl implements WeatherDataSource {

    private static WeatherDataSource objInstance;
    private final OWMApi owmApi;

    private WeatherDataSourceImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(CommonInstances.getGsonInstance()))
                .build();

        owmApi = retrofit.create(OWMApi.class);
    }

    public static WeatherDataSource getInstance() {
        if (objInstance == null) {
            objInstance = new WeatherDataSourceImpl();
        }

        return objInstance;
    }

    @Override
    public void getWeatherForecastList(final String city) {
        Call<WeatherStatusListResponse> weatherForecastListCall = owmApi.getDailyForecast(
                city,
                BuildConfig.OPEN_WEATHER_MAP_API_KEY,
                NetworkConstants.RESPONSE_FORMAT_JSON,
                NetworkConstants.RESPONSE_UNIT_METRIC,
                NetworkConstants.RESPONSE_COUNT_DEFAULT
        );
        weatherForecastListCall.enqueue(new Callback<WeatherStatusListResponse>() {
            @Override
            public void onResponse(Response<WeatherStatusListResponse> response, Retrofit retrofit) {
                WeatherStatusListResponse weatherStatusListResponse = response.body();
                if (weatherStatusListResponse == null) {
                    DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(response.message(), SunshineConstants.STATUS_SERVER_UNKNOWN);
                    EventBus.getDefault().post(event);
                } else {
                    int serverResponseCode = weatherStatusListResponse.getCod();
                    if (serverResponseCode == NetworkConstants.SERVER_RESPONSE_OK) {
                        if (city.equalsIgnoreCase(weatherStatusListResponse.getCity().getName())) {
                            DataEvent.LoadedWeatherStatusListEvent event = new DataEvent.LoadedWeatherStatusListEvent(response.body());
                            EventBus.getDefault().post(event);
                        } else {
                            Context context = SunshineApplication.getContext();
                            DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(context.getString(R.string.error_city_not_found, city), SunshineConstants.STATUS_SERVER_CITY_NOT_FOUND);
                            EventBus.getDefault().post(event);
                        }

                    } else if (serverResponseCode == NetworkConstants.SERVER_RESPONSE_CITY_NOT_FOUND) {
                        DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(weatherStatusListResponse.getMessage(), SunshineConstants.STATUS_SERVER_CITY_NOT_FOUND);
                        EventBus.getDefault().post(event);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(throwable.getMessage(), SunshineConstants.STATUS_SERVER_INVALID);
                EventBus.getDefault().post(event);
            }
        });
    }

    @Override
    public void getWeatherForecastListByLatLng(String lat, String lng) {
        Call<WeatherStatusListResponse> weatherForecastListCall = owmApi.getDailyForecastByLatLng(
                lat,
                lng,
                BuildConfig.OPEN_WEATHER_MAP_API_KEY,
                NetworkConstants.RESPONSE_FORMAT_JSON,
                NetworkConstants.RESPONSE_UNIT_METRIC,
                NetworkConstants.RESPONSE_COUNT_DEFAULT
        );
        weatherForecastListCall.enqueue(new Callback<WeatherStatusListResponse>() {
            @Override
            public void onResponse(Response<WeatherStatusListResponse> response, Retrofit retrofit) {
                WeatherStatusListResponse weatherStatusListResponse = response.body();
                if (weatherStatusListResponse == null) {
                    DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(response.message(), SunshineConstants.STATUS_SERVER_UNKNOWN);
                    EventBus.getDefault().post(event);
                } else {
                    int serverResponseCode = weatherStatusListResponse.getCod();
                    if (serverResponseCode == NetworkConstants.SERVER_RESPONSE_OK) {
                        DataEvent.LoadedWeatherStatusListEvent event = new DataEvent.LoadedWeatherStatusListEvent(response.body());
                        EventBus.getDefault().post(event);
                    } else if (serverResponseCode == NetworkConstants.SERVER_RESPONSE_CITY_NOT_FOUND) {
                        DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(weatherStatusListResponse.getMessage(), SunshineConstants.STATUS_SERVER_CITY_NOT_FOUND);
                        EventBus.getDefault().post(event);
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                DataEvent.LoadedWeatherStatusListErrorEvent event = new DataEvent.LoadedWeatherStatusListErrorEvent(throwable.getMessage(), SunshineConstants.STATUS_SERVER_INVALID);
                EventBus.getDefault().post(event);
            }
        });
    }
}
