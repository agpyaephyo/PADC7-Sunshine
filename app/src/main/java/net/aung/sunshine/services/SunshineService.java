package net.aung.sunshine.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.aung.sunshine.SunshineApplication;

/**
 * Created by aung on 2/16/16.
 */
public class SunshineService extends IntentService {

    public static Intent newIntent(Context context) {
        Intent intentToService = new Intent(context, SunshineService.class);
        return intentToService;
    }

    public SunshineService() {
        super(SunshineService.class.getSimpleName()); //better to put the name of the "worker thread".
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(SunshineApplication.TAG, "Handled Sunshine service.");
    }
}
