package net.aung.sunshine.data.vos;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.utils.WeatherDataUtils;

/**
 * Created by aung on 12/14/15.
 */
public class WeatherVO {

    @SerializedName("id")
    private int id;

    @SerializedName("main")
    private String main;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return WeatherDataUtils.getWeatherDescription(id);
    }

    public String getIcon() {
        return icon;
    }

    public static WeatherVO parseFromCursor(Cursor cursor) {
        WeatherVO weather = new WeatherVO();
        weather.id = cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_CONDITION_ID));
        weather.description = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC));
        return weather;
    }
}
