package net.aung.sunshine.receivers.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.models.WeatherStatusModel;
import net.aung.sunshine.events.DataEvent;
import net.aung.sunshine.utils.NetworkUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.SunshineConstants;

import de.greenrobot.event.EventBus;

/**
 * Created by aung on 2/8/16.
 */
public class InternetConnectivityStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isOnline(context)) {
            //Toast.makeText(context, "Internet is being connected", Toast.LENGTH_SHORT).show();
            WeatherStatusModel.getInstance().loadWeatherStatusList(true);
        } else {
            //Toast.makeText(context, "Internet is being disconnected", Toast.LENGTH_SHORT).show();
        }
    }


}
