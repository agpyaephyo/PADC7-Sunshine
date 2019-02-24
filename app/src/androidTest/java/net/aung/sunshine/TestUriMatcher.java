package net.aung.sunshine;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.persistence.WeatherProvider;

/**
 * Created by aung on 2/10/16.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String TEST_LOCATION = "London";
    private static final long TEST_DATE = 1419033600L;
    private static final long TEST_LOCATION_ID = 10L;

    //content://net.aung.sunshine/weather
    private static final Uri TEST_WEATHER_DIR = WeatherContract.WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_LOCATION_DIR = WeatherContract.WeatherEntry.buildWeatherUri(TEST_LOCATION);
    private static final Uri TEST_WEATHER_WITH_LOCATION_DATE_ITEM = WeatherContract.WeatherEntry.buildWeatherUri(TEST_LOCATION, TEST_DATE);
    private static final Uri TEST_WEATHER_WITH_DATE_DIR = WeatherContract.WeatherEntry.buildWeatherUriWithDate(TEST_DATE);
    private static final Uri TEST_WEATHER_WITH_LOCATION_STARTDATE_DIR = WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(TEST_LOCATION, TEST_DATE);

    //content://net.aung.sunshine/location
    private static final Uri TEST_LOCATION_DIR = WeatherContract.CityEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher uriMatcher = WeatherProvider.buildUriMatcher();

        assertEquals("Error : The WEATHER URI was matched incorrectly.",
                uriMatcher.match(TEST_WEATHER_DIR), WeatherProvider.WEATHER);
        Log.d(SunshineApplication.TAG, "Correct : WEATHER URI - " + TEST_WEATHER_DIR);

        assertEquals("Error : The WEATHER WITH LOCATION URI was matched incorrectly.",
                uriMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), WeatherProvider.WEATHER_WITH_CITY);
        Log.d(SunshineApplication.TAG, "Correct : WEATHER WITH LOCATION URI - " + TEST_WEATHER_WITH_LOCATION_DIR);

        assertEquals("Error : The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.",
                uriMatcher.match(TEST_WEATHER_WITH_LOCATION_DATE_ITEM), WeatherProvider.WEATHER_WITH_CITY_AND_DATE);
        Log.d(SunshineApplication.TAG, "Correct : WEATHER WITH LOCATION DATE URI - " + TEST_WEATHER_WITH_LOCATION_DATE_ITEM);

        assertEquals("Error : The WEATHER WITH LOCATION AND START DATE URI was matched incorrectly.",
                uriMatcher.match(TEST_WEATHER_WITH_LOCATION_STARTDATE_DIR), WeatherProvider.WEATHER_WITH_CITY);
        Log.d(SunshineApplication.TAG, "Correct : WEATHER WITH LOCATION STARTDATE URI - " + TEST_WEATHER_WITH_LOCATION_STARTDATE_DIR);

        assertEquals("Error : The WEATHER WITH DATE URI was matched incorrectly.",
                uriMatcher.match(TEST_WEATHER_WITH_DATE_DIR), WeatherProvider.WEATHER);
        Log.d(SunshineApplication.TAG, "Correct : WEATHER WITH DATE URI - " + TEST_WEATHER_WITH_DATE_DIR);

        assertEquals("Error : The LOCATION URI was matched incorrectly",
                uriMatcher.match(TEST_LOCATION_DIR), WeatherProvider.LOCATION);
        Log.d(SunshineApplication.TAG, "Correct : LOCATION URI - " + TEST_LOCATION_DIR);
    }
}
