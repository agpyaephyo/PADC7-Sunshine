package net.aung.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.persistence.WeatherDBHelper;
import net.aung.sunshine.utils.TestUtils;

import java.util.HashSet;

/**
 * Created by aung on 2/9/16.
 */
public class TestDB extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test DB basic operations.
     * - is operational ?
     * - all the tables are created properly ?
     * - if columns in location table are correct ?
     * - if columns in weather table are correct ?
     *
     * @throws Throwable
     */
    public void testDatabase() throws Throwable {
        Log.d(SunshineApplication.TAG, "#### Start testCreateDB");
        //Create DB.
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase(); //the db should be "opened" by default.
        Log.d(SunshineApplication.TAG, "Database is created");

        //TODO Is db operational ?
        assertTrue("Database can't be opened", db.isOpen());
        Log.d(SunshineApplication.TAG, "Database is operational");

        //Create HashSet for table name we would like to create.
        final HashSet<String> tableNames = new HashSet<>();
        tableNames.add(WeatherContract.CityEntry.TABLE_NAME);
        tableNames.add(WeatherContract.WeatherEntry.TABLE_NAME);

        //TODO Are tables created correctly ?
        //Get table names from created db.
        Cursor cursorTables = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        //** Check if cursor is operational. Maybe because db hasn't been created correctly.
        assertTrue("Table cursor can't move to first", cursorTables.moveToFirst());
        Log.d(SunshineApplication.TAG, "Table cursor is operational");

        do {
            tableNames.remove(cursorTables.getString(0)); //table name is being stored in column index 0.
        } while (cursorTables.moveToNext());

        //** Check if all the desired tables are being created.
        if (!tableNames.isEmpty()) {
            StringBuilder notCreatedTables = new StringBuilder();
            for (String tableName : tableNames) {
                notCreatedTables.append(tableName + " - ");
            }
            fail("One of the db tables is not being created. " + notCreatedTables.toString());
        } else {
            Log.d(SunshineApplication.TAG, "All the tables are being created properly.");
        }

        locationColumnsTest(db);
        weatherColumnsTest(db);

        db.close();
    }

    /**
     * - crete north pole values
     * - insert north pole values
     * - query the location table back and verify with north pole values
     * @throws Throwable
     */
    public void testLocationTable() throws Throwable {
        Log.d(SunshineApplication.TAG, "#### Start testLocationTable");
        //Create DB.
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase(); //the db should be "opened" by default.
        Log.d(SunshineApplication.TAG, "Database is created");

        //Create north pole location value.
        ContentValues northPoleLocationValues = TestUtils.createNorthPoleLocationValues();

        //Insert north pole location value into location table.
        long locationRowId = db.insert(WeatherContract.CityEntry.TABLE_NAME, null, northPoleLocationValues);

        assertTrue("Fail to insert north pole location value", locationRowId != -1);
        Log.d(SunshineApplication.TAG, "Success to insert north pole location values");

        //Query north pole location value.
        Cursor cursorLocationAll = db.query(WeatherContract.CityEntry.TABLE_NAME,
                null, //the columns you want. all.
                null, //columns for the "where" clause.
                null, //values for the "where" clause.
                null, //columns to group by.
                null, //columns to filter by row groups.
                null); //sort order.

        assertTrue("location value cursor can't move to first", cursorLocationAll.moveToFirst());
        Log.d(SunshineApplication.TAG, "Location value cursor is operational");

        TestUtils.validateCurrentCursorRow("Test query location table failed.", cursorLocationAll, northPoleLocationValues);
        Log.d(SunshineApplication.TAG, "Query to location table return north pole values");

        assertFalse("there are more than one north pole location value", cursorLocationAll.moveToNext()); //moreToNext return false when there is no more record.
        Log.d(SunshineApplication.TAG, "There is only one record for north pole query");

        cursorLocationAll.close();
        db.close();
    }

    /**
     * - create & insert north pole location values
     * - create & insert test weather values
     * - query the weather table and verify the returned values
     * @throws Throwable
     */
    public void testWeatherTable() throws Throwable {
        Log.d(SunshineApplication.TAG, "#### Start testWeatherTable");
        //Create DB.
        SQLiteDatabase db = new WeatherDBHelper(mContext).getWritableDatabase(); //the db should be "opened" by default.
        Log.d(SunshineApplication.TAG, "Database is created");

        //Create north pole location value.
        ContentValues northPoleLocationValues = TestUtils.createNorthPoleLocationValues();

        //Insert north pole location value into location table.
        long northPoleLocationRowId = db.insert(WeatherContract.CityEntry.TABLE_NAME, null, northPoleLocationValues);

        assertTrue("Fail to insert north pole location value", northPoleLocationRowId != -1);
        Log.d(SunshineApplication.TAG, "Success to insert north pole location values");

        ContentValues testWeatherValues = TestUtils.createTestWeatherValues(northPoleLocationRowId, TestUtils.TEST_DATE);

        //Insert test weather values into weather table.
        long testWeatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testWeatherValues);

        assertTrue("Fail to insert test weather values", testWeatherRowId != -1);
        Log.d(SunshineApplication.TAG, "Success to insert test weather values");

        //Query weather values.
        Cursor cursorWeatherAll = db.query(WeatherContract.WeatherEntry.TABLE_NAME,
                null, //the columns you want. all.
                null, //columns for the "where" clause.
                null, //values for the "where" clause.
                null, //columns to group by.
                null, //columns to filter by row groups.
                null); //sort order.

        assertTrue("weather value cursor can't move to first", cursorWeatherAll.moveToFirst());
        Log.d(SunshineApplication.TAG, "Weather value cursor is operational");

        TestUtils.validateCurrentCursorRow("Test query weather table failed.", cursorWeatherAll, testWeatherValues);
        Log.d(SunshineApplication.TAG, "Query to weather table return expected values");

        assertFalse("there are more than one test weather value", cursorWeatherAll.moveToNext()); //moreToNext return false when there is no more record.
        Log.d(SunshineApplication.TAG, "There is only one record for test weather query");

        /*
        int affectedRows = db.delete(WeatherContract.LocationEntry.TABLE_NAME, WeatherContract.LocationEntry._ID + "=" + northPoleLocationRowId, null);
        Log.d(SunshineApplication.TAG, "deleted row : "+affectedRows);

        //Query north pole location value.
        Cursor cursorLocationAll = db.query(WeatherContract.LocationEntry.TABLE_NAME,
                null, //the columns you want. all.
                null, //columns for the "where" clause.
                null, //values for the "where" clause.
                null, //columns to group by.
                null, //columns to filter by row groups.
                null); //sort order.

        assertTrue("location value cursor can't move to first", cursorLocationAll.moveToFirst());
        Log.d(SunshineApplication.TAG, "Location value cursor is operational");

        assertTrue("North pole location row should not get deleted from location table while there is record referring to that location row in weather table.", affectedRows < 1);
        Log.d(SunshineApplication.TAG, "North pole location is not being deleted while there is weather record referring to that location row.");
        */

        cursorWeatherAll.close();
        db.close();
    }

    /**
     * Test if columns in Location table are correct.
     */
    private void locationColumnsTest(SQLiteDatabase db) {
        Cursor cursorLocationColumns = db.rawQuery("PRAGMA table_info(" + WeatherContract.CityEntry.TABLE_NAME + ")", null);

        //** Check if cursor is operational.
        assertTrue("Location column cursor can't move to first", cursorLocationColumns.moveToFirst());
        Log.d(SunshineApplication.TAG, "Location column cursor is operational");

        //Create HashSet for column names in Location Table.
        final HashSet<String> locationColumns = new HashSet<String>();
        locationColumns.add(WeatherContract.CityEntry._ID);
        locationColumns.add(WeatherContract.CityEntry.COLUMN_CITY_NAME);
        locationColumns.add(WeatherContract.CityEntry.COLUMN_COORD_LAT);
        locationColumns.add(WeatherContract.CityEntry.COLUMN_COORD_LNG);

        int indexForColumnName = cursorLocationColumns.getColumnIndex("name");
        do {
            String columnName = cursorLocationColumns.getString(indexForColumnName);
            locationColumns.remove(columnName);
        } while (cursorLocationColumns.moveToNext());

        //** Check if all the columns in Location table are being created.
        if (!locationColumns.isEmpty()) {
            StringBuilder notCreatedColumns = new StringBuilder();
            for (String locationColumn : locationColumns) {
                notCreatedColumns.append(locationColumn + " - ");
            }
            fail("One of the columns in location table is not being created. " + notCreatedColumns.toString());
        } else {
            Log.d(SunshineApplication.TAG, "All the columns in Location table are being created properly.");
        }
    }

    /**
     * Test if columns in Weather table are correct.
     */
    private void weatherColumnsTest(SQLiteDatabase db) {
        Cursor cursorWeatherColumns = db.rawQuery("PRAGMA table_info(" + WeatherContract.WeatherEntry.TABLE_NAME + ")", null);

        //** Check if cursor is operation.
        assertTrue("Weather column cursor can't move to first", cursorWeatherColumns.moveToFirst());
        Log.d(SunshineApplication.TAG, "Weather column cursor is operational");

        final HashSet<String> weatherColumns = new HashSet<>();
        weatherColumns.add(WeatherContract.WeatherEntry._ID);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_MIN_TEMPERATURE);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_MAX_TEMPERATURE);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_DATE);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_PRESSURE);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_WEATHER_CONDITION_ID);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC);
        weatherColumns.add(WeatherContract.WeatherEntry.COLUMN_LOCATION_ID);

        int indexForColumnName = cursorWeatherColumns.getColumnIndex("name");
        do {
            String columnName = cursorWeatherColumns.getString(indexForColumnName);
            weatherColumns.remove(columnName);
        } while (cursorWeatherColumns.moveToNext());

        //** Check if all the columns in Weather table are being created.
        if (!weatherColumns.isEmpty()) {
            StringBuilder notCreatedColumns = new StringBuilder();
            for (String weatherColumn : weatherColumns) {
                notCreatedColumns.append(weatherColumn + " - ");
            }

            fail("One of the columns in weather table is not being created. " + notCreatedColumns.toString());
        } else {
            Log.d(SunshineApplication.TAG, "All the columns in Weather table are being created properly.");
        }
    }
}
