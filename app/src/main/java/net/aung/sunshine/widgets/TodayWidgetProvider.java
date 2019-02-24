package net.aung.sunshine.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.aung.sunshine.services.TodayWidgetIntentService;
import net.aung.sunshine.sync.SunshineSyncAdapter;

/**
 * Created by aung on 3/3/16.
 */
public class TodayWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(SunshineSyncAdapter.ACTION_DATA_UPDATED)) {
            context.startService(TodayWidgetIntentService.newIntent(context));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(TodayWidgetIntentService.newIntent(context));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(TodayWidgetIntentService.newIntent(context));
    }
}
