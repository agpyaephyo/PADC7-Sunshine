package net.aung.sunshine.services;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.activities.ForecastActivity;
import net.aung.sunshine.activities.ForecastDetailActivity;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.utils.ImageUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;
import net.aung.sunshine.utils.WeatherDataUtils;

import java.util.concurrent.ExecutionException;

/**
 * Created by aung on 3/4/16.
 */
public class WeatherListWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do.
            }

            @Override
            public void onDataSetChanged() {
                if (data != null)
                    data.close();

                final long identityToken = Binder.clearCallingIdentity();

                String city = SettingsUtils.retrieveUserCity();
                Uri uri = WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY);

                data = getContentResolver().query(uri,
                        null, //projections
                        null, //selection
                        null, //selectionArgs
                        WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.COLUMN_DATE + " ASC");

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews rvs = new RemoteViews(getPackageName(), R.layout.widget_weather_list_item);
                WeatherStatusVO weatherStatus = WeatherStatusVO.parseFromCursor(data);

                Bitmap weatherArt = null;
                try {
                    weatherArt = ImageUtils.getBitmapForNotification(weatherStatus);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                rvs.setTextViewText(R.id.tv_status, weatherStatus.getWeather().getDescription());
                rvs.setTextViewText(R.id.tv_day, weatherStatus.getDateDisplay());

                if (weatherArt == null) {
                    rvs.setImageViewResource(R.id.iv_status_art, WeatherDataUtils.getArtResourceForWeatherCondition(weatherStatus.getWeather().getId()));
                } else {
                    rvs.setImageViewBitmap(R.id.iv_status_art, weatherArt);
                }

                if (!getResources().getBoolean(R.bool.isTwoPane)) {
                    final Intent fillInIntent = ForecastDetailActivity.createNewIntent(SunshineApplication.getContext(), weatherStatus);
                    rvs.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                } else {
                    final Intent fillInIntent = ForecastActivity.createNewIntent(SunshineApplication.getContext(), weatherStatus, position);
                    rvs.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                }


                return rvs;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_weather_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data != null && data.moveToPosition(position)) {
                    WeatherStatusVO weatherStatus = WeatherStatusVO.parseFromCursor(data);
                    return weatherStatus.getWeather().getId();
                }
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
