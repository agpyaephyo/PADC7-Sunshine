package net.aung.sunshine.events;

import net.aung.sunshine.data.responses.WeatherStatusListResponse;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.utils.SunshineConstants;

import java.util.List;

/**
 * Created by aung on 12/14/15.
 */
public class DataEvent {

    public static class RefreshNewWeatherDataEvent {

    }

    public static class NewWeatherStatusDetail {
        private WeatherStatusVO weatherStatus;

        public NewWeatherStatusDetail(WeatherStatusVO weatherStatus) {
            this.weatherStatus = weatherStatus;
        }

        public WeatherStatusVO getWeatherStatus() {
            return weatherStatus;
        }
    }

    public static class LoadedWeatherStatusListEvent {
        private WeatherStatusListResponse response;
        private int loadingType;

        public LoadedWeatherStatusListEvent(WeatherStatusListResponse response) {
            this.response = response;
        }

        public WeatherStatusListResponse getResponse() {
            return response;
        }
    }

    public static class LoadedWeatherStatusListErrorEvent {
        private @SunshineConstants.ServerStatus int status;
        private String error;

        public LoadedWeatherStatusListErrorEvent(String error, @SunshineConstants.ServerStatus int status) {
            this.error = error;
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public @SunshineConstants.ServerStatus int getStatus() {
            return status;
        }
    }

    public static class PreferenceCityChangeEvent {
        private String newCity;

        public PreferenceCityChangeEvent(String newCity) {
            this.newCity = newCity;
        }

        public String getNewCity() {
            return newCity;
        }
    }

    public static class PreferenceLocationChangeEvent {
        private String lat;
        private String lng;

        public PreferenceLocationChangeEvent(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }
    }

    public static class PreferenceNotificationChangeEvent {

        private boolean newPref;

        public PreferenceNotificationChangeEvent(boolean newPref) {
            this.newPref = newPref;
        }

        public boolean getNewPref() {
            return newPref;
        }
    }
}
