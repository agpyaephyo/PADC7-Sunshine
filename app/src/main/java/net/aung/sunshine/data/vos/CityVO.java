package net.aung.sunshine.data.vos;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import net.aung.sunshine.data.persistence.WeatherContract;

/**
 * Created by aung on 12/14/15.
 */
public class CityVO {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("coord")
    private CoordinateVO coordinates;

    @SerializedName("country")
    private String country;

    @SerializedName("population")
    private long population;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CoordinateVO getCoordinates() {
        return coordinates;
    }

    public String getCountry() {
        return country;
    }

    public long getPopulation() {
        return population;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.CityEntry.COLUMN_CITY_NAME, name);
        contentValues.put(WeatherContract.CityEntry.COLUMN_COORD_LAT, coordinates.getLatitude());
        contentValues.put(WeatherContract.CityEntry.COLUMN_COORD_LNG, coordinates.getLongitude());
        contentValues.put(WeatherContract.CityEntry.COLUMN_COUNTRY, country);
        contentValues.put(WeatherContract.CityEntry.COLUMN_POPULATION, population);

        return contentValues;
    }

    public static CityVO parseFromCursor(Cursor cursor) {
        CityVO city = new CityVO();
        city.name = cursor.getString(cursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_CITY_NAME));
        city.coordinates = CoordinateVO.parseFromCursor(cursor);
        city.country = cursor.getString(cursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_COUNTRY));
        city.population = cursor.getLong(cursor.getColumnIndex(WeatherContract.CityEntry.COLUMN_POPULATION));
        return city;
    }
}
