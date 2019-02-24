package net.aung.sunshine.receivers.internal;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.services.SunshineService;

/**
 * Created by aung on 2/16/16.
 */
public class DataSyncAlarmReceiver extends BroadcastReceiver {

    public static PendingIntent newPendingIntent(Context context) {
        Intent intentToAlarmReceiver = new Intent(context, DataSyncAlarmReceiver.class);
        PendingIntent pendingIntentToAlarmReceiver = PendingIntent.getBroadcast(context, 0, intentToAlarmReceiver, PendingIntent.FLAG_ONE_SHOT);

        return pendingIntentToAlarmReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(SunshineApplication.TAG, "Received Alarm.");
        Intent intentToService = SunshineService.newIntent(context);
        context.startService(intentToService);
    }
}
