package net.aung.sunshine;

import android.app.Application;
import android.content.Context;

/**
 * Created by aung on 8/10/16.
 */
public class SunshineWearApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
