package net.aung.sunshine.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.services.SunshineGCMRegistrationService;

/**
 * Created by aung on 2/26/16.
 */
public class GcmUtils {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static boolean isGcmRegistered() {
        Context context = SunshineApplication.getContext();
        final SharedPreferences prefs = context.getSharedPreferences(SunshineConstants.SHARED_PREF_GCM, Context.MODE_PRIVATE);
        return prefs.getString(SunshineConstants.SP_KEY_GCM_ID, null) != null;
    }

    private static boolean checkGooglePlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(SunshineApplication.TAG, "This device is not supported by Google Play Services.");
            }
            return false;
        }
        return true;
    }

    public static void setupGCM(Activity activity) {
        if (!isGcmRegistered()) {
            if (checkGooglePlayServices(activity)) {
                Context context = SunshineApplication.getContext();
                Intent intentToGcmRegistration = new Intent(context, SunshineGCMRegistrationService.class);
                context.startService(intentToGcmRegistration);
            } else {
                //TODO Communicate this if it is the first time.
                Log.d(SunshineApplication.TAG, "No valid Google Play Services APK. Weather alerts will be disabled.");
            }
        }
    }

    public static void storeRegistrationId(String regId) {
        Context context = SunshineApplication.getContext();
        final SharedPreferences prefs = context.getSharedPreferences(SunshineConstants.SHARED_PREF_GCM, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SunshineConstants.SP_KEY_GCM_ID, regId);
        editor.apply();
    }
}
