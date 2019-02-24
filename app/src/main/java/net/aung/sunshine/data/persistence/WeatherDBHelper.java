package net.aung.sunshine.data.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aung on 2/8/16.
 */
public class WeatherDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3; /* manually update every time you release a new apk with updated database schema. */

    public static final String DATABASE_NAME = "weather.db"; /* actual database file on the file system */

    private static final String SQL_CREATE_CITY_TABLE = "CREATE TABLE " + WeatherContract.CityEntry.TABLE_NAME + " (" +
            WeatherContract.CityEntry._ID + " INTEGER PRIMARY KEY, "+
            WeatherContract.CityEntry.COLUMN_CITY_NAME + " TEXT UNIQUE NOT NULL, "+
            WeatherContract.CityEntry.COLUMN_COORD_LAT + " REAL NOT NULL, "+
            WeatherContract.CityEntry.COLUMN_COORD_LNG + " REAL NOT NULL, "+
            WeatherContract.CityEntry.COLUMN_COUNTRY + " TEXT NOT NULL, "+
            WeatherContract.CityEntry.COLUMN_POPULATION + " INTEGER NOT NULL "+
            " );";

    private static final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
            /* PK */
            WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+

            /* FK */
            WeatherContract.WeatherEntry.COLUMN_LOCATION_ID + " INTEGER, "+

            WeatherContract.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_WEATHER_DESC + " TEXT NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_WEATHER_CONDITION_ID + " INTEGER NOT NULL, "+

            WeatherContract.WeatherEntry.COLUMN_MIN_TEMPERATURE + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMPERATURE + " REAL NOT NULL, "+

            WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, "+
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "+

            /* make reference for FK */
            " FOREIGN KEY ("+ WeatherContract.WeatherEntry.COLUMN_LOCATION_ID+") REFERENCES " +
            WeatherContract.CityEntry.TABLE_NAME + " (" + WeatherContract.CityEntry._ID+"), "+

            /* To assure the application have just one weather entry per day per location, created UNIQUE constraint with REPLACE strategy. */
            " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATE + ", " +
            WeatherContract.WeatherEntry.COLUMN_LOCATION_ID + ") ON CONFLICT REPLACE" +
            " );";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null /*cursor_factory*/ , DATABASE_VERSION); //NULL database_error_handler.
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CITY_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This database is only a cache for online data, so its upgrade policy is simply to discard the data and start over.
        //Will only fire if you change the version number of your db.

        db.execSQL("DROP TABLE IF EXISTS "+ WeatherContract.CityEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}