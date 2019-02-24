package net.aung.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import net.aung.sunshine.SunshineApplication;

/**
 * This class is used to deliver SyncAdapter Binder to the SyncManager.
 * Created by aung on 2/17/16.
 */
public class SunshineSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SunshineSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(SunshineApplication.TAG, "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SunshineSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
