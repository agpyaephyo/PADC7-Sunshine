package net.aung.sunshine.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bumptech.glide.Glide;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.activities.ForecastActivity;
import net.aung.sunshine.activities.ReplyActivity;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;

import java.util.concurrent.ExecutionException;

/**
 * Created by aung on 2/17/16.
 */
public class NotificationUtils {

    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final int ALERT_NOTIFICATION_ID = 3005;

    public static final String EXTRA_VOICE_REPLY = "EXTRA_VOICE_REPLY";

    public static void showWeatherNotification(WeatherStatusVO weather) {
        Context context = SunshineApplication.getContext();

        //Notification Icon
        int weatherArtResourceId = WeatherDataUtils.getArtResourceForWeatherCondition(weather.getWeather().getId());

        try {
            Bitmap weatherArtBitmap = ImageUtils.getBitmapForNotification(weather);

            //Notification Title
            String title = context.getString(R.string.app_name);

            //Notification Text
            String text = String.format(context.getString(R.string.format_notification),
                    weather.getWeather().getDescription(),
                    weather.getTemperature().getMaxTemperatureDisplay(),
                    weather.getTemperature().getMinTemperatureDisplay());

            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode("Yangon"));
            mapIntent.setData(geoUri);
            PendingIntent mapPendingIntent = PendingIntent.getActivity(context, 0, mapIntent, 0);

            NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(R.drawable.ic_map_white_24dp,
                            context.getString(R.string.lbl_map), mapPendingIntent)
                            .build();

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(context.getString(R.string.dummy_long_text));

            int bigPictureWidth = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
            int bigPictureHeight = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);

            Bitmap bigPictureStyleBitmap = Glide.with(context)
                    .load(R.drawable.drawer_background)
                    .asBitmap()
                    .into(bigPictureWidth, bigPictureHeight)
                    .get();

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(bigPictureStyleBitmap);

            String[] prePopulatedReplies = new String[] {"It's terrible", "Too hot", "Fine", "It's good actually", "IDK"};

            String replyLabel = "What do you think of today's weather ?";
            RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                    .setLabel(replyLabel)
                    .setChoices(prePopulatedReplies)
                    .build();

            //Create an intent for the reply action
            Intent replyIntent = new Intent(context, ReplyActivity.class);
            PendingIntent replyPendingIntent =
                    PendingIntent.getActivity(context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Create the reply action and add the remote input.
            NotificationCompat.Action replyAction =
                    new NotificationCompat.Action.Builder(R.drawable.ic_reply_voice_24dp, "Reply About Weather", replyPendingIntent).
                            addRemoteInput(remoteInput).
                            build();

            //Create a big text style for second page.
            NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
            secondPageStyle.setBigContentTitle("More Weather Information")
                    .bigText("Global warming. Flooding. Thunderstrom and What Nots.");

            //Create second page notification.
            Notification secondPageNotification =
                    new NotificationCompat.Builder(context)
                    .setStyle(secondPageStyle)
                    .build();

            NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                    .setBackground(bigPictureStyleBitmap)
                    .addAction(replyAction)
                    .addPage(secondPageNotification)
                    ;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setColor(context.getResources().getColor(R.color.primary))
                    .setSmallIcon(weatherArtResourceId)
                    .setLargeIcon(weatherArtBitmap)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    //.addAction(replyAction)
                    .addAction(R.drawable.ic_map_24dp, context.getString(R.string.lbl_map), mapPendingIntent)
                    //.extend(new NotificationCompat.WearableExtender().addAction(action))
                    //.setStyle(bigTextStyle)
                    //.setStyle(bigPictureStyle)
                    .extend(wearableExtender)
                    ;

            //Open the app when user tap on notification
            Intent resultIntent = new Intent(context, ForecastActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            //NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(WEATHER_NOTIFICATION_ID, builder.build());
        } catch (InterruptedException e) {
            Log.e(SunshineApplication.TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.e(SunshineApplication.TAG, e.getMessage());
        }
    }

    public static void showAlertNotification(String message) {
        Context context = SunshineApplication.getContext();

        Bitmap weatherArtBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.art_storm);

        //Notification Title
        String title = context.getString(R.string.app_name);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(context.getResources().getColor(R.color.primary))
                .setSmallIcon(R.drawable.art_clear)
                .setLargeIcon(weatherArtBitmap)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        //Open the app when user tap on notification
        Intent resultIntent = new Intent(context, ForecastActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ALERT_NOTIFICATION_ID, builder.build());
    }

    public static void hideWeatherNotification() {
        Context context = SunshineApplication.getContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(WEATHER_NOTIFICATION_ID);
    }

    public static void showUpdatedWeatherNotification() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = SunshineApplication.getContext();
                String city = SettingsUtils.retrieveUserCity();
                Cursor cursorWeather = context.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY),
                        null, null, null, null);

                if (cursorWeather.moveToFirst()) {
                    WeatherStatusVO weatherStatusDetail = WeatherStatusVO.parseFromCursor(cursorWeather);
                    NotificationUtils.showWeatherNotification(weatherStatusDetail);
                }
            }
        }).start();
    }
}
