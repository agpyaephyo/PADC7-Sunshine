package net.aung.sunshine.muzei;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.MuzeiArtSource;

import net.aung.sunshine.activities.ForecastActivity;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.sync.SunshineSyncAdapter;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;
import net.aung.sunshine.utils.WeatherDataUtils;

/**
 * Created by aung on 3/4/16.
 */
public class WeatherMuzeiSource extends MuzeiArtSource {

    /**
     * Remember to call this constructor from an empty constructor!
     */
    public WeatherMuzeiSource() {
        super(WeatherMuzeiSource.class.getSimpleName());
    }

    @Override
    protected void onUpdate(int reason) {
        String city = SettingsUtils.retrieveUserCity();
        Uri uri = WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY);

        Cursor data = getContentResolver().query(uri,
                null, //projections
                null, //selection
                null, //selectionArgs
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");

        if (data.moveToFirst()) {
            WeatherStatusVO weatherStatus = WeatherStatusVO.parseFromCursor(data);
            String imageUrl = WeatherDataUtils.getImageUrlForWeatherCondition(weatherStatus.getWeather().getId());
            if (imageUrl != null) {
                publishArtwork(new Artwork.Builder()
                        .imageUri(Uri.parse(imageUrl))
                        .title(weatherStatus.getWeather().getDescription())
                        .byline(city)
                        .viewIntent(new Intent(this, ForecastActivity.class))
                        .build());
            }
        }

        data.close();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        if (intent != null && intent.getAction().equals(SunshineSyncAdapter.ACTION_DATA_UPDATED)) {
            if(isEnabled()) {
                onUpdate(UPDATE_REASON_OTHER) ;
            }
        }
    }
}
