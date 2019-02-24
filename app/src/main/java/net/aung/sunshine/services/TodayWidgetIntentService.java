package net.aung.sunshine.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.activities.ForecastActivity;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.utils.ImageUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;
import net.aung.sunshine.utils.WeatherDataUtils;
import net.aung.sunshine.widgets.TodayWidgetProvider;

import java.util.concurrent.ExecutionException;

/**
 * Created by aung on 3/3/16.
 */
public class TodayWidgetIntentService extends IntentService {

    public static Intent newIntent(Context context) {
        return new Intent(context, TodayWidgetIntentService.class);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public TodayWidgetIntentService() {
        super(TodayWidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = SunshineApplication.getContext();
        String city = SettingsUtils.retrieveUserCity();
        Cursor cursorWeather = context.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY),
                null, null, null, null);

        if (cursorWeather.moveToFirst()) {
            WeatherStatusVO weatherStatus = WeatherStatusVO.parseFromCursor(cursorWeather);
            Bitmap weatherArt = null;
            try {
                weatherArt = ImageUtils.getBitmapForNotification(weatherStatus);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                    TodayWidgetProvider.class));

            for (int appWidgetId : appWidgetIds) {

                Bundle widgetOption = appWidgetManager.getAppWidgetOptions(appWidgetId);
                int minWidth = widgetOption.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                int minHeight = widgetOption.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

                RemoteViews rvs = null;
                if (minWidth <= 160 && minHeight <= 65) {
                    //2 x 1
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_two_one);
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                } else if ((minWidth <= 160) && (minHeight > 65 && minHeight < 150)) {
                    // 2 x 2
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_two_two);
                    rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                } else if (minWidth <= 160 && (minHeight >= 150 && minHeight < 230)) {
                    // 2 x 3
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_two_three);
                    rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                } else if ((minWidth > 160 && minWidth < 250) && (minHeight <= 65)) {
                    // 3 x 1
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_three_one);
                    rvs.setTextViewText(R.id.tv_status, weatherStatus.getWeather().getDescription());
                } else if ((minWidth > 160 && minWidth < 250) && (minHeight > 65 && minHeight < 150)) {
                    // 3 x 2
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_three_two);
                    rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                } else if ((minWidth > 160 && minWidth < 250) && (minHeight >= 150 && minHeight < 230)) {
                    // 3 x 3
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_three_three);
                    rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_status, weatherStatus.getWeather().getDescription());
                } else if (minWidth >= 250 && minHeight <= 65) {
                    // 4 x 1
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_four_one);
                    rvs.setTextViewText(R.id.tv_status, weatherStatus.getWeather().getDescription());
                } else if (minWidth >= 250 && (minHeight > 65 && minHeight < 150)) {
                    // 4 x 2
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_four_two);
                    rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_status, weatherStatus.getWeather().getDescription());
                } else if (minWidth >= 250 && (minHeight >= 150 && minHeight < 230)) {
                    // 4 x 3
                    rvs = new RemoteViews(context.getPackageName(), R.layout.widget_today_four_three);
                    rvs.setTextViewText(R.id.tv_date_day_combine, weatherStatus.getDateDayCombined());
                    rvs.setTextViewText(R.id.tv_min_temperature, weatherStatus.getTemperature().getMinTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_max_temperature, weatherStatus.getTemperature().getMaxTemperatureDisplay());
                    rvs.setTextViewText(R.id.tv_status, weatherStatus.getWeather().getDescription());
                }

                if (rvs != null) {
                    if (weatherArt == null) {
                        rvs.setImageViewResource(R.id.iv_status_art, WeatherDataUtils.getArtResourceForWeatherCondition(weatherStatus.getWeather().getId()));
                    } else {
                        rvs.setImageViewBitmap(R.id.iv_status_art, weatherArt);
                    }
                }

                Intent intentToWeatherForecast = new Intent(context, ForecastActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToWeatherForecast, 0);
                rvs.setOnClickPendingIntent(R.id.widget, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, rvs);
            }

        }
    }
}
