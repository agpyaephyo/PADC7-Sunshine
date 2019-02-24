package net.aung.sunshine.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;

import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.vos.WeatherStatusVO;

import java.util.concurrent.ExecutionException;

/**
 * Created by aung on 3/3/16.
 */
public class ImageUtils {

    public static Bitmap getBitmapForNotification(WeatherStatusVO weather) throws ExecutionException, InterruptedException {
        Context context = SunshineApplication.getContext();
        int weatherArtResourceId = WeatherDataUtils.getArtResourceForWeatherCondition(weather.getWeather().getId());
        Bitmap weatherArtBitmap;
        if (SettingsUtils.retrieveIconPackPref() == SettingsUtils.ICON_PACK_UDACITY) {
            int largeIconWidth = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width); //Don't need to check for HoneyComb version because minimum API version is 16.
            int largeIconHeight = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
            String artUrl = WeatherDataUtils.getArtUrlFromWeatherCondition(weather.getWeather().getId());

            weatherArtBitmap = Glide.with(context)
                    .load(artUrl)
                    .asBitmap()
                    .placeholder(weatherArtResourceId)
                    .error(weatherArtResourceId)
                    .into(largeIconWidth, largeIconHeight)
                    .get();
        } else {
            weatherArtBitmap = BitmapFactory.decodeResource(context.getResources(), weatherArtResourceId);
        }

        return weatherArtBitmap;
    }
}
