package net.aung.sunshine.data.vos;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import net.aung.sunshine.data.persistence.WeatherContract;

/**
 * Created by aung on 12/14/15.
 */
public class CoordinateVO {

    @SerializedName("lon")
    private double longitude;

    @SerializedName("lat")
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public static CoordinateVO parseFromCursor(Cursor cursor) {
        CoordinateVO coordinate = new CoordinateVO();
        coordinate.latitude = cursor.getDouble(cursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_COORD_LAT));
        coordinate.longitude = cursor.getDouble(cursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_COORD_LNG));
        return coordinate;
    }
}
