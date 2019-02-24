package net.aung.sunshine.data.vos;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.utils.DateFormatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This object is immutable.
 */
public class WeatherStatusVO {

    @SerializedName("dt")
    private long dateTime;

    @SerializedName("temp")
    private TemperatureVO temperature;

    @SerializedName("pressure")
    private double pressure;

    @SerializedName("humidity")
    private double humidity;

    @SerializedName("weather")
    private ArrayList<WeatherVO> weatherList;

    @SerializedName("speed")
    private double windSpeed;

    @SerializedName("deg")
    private int deg;

    @SerializedName("clouds")
    private int clouds;

    @SerializedName("rain")
    private double rain;

    private Date date;

    public long getDateTime() {
        return dateTime;
    }

    public TemperatureVO getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public String getPressureDisplay() {
        return String.valueOf(pressure) + " hPa";
    }

    public double getHumidity() {
        return humidity;
    }

    public String getHumidityDisplay() {
        return String.valueOf(humidity) + " %";
    }

    public ArrayList<WeatherVO> getWeatherList() {
        return weatherList;
    }

    public WeatherVO getWeather() {
        return weatherList.get(0);
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getWindSpeedDisplay() {
        return String.valueOf(windSpeed) + " km/h SE";
    }

    public int getDeg() {
        return deg;
    }

    public int getClouds() {
        return clouds;
    }

    public double getRain() {
        return rain;
    }

    public String getDateDisplay() {
        if (date == null) {
            date = new Date(dateTime * 1000);
        }

        String dateText;

        Calendar calendar = Calendar.getInstance();
        int todayDate = calendar.get(Calendar.DATE);
        calendar.setTime(date);
        int weatherDate = calendar.get(Calendar.DATE);
        if (todayDate == weatherDate) {
            //today
            dateText = SunshineApplication.getContext().getString(R.string.lbl_today) + ", " + DateFormatUtils.sdfWeatherStatusDateToday.format(date);
        } else if (todayDate + 1 == weatherDate) {
            //tomorrow
            dateText = SunshineApplication.getContext().getString(R.string.lbl_tomorrow) + ", " + DateFormatUtils.sdfWeatherStatusDateTomorrow.format(date);
        } else {
            //the rest
            dateText = DateFormatUtils.sdfWeatherStatusDate.format(date);
        }

        return dateText;
    }

    public String getDay() {
        if (date == null) {
            date = new Date(dateTime * 1000);
        }

        String dateText;

        Calendar calendar = Calendar.getInstance();
        int todayDate = calendar.get(Calendar.DATE);
        calendar.setTime(date);
        int weatherDate = calendar.get(Calendar.DATE);
        if (todayDate == weatherDate) {
            //today
            dateText = SunshineApplication.getContext().getString(R.string.lbl_today);
        } else if (todayDate + 1 == weatherDate) {
            //tomorrow
            dateText = SunshineApplication.getContext().getString(R.string.lbl_tomorrow);
        } else {
            //the rest
            dateText = DateFormatUtils.sdfDay.format(date);
        }

        return dateText;
    }

    public String getDetailDate() {
        if (date == null) {
            date = new Date(dateTime * 1000);
        }

        return DateFormatUtils.sdfWeatherStatusDateToday.format(date);
    }

    public String getDateDayCombined() {
        return getDay() + ", " + getDetailDate();
    }

    private ContentValues getContentValues(long cityRowId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_LOCATION_ID, cityRowId);
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);

        contentValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);

        contentValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMPERATURE, temperature.getMaxTemperature());
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMPERATURE, temperature.getMinTemperature());

        contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC, getWeather().getDescription());
        contentValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_CONDITION_ID, getWeather().getId());

        return contentValues;
    }

    public static WeatherStatusVO parseFromCursor(Cursor cursor) {
        WeatherStatusVO weatherStatus = new WeatherStatusVO();

        weatherStatus.dateTime = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        weatherStatus.humidity = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        weatherStatus.pressure = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        weatherStatus.windSpeed = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));


        weatherStatus.temperature = TemperatureVO.parseFromCursor(cursor);
        weatherStatus.weatherList = new ArrayList<>();
        weatherStatus.weatherList.add(WeatherVO.parseFromCursor(cursor));

        return weatherStatus;
    }

    public static ContentValues[] parseToContentValuesArray(List<WeatherStatusVO> weatherStatusList, long cityRowId) {
        ContentValues[] contentValueArray = new ContentValues[weatherStatusList.size()];

        for (int index = 0;index<contentValueArray.length;index++) {
            WeatherStatusVO weatherStatus = weatherStatusList.get(index);
            contentValueArray[index] = weatherStatus.getContentValues(cityRowId);
        }

        return contentValueArray;
    }
}
