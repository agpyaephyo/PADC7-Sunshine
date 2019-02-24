package net.aung.sunshine.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;

import net.aung.sunshine.R;
import net.aung.sunshine.sync.SunshineSyncAdapter;
import net.aung.sunshine.utils.DateFormatUtils;
import net.aung.sunshine.utils.GcmUtils;
import net.aung.sunshine.utils.NotificationUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.WeatherDataUtils;

import java.util.Locale;

/**
 * Created by aung on 12/10/15.
 */
public class BaseActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String IE_WEATHER_STATUS_DATE_TIME = "IE_WEATHER_STATUS_DATE_TIME";
    protected static final String IE_WEATHER_STATUS_POSITION = "IE_WEATHER_STATUS_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        GcmUtils.setupGCM(this); //TODO think of something to compensate the SERVICE_NOT_AVAILABLE back-offs.
        forceUpdateLocale();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_language_key))) {
            forceUpdateLocale();
            WeatherDataUtils.loadWeatherDescMap();
            DateFormatUtils.loadDateFormat(SettingsUtils.getLocale());

            SunshineSyncAdapter.syncImmediately(this);
        }
    }


    private void forceUpdateLocale() {
        if (SettingsUtils.retrieveLanguagePref() == SettingsUtils.LANG_MM) {
            Resources res = getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale("my");
            res.updateConfiguration(conf, dm);
        } else {
            Resources res = getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale("en");
            res.updateConfiguration(conf, dm);
        }
    }

    protected void showCityInGoogleMap(String city, View viewForErrorSnackBar) {
        //String mapUrl = "http://maps.google.co.in/maps?q=" + city;

        Uri mapUri = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", city)
                .build();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, mapUri);
        //intent.setPackage("com.google.android.apps.maps"); //only open with Google Map App
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(viewForErrorSnackBar, "You have no app being installed to show your selected city on the Map. Please install app like Google Map.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
