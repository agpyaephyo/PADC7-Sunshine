package net.aung.sunshine.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.utils.NotificationUtils;

/**
 * Created by aung on 2/26/16.
 */
public class SunshineGcmListenerService extends GcmListenerService {

    private static final String EXTRA_WEATHER = "weather";
    private static final String EXTRA_LOCATION = "location";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(SunshineApplication.TAG, "From : " + from);
        Log.d(SunshineApplication.TAG, "Message : " + message);

        Context context = SunshineApplication.getContext();
        String senderId = context.getString(R.string.gcm_default_sender_id);
        if (senderId.equals(from)) {
            String weather = data.getString(EXTRA_WEATHER);
            String location = data.getString(EXTRA_LOCATION);
            String alertMsg = "Heads up: " + weather + " in " + location + "!";
            NotificationUtils.showAlertNotification(alertMsg);
        }
    }
}
