package net.aung.sunshine;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.persistence.WeatherDBHelper;
import net.aung.sunshine.data.persistence.WeatherProvider;
import net.aung.sunshine.utils.TestUtils;

/**
 * Created by aung on 2/10/16.
 */
public class TestContentProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    private void deleteAllRecordsFromProvider() {
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase();
        db.delete(WeatherContract.WeatherEntry.TABLE_NAME,
                null,
                null
        );

        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Some records in weather table cannot be deleted.",
                weatherCursor.getCount(), 0);
        weatherCursor.close();

        db.delete(WeatherContract.CityEntry.TABLE_NAME,
                null,
                null
        );

        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.CityEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Some records in location table cannot be deleted.",
                locationCursor.getCount(), 0);
        locationCursor.close();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), WeatherProvider.class.getName()); //packageName + Provider class name.

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0); //flag for getting provider info. GET_META_DATA. GET_SHARED_LIBRARY_FILES.

            assertEquals("Error : Provider registered with authority " + providerInfo.authority + " instead of " + WeatherContract.CONTENT_AUTHORITY,
                    providerInfo.authority, WeatherContract.CONTENT_AUTHORITY);
            Log.d(SunshineApplication.TAG, "Correct : Provider registered with authority " + providerInfo.authority);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            assertTrue("Error : ContentProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        //content://net.aung.sunshine/weather
        String weatherMIME = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        assertEquals("Error : Type for weather should be DIR instead of " + weatherMIME,
                WeatherContract.WeatherEntry.DIR_TYPE, weatherMIME);
        Log.d(SunshineApplication.TAG, "Correct : Type for weather - " + weatherMIME);

        //content://net.aung.sunshine/weather?date=12345678L
        String weatherDateMIME = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherUriWithDate(1345678L));
        assertEquals("Error : Type for weather should be DIR instead of " + weatherDateMIME,
                WeatherContract.WeatherEntry.DIR_TYPE, weatherDateMIME);
        Log.d(SunshineApplication.TAG, "Correct : Type for weather date - " + weatherDateMIME);

        //content://net.aung.sunshine/weather/London
        String weatherLocationMIME = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherUri("London"));
        assertEquals("Error : Type for weather/London should be DIR instead of " + weatherLocationMIME,
                WeatherContract.WeatherEntry.DIR_TYPE, weatherLocationMIME);
        Log.d(SunshineApplication.TAG, "Correct : Type for weather/London - " + weatherLocationMIME);

        //content://net.aung.sunshine/weather/London?date=12345678L
        String weatherLocationStartDateMIME = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherUriWithStartDate("London", 1345678L));
        assertEquals("Error : Type for weather/London should be DIR instead of " + weatherLocationStartDateMIME,
                WeatherContract.WeatherEntry.DIR_TYPE, weatherLocationStartDateMIME);
        Log.d(SunshineApplication.TAG, "Correct : Type for weather/London?date=12345678L - " + weatherLocationStartDateMIME);

        //content://net.aung.sunshine/weather/London/1345678L
        String weatehrLocationDateMIME = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherUri("London", 1345678L));
        assertEquals("Error : Type for weather/London/12345678L should be ITEM instead of " + weatehrLocationDateMIME,
                WeatherContract.WeatherEntry.ITEM_TYPE, weatehrLocationDateMIME);
        Log.d(SunshineApplication.TAG, "Correct : Type for weather/London/12345678L - " + weatehrLocationDateMIME);

        //content://net.aung.sunshine/location
        String locationMIME = mContext.getContentResolver().getType(WeatherContract.CityEntry.CONTENT_URI);
        assertEquals("Error : Type for location should be DIR instead of " + locationMIME,
                WeatherContract.CityEntry.DIR_TYPE, locationMIME);
        Log.d(SunshineApplication.TAG, "Correct : Type for location - " + locationMIME);
    }

    public void testLocationInsertQuery() {
        ContentValues northPoleLocation = TestUtils.createNorthPoleLocationValues();
        /* Insert via db.
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase();
        long locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, northPoleLocation);

        assertTrue("Unable to insert into location table",
                locationRowId != -1);
        Log.d(SunshineApplication.TAG, "Inserted north pole values into location table.");

        db.close();
        */

        //Insert via ContentProvider.
        mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, northPoleLocation);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. North pole values into location table.");

        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.CityEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Unable to retrieve from location table",
                locationCursor.moveToFirst());
        Log.d(SunshineApplication.TAG, "Successfully retrieved from location table by using content resolver");

        TestUtils.validateCurrentCursorRow("Retrieved values from location table by content resolver is not correct",
                locationCursor,
                northPoleLocation);
        Log.d(SunshineApplication.TAG, "Retrieved values from location table by using content resolver are correct");

        assertFalse("Location cursor retrieved by using content resolver has more than one row",
                locationCursor.moveToNext());
        Log.d(SunshineApplication.TAG, "Location cursor retrieved by content resolver has only one row.");
        locationCursor.close();
    }

    //User the database directly to insert the data.
    //And use Content Provider to query the data back.
    //- weather list by location_setting
    //- weather list by date
    //- weather list by location_setting + date
    //- weather list by location_setting AND start_date
    public void testWeatherInsertQuery() {
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase();

        ContentValues northPoleLocation = TestUtils.createNorthPoleLocationValues();

        /*
        long locationNorthId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, northPoleLocation);

        assertTrue("Unable to insert north pole values into location table",
                locationNorthId != -1);
        Log.d(SunshineApplication.TAG, "Inserted north pole values into location table.");
        */
        Uri northPoleLocationUri = mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, northPoleLocation);
        long locationNorthId = ContentUris.parseId(northPoleLocationUri);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. North pole values into location table.");

        ContentValues southPoleLocation = TestUtils.createSouthPoleLocationValues();

        /*
        long locationSouthId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, southPoleLocation);

        assertTrue("Unable to insert South into location table",
                locationSouthId != -1);
        Log.d(SunshineApplication.TAG, "Inserted south pole values into location table.");
        */

        Uri southPoleLocationUri = mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, southPoleLocation);
        long locationSouthId = ContentUris.parseId(southPoleLocationUri);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. South pole values into location table.");

        //==

        ContentValues testWeatherValuesNorth1 = TestUtils.createTestWeatherValues(locationNorthId, TestUtils.TEST_DATE);
        /*
        long weatherNorth1 = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValuesNorth1);

        assertTrue("Unable to insert North 1 into weather table",
                weatherNorth1 != -1);
        Log.d(SunshineApplication.TAG, "Inserted North 1 into weather table.");
        */
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, testWeatherValuesNorth1);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. North 1 into weather table");

        ContentValues testWeatherValuesNorth2 = TestUtils.createTestWeatherValues(locationNorthId, TestUtils.TEST_DATE + 1);
        /*
        long weatherNorth2 = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValuesNorth2);

        assertTrue("Unable to insert North 2 into weather table",
                weatherNorth2 != -1);
        Log.d(SunshineApplication.TAG, "Inserted North 2 into weather table.");
        */
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, testWeatherValuesNorth2);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. North 2 into weather table");

        ContentValues testWeatherValuesNorth3 = TestUtils.createTestWeatherValues(locationNorthId, TestUtils.TEST_DATE + 2);
        /*
        long weatherNorth3 = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValuesNorth3);

        assertTrue("Unable to insert North 3 into weather table",
                weatherNorth3 != -1);
        Log.d(SunshineApplication.TAG, "Inserted North 3 into weather table.");
        */
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, testWeatherValuesNorth3);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. North 3 into weather table");

        ContentValues testWeatherValuesSouth1 = TestUtils.createTestWeatherValues(locationSouthId, TestUtils.TEST_DATE);
        /*
        long weatherSouth1 = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValuesSouth1);

        assertTrue("Unable to insert South 1 into weather table",
                weatherSouth1 != -1);
        Log.d(SunshineApplication.TAG, "Inserted South 1 into weather table.");
        */
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, testWeatherValuesSouth1);
        Log.d(SunshineApplication.TAG, "Inserted by ContentProvider success. South 1 into weather table");

        db.close();
        //###

        //- retrieve by location_setting
        String city = northPoleLocation.get(WeatherContract.CityEntry.COLUMN_CITY_NAME).toString();
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherUri(city),
                null,
                null,
                null,
                null
        );

        assertTrue("Unable to retrieve from weather table (location_setting)", weatherCursor.moveToFirst());
        Log.d(SunshineApplication.TAG, "Successfully retrieved from weather table (location_setting)");

        assertTrue("(location_setting) should return for 3 rows instead of " + weatherCursor.getCount(),
                weatherCursor.getCount() == 3);
        Log.d(SunshineApplication.TAG, "(location_setting) return for " + weatherCursor.getCount());

        TestUtils.validateCurrentCursorRow("Retrieved North 1 by content resolver is not correct",
                weatherCursor,
                testWeatherValuesNorth1);
        Log.d(SunshineApplication.TAG, "Retrieved North 1 by using content resolver are correct");


        //- retrieve by date
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherUriWithDate(TestUtils.TEST_DATE),
                null,
                null,
                null,
                null
        );

        assertTrue("Unable to retrieve from weather table (retrieve by date)", weatherCursor.moveToFirst());
        Log.d(SunshineApplication.TAG, "Successfully retrieved from weather table (retrieve by date)");

        assertTrue("(retrieve by date) should return for 2 rows instead of " + weatherCursor.getCount(),
                weatherCursor.getCount() == 2);
        Log.d(SunshineApplication.TAG, "(retrieve by date) return for " + weatherCursor.getCount());

        TestUtils.validateCurrentCursorRow("Retrieved North 1 by content resolver is not correct",
                weatherCursor,
                testWeatherValuesNorth1);
        Log.d(SunshineApplication.TAG, "Retrieved North 1 by using content resolver are correct");

        //- weather list by location_setting + date
        city = southPoleLocation.get(WeatherContract.CityEntry.COLUMN_CITY_NAME).toString();
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherUri(city, TestUtils.TEST_DATE),
                null,
                null,
                null,
                null
        );

        assertTrue("Unable to retrieve from weather table (location_setting + date)", weatherCursor.moveToFirst());
        Log.d(SunshineApplication.TAG, "Successfully retrieved from weather table (location_setting + date)");

        assertTrue("(location_setting + date) should return for 1 rows instead of " + weatherCursor.getCount(),
                weatherCursor.getCount() == 1);
        Log.d(SunshineApplication.TAG, "(location_setting + date) return for " + weatherCursor.getCount());

        TestUtils.validateCurrentCursorRow("Retrieved South 1 by content resolver is not correct",
                weatherCursor,
                testWeatherValuesSouth1);
        Log.d(SunshineApplication.TAG, "Retrieved South 1 by using content resolver are correct");

        //- weather list by location_setting AND start_date
        city = northPoleLocation.get(WeatherContract.CityEntry.COLUMN_CITY_NAME).toString();
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, TestUtils.TEST_DATE + 1),
                null,
                null,
                null,
                null
        );

        assertTrue("Unable to retrieve from weather table (location_setting AND start_date)", weatherCursor.moveToFirst());
        Log.d(SunshineApplication.TAG, "Successfully retrieved from weather table (location_setting AND start_date)");

        assertTrue("(location_setting) should return for 2 rows instead of " + weatherCursor.getCount(),
                weatherCursor.getCount() == 2);
        Log.d(SunshineApplication.TAG, "(location_setting) return for " + weatherCursor.getCount());

        TestUtils.validateCurrentCursorRow("Retrieved North 2 by content resolver is not correct",
                weatherCursor,
                testWeatherValuesNorth2);
        Log.d(SunshineApplication.TAG, "Retrieved North 2 by using content resolver are correct");

        weatherCursor.close();
    }

    public void testInsertWithObserver() {
        ContentValues northPoleLocation = TestUtils.createNorthPoleLocationValues();
        String northLS = northPoleLocation.get(WeatherContract.CityEntry.COLUMN_CITY_NAME).toString();

        ContentValues southPoleLocation = TestUtils.createSouthPoleLocationValues();
        String southLS = southPoleLocation.get(WeatherContract.CityEntry.COLUMN_CITY_NAME).toString();


        TestUtils.TestContentObserver testObserverLocation = TestUtils.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(WeatherContract.CityEntry.CONTENT_URI, true, testObserverLocation);

        Uri northPoleUri = mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, northPoleLocation);

        testObserverLocation.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testObserverLocation);

        long northPoleRowId = ContentUris.parseId(northPoleUri);
        assertTrue("Inserted north pole row id shouldn't be -1",
                northPoleRowId != -1);

        Cursor locationCursor = mContext.getContentResolver().query(
                WeatherContract.CityEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Return empty cursor for location",
                locationCursor.moveToFirst());

        TestUtils.validateCurrentCursorRow("Error validating north pole location entry",
                locationCursor, northPoleLocation);

        ContentValues northOne = TestUtils.createTestWeatherValues(northPoleRowId);
        TestUtils.TestContentObserver testObserverWeather = TestUtils.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(WeatherContract.WeatherEntry.CONTENT_URI, true, testObserverWeather);

        Uri testWeatherUri = mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, northOne);

        testObserverWeather.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testObserverWeather);

        long testWeatherRowId = ContentUris.parseId(testWeatherUri);
        assertTrue("Inserted test weather row id shouldn't be -1",
                testWeatherRowId != -1);

        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Return empty cursor for weather",
                weatherCursor.moveToFirst());

        TestUtils.validateCurrentCursorRow("Error validating test weather entry",
                weatherCursor, northOne);

        Uri southPoleUri = mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, southPoleLocation);
        long southPoleRowId = ContentUris.parseId(southPoleUri);

        ContentValues southOne = TestUtils.createTestWeatherValues(southPoleRowId);
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, southOne);

        ContentValues northTwo = TestUtils.createTestWeatherValues(northPoleRowId, TestUtils.TEST_DATE + 1);
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, northTwo);

        ContentValues northThree = TestUtils.createTestWeatherValues(northPoleRowId, TestUtils.TEST_DATE + 2);
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, northThree);

        ContentValues southTwo = TestUtils.createTestWeatherValues(southPoleRowId, TestUtils.TEST_DATE + 1);
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, southTwo);


        Cursor cAllSouth = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUri(southLS),
                null, null, null, null);

        assertEquals("Weathers for south pole should return 2 row.",
                cAllSouth.getCount(), 2);


        Cursor cAllNorth = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUri(northLS),
                null, null, null, WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");

        assertEquals("Weathers for north pole should return 3 row.",
                cAllNorth.getCount(), 3);

        cAllNorth.moveToFirst();

        ContentValues northOneWithLocation = new ContentValues();
        northOneWithLocation.putAll(northOne);
        northOneWithLocation.putAll(northPoleLocation);

        TestUtils.validateCurrentCursorRow("Failing because of INNER JOIN Query ?",
                cAllNorth,
                northOneWithLocation);

        Cursor cDayOne = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUriWithDate(TestUtils.TEST_DATE),
                null, null, null, null);

        assertEquals("Weathers for day one should return 2 row.",
                cDayOne.getCount(), 2);

        Cursor cDayThree = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUriWithDate(TestUtils.TEST_DATE + 2),
                null, null, null, null);

        assertEquals("Weathers for day three should return 1 row.",
                cDayThree.getCount(), 1);

        Cursor cNorthDayOne = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUri(northLS, TestUtils.TEST_DATE),
                null, null, null, null);

        assertEquals("Weathers for north day one should return 1 row.",
                cNorthDayOne.getCount(), 1);

        Cursor cNorthFromDayTwo = mContext.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(northLS, TestUtils.TEST_DATE + 1),
                null, null, null, null);

        assertEquals("Weathers for north from day two should return 2 row.",
                cNorthFromDayTwo.getCount(), 2);
    }

    public void testLocationUpdate() {
        ContentValues northPoleLocation = TestUtils.createNorthPoleLocationValues();
        Uri northPoleUri = mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, northPoleLocation);
        long northPoleRowId = ContentUris.parseId(northPoleUri);

        Cursor locationCursor = mContext.getContentResolver().query(WeatherContract.CityEntry.CONTENT_URI,
                null, null, null, null);

        TestUtils.TestContentObserver testLocationObserver = TestUtils.TestContentObserver.getTestContentObserver();
        locationCursor.registerContentObserver(testLocationObserver);

        ContentValues updatedNorthPoleLocation = new ContentValues(northPoleLocation);
        updatedNorthPoleLocation.put(WeatherContract.CityEntry.COLUMN_CITY_NAME, "Santa's Village");

        int updatedRowCount = mContext.getContentResolver().update(WeatherContract.CityEntry.CONTENT_URI, updatedNorthPoleLocation,
                WeatherContract.CityEntry._ID + "=?", new String[]{Long.toString(northPoleRowId)});

        //check if update happens.
        assertTrue("Update for north pole location fails because updated row count is " + updatedRowCount,
                updatedRowCount == 1);

        //check if observer is being notified.
        testLocationObserver.waitForNotificationOrFail();
        locationCursor.unregisterContentObserver(testLocationObserver);
        locationCursor.close();

        Cursor updatedLocationCursor = mContext.getContentResolver().query(WeatherContract.CityEntry.CONTENT_URI,
                null,
                WeatherContract.CityEntry._ID + " = ?",
                new String[]{Long.toString(northPoleRowId)},
                null);

        updatedLocationCursor.moveToFirst();

        //check if update values are reflecting in updated cursor.
        TestUtils.validateCurrentCursorRow("Update north pole location values are not matching with updated cursor",
                updatedLocationCursor, updatedNorthPoleLocation);

        updatedLocationCursor.close();
    }

    public void testLocationDelete() {
        ContentValues northPoleLocation = TestUtils.createNorthPoleLocationValues();
        Uri northPoleUri = mContext.getContentResolver().insert(WeatherContract.CityEntry.CONTENT_URI, northPoleLocation);
        long northPoleRowId = ContentUris.parseId(northPoleUri);

        /*
        ContentValues northOne = TestUtils.createTestWeatherValues(northPoleRowId);
        mContext.getContentResolver().insert(WeatherContract.WeatherEntry.CONTENT_URI, northOne);
        */

        int deletedRowCount = mContext.getContentResolver().delete(WeatherContract.CityEntry.CONTENT_URI,
                WeatherContract.CityEntry._ID + " = ? ",
                new String[]{Long.toString(northPoleRowId)});

        //check if delete happens.
        assertTrue("Delete for north pole location fails because deleted row count is"+deletedRowCount,
                deletedRowCount == 1);

        Cursor locationCursor = mContext.getContentResolver().query(WeatherContract.CityEntry.CONTENT_URI,
                null,
                WeatherContract.CityEntry._ID + " = ?",
                new String[]{Long.toString(northPoleRowId)},
                null);

        //check if record still exists with northPoleRowId.
        assertFalse("Even after delete, location with north pole row id still exists.",
                locationCursor.moveToFirst());
    }
}
