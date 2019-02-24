package net.aung.sunshine.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by aung on 2/12/16.
 */
public class SunshineConstants {

    public static final long TODAY = new Date().getTime() / 1000 - (24 * 60 * 60); //to make sure we are showing for today also.

    public static final int FORECAST_LIST_LOADER = 0;
    public static final int FORECAST_DETAIL_LOADER = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_SERVER_OK, STATUS_SERVER_DOWN, STATUS_SERVER_INVALID, STATUS_SERVER_UNKNOWN, STATUS_SERVER_CITY_NOT_FOUND})
    public @interface ServerStatus {}

    public static final int STATUS_SERVER_OK = 0;
    public static final int STATUS_SERVER_DOWN = 1;
    public static final int STATUS_SERVER_INVALID = 2;
    public static final int STATUS_SERVER_UNKNOWN = 3;
    public static final int STATUS_SERVER_CITY_NOT_FOUND = 4;

    public static final String UNKNOWN_CITY = "Unknown City";

    public static final String SHARED_PREF_GCM = "SHARED_PREF_GCM";
    public static final String SP_KEY_GCM_ID = "SP_KEY_GCM_ID";
}
