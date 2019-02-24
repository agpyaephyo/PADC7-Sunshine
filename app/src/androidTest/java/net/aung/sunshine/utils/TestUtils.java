package net.aung.sunshine.utils;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import net.aung.sunshine.data.persistence.WeatherContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by aung on 2/9/16.
 */
public class TestUtils extends AndroidTestCase {

    public static final String TEST_LOCATION_NORTH = "North Pole";
    public static final String TEST_LOCATION_SOUTH = "South Pole";
    public static final long TEST_DATE = 1419033600L; // December 20th, 2014

    public static ContentValues createNorthPoleLocationValues() {
        ContentValues northPoleLocationValues = new ContentValues();
        northPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_CITY_NAME, TEST_LOCATION_NORTH);
        northPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_COORD_LAT, 64.7488);
        northPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_COORD_LNG, -147.353);
        northPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_COUNTRY, "ABC");
        northPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_POPULATION, 11223344);

        return northPoleLocationValues;
    }

    public static ContentValues createSouthPoleLocationValues() {
        ContentValues southPoleLocationValues = new ContentValues();
        southPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_CITY_NAME, TEST_LOCATION_SOUTH);
        southPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_COORD_LAT, 4.7488);
        southPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_COORD_LNG, -7.353);
        southPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_COUNTRY, "DEF");
        southPoleLocationValues.put(WeatherContract.CityEntry.COLUMN_POPULATION, 55667788);

        return southPoleLocationValues;
    }

    public static ContentValues createTestWeatherValues(long locationId) {
        return createTestWeatherValues(locationId, TEST_DATE);
    }

    public static ContentValues createTestWeatherValues(long locationId, long date) {
        ContentValues testWeatherValues = new ContentValues();
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOCATION_ID, locationId);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, date);

        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMPERATURE, 75);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMPERATURE, 65);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC, "Asteroids");
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        testWeatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_CONDITION_ID, 321);

        return testWeatherValues;
    }

    public static void validateCurrentCursorRow(String errorMsg, Cursor cursor, ContentValues values) {
        Set<Map.Entry<String, Object>> valueSet = values.valueSet();
        for (Map.Entry<String, Object> eachEntry : valueSet) {
            String columnName = eachEntry.getKey();
            int columnIndex = cursor.getColumnIndex(columnName);
            assertTrue(errorMsg + " The column, "+columnName+" is not being retrieved.", columnIndex != -1);

            String value = eachEntry.getValue().toString();
            String cursorValue = cursor.getString(columnIndex);
            assertEquals(errorMsg + " The value for column, "+columnName+" is expected to be "+value+". And cursor has "+cursorValue+".", value, cursorValue);
        }
    }

    public static class TestContentObserver extends ContentObserver {

        private final HandlerThread mHT;
        private boolean mContentChanged;

        public static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread(TestContentObserver.class.getName());
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {

                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }
}
