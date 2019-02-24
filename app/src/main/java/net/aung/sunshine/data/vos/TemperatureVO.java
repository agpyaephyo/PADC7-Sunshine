package net.aung.sunshine.data.vos;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.utils.SettingsUtils;

/**
 * Created by aung on 12/14/15.
 */
public class TemperatureVO {

    @SerializedName("day")
    private double dayTemperature;

    @SerializedName("night")
    private double nightTemperature;

    @SerializedName("min")
    private double minTemperature;

    @SerializedName("max")
    private double maxTemperature;

    @SerializedName("morn")
    private double morningTemperature;

    @SerializedName("eve")
    private double eveningTemperature;

    public String getDayTemperatureDisplay() {
        double temperature = getTemperatureBySelectedUnit(dayTemperature);
        return formatTemperature(temperature);
    }

    public String getNightTemperatureDisplay() {
        double temperature = getTemperatureBySelectedUnit(nightTemperature);
        return formatTemperature(temperature);
    }

    public String getMinTemperatureDisplay() {
        double temperature = getTemperatureBySelectedUnit(minTemperature);
        return formatTemperature(temperature);
    }

    public String getMaxTemperatureDisplay() {
        double temperature = getTemperatureBySelectedUnit(maxTemperature);
        return formatTemperature(temperature);
    }

    public String getMorningTemperatureDisplay() {
        double temperature = getTemperatureBySelectedUnit(morningTemperature);
        return formatTemperature(temperature);
    }

    public String getEveningTemperatureDisplay() {
        double temperature = getTemperatureBySelectedUnit(eveningTemperature);
        return formatTemperature(temperature);
    }

    public double getDayTemperature() {
        return getTemperatureBySelectedUnit(dayTemperature);
    }

    public double getNightTemperature() {
        return getTemperatureBySelectedUnit(nightTemperature);
    }

    public double getMinTemperature() {
        return getTemperatureBySelectedUnit(minTemperature);
    }

    public double getMaxTemperature() {
        return getTemperatureBySelectedUnit(maxTemperature);
    }

    public double getMorningTemperature() {
        return getTemperatureBySelectedUnit(morningTemperature);
    }

    public double getEveningTemperature() {
        return getTemperatureBySelectedUnit(eveningTemperature);
    }

    private double getTemperatureBySelectedUnit(double temperature) {
        String selectedUnit = SettingsUtils.retrieveSelectedUnit();
        Context context = SunshineApplication.getContext();
        if(selectedUnit.equalsIgnoreCase(context.getString(R.string.pref_unit_imperial))) {
            temperature = (temperature * 1.8) + 32;
        }

        return temperature;
    }

    public static TemperatureVO parseFromCursor(Cursor cursor) {
        TemperatureVO temperature = new TemperatureVO();
        temperature.minTemperature = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMPERATURE));
        temperature.maxTemperature = cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMPERATURE));
        return temperature;
    }

    private String formatTemperature(double temperature) {
        Context context = SunshineApplication.getContext();
        return context.getString(R.string.format_temperature, (int) temperature);
    }
}
