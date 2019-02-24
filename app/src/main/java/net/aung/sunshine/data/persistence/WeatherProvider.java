package net.aung.sunshine.data.persistence;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by aung on 2/10/16.
 */
public class WeatherProvider extends ContentProvider {

    public static final int WEATHER = 100;
    public static final int WEATHER_WITH_CITY = 101;
    public static final int WEATHER_WITH_CITY_AND_DATE = 102;

    public static final int LOCATION = 300;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final SQLiteQueryBuilder sWeatherLocationInnerJoin;

    static {
        sWeatherLocationInnerJoin = new SQLiteQueryBuilder();
        //weather INNER JON location ON weather.location_id = location._id
        sWeatherLocationInnerJoin.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.CityEntry.TABLE_NAME + " ON " +
                        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_LOCATION_ID + " = " +
                        WeatherContract.CityEntry.TABLE_NAME + "." + WeatherContract.CityEntry._ID);
    }

    private WeatherDBHelper mWeatherDBHelper;

    //location.location_setting = ?
    private static final String sCityNameSelection =
            WeatherContract.CityEntry.TABLE_NAME +
                    "." + WeatherContract.CityEntry.COLUMN_CITY_NAME + " = ?";

    //location.location_setting = ? AND date >= ?
    private static final String sCityNameWithStartDateSelection =
            WeatherContract.CityEntry.TABLE_NAME +
                    "." + WeatherContract.CityEntry.COLUMN_CITY_NAME + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " >= ?";

    //location.location_setting = ? AND date = ?
    private static final String sCityNameAndDateSelection =
            WeatherContract.CityEntry.TABLE_NAME +
                    "." + WeatherContract.CityEntry.COLUMN_CITY_NAME + " = ? AND " +
                    WeatherContract.WeatherEntry.COLUMN_DATE + " = ?";

    //date = ?
    private static final String sDateSelection =
            WeatherContract.WeatherEntry.COLUMN_DATE + " = ?";

    public static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //* is for String. # is for matching Number.
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, WEATHER); //net.aung.sunshine/weather -> 100
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_CITY); //net.aung.sunshine/weather/* -> 101
        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_CITY_AND_DATE); //net.aung.sunshine/weather/*/# -> 102

        //uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/date/#", WEATHER_WITH_DATE); //net.aung.sunshine/weather/date/# -> 103
        //uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER + "/*?"+ WeatherContract.WeatherEntry.COLUMN_DATE+"=#", WEATHER_WITH_LOCATION_AND_START_DATE); //net.aung.sunshine/weather/94379?startDate=12345678 -> 104

        uriMatcher.addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_CITY, LOCATION); //net.aung.sunshine/location -> 300

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mWeatherDBHelper = new WeatherDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor queryCursor = null;
        int matchUri = sUriMatcher.match(uri);
        switch (matchUri) {
            // "weather"
            case WEATHER: //also for date
                long dateParam = WeatherContract.WeatherEntry.getDateParamFromUri(uri);
                if (dateParam > 0) {
                    selection = sDateSelection;
                    selectionArgs = new String[]{Long.toString(dateParam)};
                }

                queryCursor = mWeatherDBHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //group_by
                        null, //having
                        sortOrder
                );
                break;
            case LOCATION:
                queryCursor = mWeatherDBHelper.getReadableDatabase().query(
                        WeatherContract.CityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //group_by
                        null, //having
                        sortOrder
                );
                break;
            case WEATHER_WITH_CITY: //also for location + startDate
                //ignore current "selection" & "selectionArgs" from params ?

                //-get "location_setting" from uri.
                String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);

                //-get "start_date" from uri.
                long startDate = WeatherContract.WeatherEntry.getDateParamFromUri(uri);

                if (startDate > 0) {
                    //both location_setting & start_date
                    selection = sCityNameWithStartDateSelection;
                    selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
                } else {
                    //only location_setting
                    selection = sCityNameSelection;
                    selectionArgs = new String[]{locationSetting};
                }

                //need special query builder for INNER_JOIN between two tables.
                queryCursor = sWeatherLocationInnerJoin.query(mWeatherDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            case WEATHER_WITH_CITY_AND_DATE:

                String location = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
                long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

                selection = sCityNameAndDateSelection;
                selectionArgs = new String[]{location, Long.toString(date)};

                queryCursor = sWeatherLocationInnerJoin.query(mWeatherDBHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        queryCursor.setNotificationUri(getContext().getContentResolver(), uri); //**
        return queryCursor;
    }

    /**
     * Return type of the Uri. If the uri is to show an image, return MIME type such as "image/jpeg".
     * Whether to return cursor with single row [Type Item] or cursor with multiple rows [Type Directory].
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {

        final int matchUri = sUriMatcher.match(uri);

        switch (matchUri) {
            case WEATHER:
                return WeatherContract.WeatherEntry.DIR_TYPE;
            case WEATHER_WITH_CITY:
                return WeatherContract.WeatherEntry.DIR_TYPE;
            case WEATHER_WITH_CITY_AND_DATE:
                return WeatherContract.WeatherEntry.ITEM_TYPE;
            case LOCATION:
                return WeatherContract.CityEntry.DIR_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //the URI to insert is only for base URIs (weather, location).
        final SQLiteDatabase db = mWeatherDBHelper.getWritableDatabase();
        final int matchUri = sUriMatcher.match(uri);
        Uri insertedUri;

        switch (matchUri) {
            case WEATHER: {
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    insertedUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case LOCATION: {
                long _id = db.insert(WeatherContract.CityEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    insertedUri = WeatherContract.CityEntry.buildCityUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null); //notify any registered observers.
        db.close();
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mWeatherDBHelper.getWritableDatabase();
        final int matchUri = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (matchUri) {
            case WEATHER: {
                rowsDeleted = db.delete(WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                rowsDeleted = db.delete(WeatherContract.CityEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mWeatherDBHelper.getWritableDatabase();
        final int matchUri = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (matchUri) {
            case WEATHER: {
                rowsUpdated = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LOCATION: {
                rowsUpdated = db.update(WeatherContract.CityEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mWeatherDBHelper.getWritableDatabase();
        final int matchUri = sUriMatcher.match(uri);
        switch (matchUri) {
            case WEATHER: {
                int insertedCount = 0;
                try {
                    db.beginTransaction();
                    for (ContentValues contentValues : values) {
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1) {
                            insertedCount++;
                        }
                    }
                    db.setTransactionSuccessful(); //without this, records won't be committed into db.
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return insertedCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
