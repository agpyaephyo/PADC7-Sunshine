package net.aung.sunshine.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.data.persistence.WeatherContract;
import net.aung.sunshine.data.vos.WeatherStatusVO;
import net.aung.sunshine.muzei.WeatherMuzeiSource;
import net.aung.sunshine.utils.ImageUtils;
import net.aung.sunshine.utils.NotificationUtils;
import net.aung.sunshine.utils.SettingsUtils;
import net.aung.sunshine.utils.AppSharedConstants;
import net.aung.sunshine.utils.SunshineConstants;
import net.aung.sunshine.utils.WeatherDataUtils;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Run on background thread.
 * Created by aung on 2/17/16.
 */
public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int SYNC_INTERVAL = 60 * 180; //3 hour interval.
    public static final int SYNC_FLEXTIME = 0;

    public static final String ACTION_DATA_UPDATED = "net.aung.sunshine.ACTION_DATA_UPDATED";

    private GoogleApiClient mGoogleApiClient;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(SunshineApplication.TAG, "Performing Sync.");
        buildGoogleApiClient();

        boolean isNotificationOn = SettingsUtils.retrieveNotificationPref();
        if (isNotificationOn) {
            notifyWeather();
        }

        Context context = SunshineApplication.getContext();
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);

        // Muzei is only compatible with Jelly Bean MR1+ devices, so there's no need to update the
        // Muzei background on lower API level devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.startService(new Intent(ACTION_DATA_UPDATED)
                    .setClass(context, WeatherMuzeiSource.class));
        }

        notifyWeather();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    private void notifyWeather() {
        Context context = getContext();
        String city = SettingsUtils.retrieveUserCity();
        Cursor cursorWeather = context.getContentResolver().query(WeatherContract.WeatherEntry.buildWeatherUriWithStartDate(city, SunshineConstants.TODAY),
                null, null, null, null);

        if (cursorWeather.moveToFirst()) {
            WeatherStatusVO weatherStatusDetail = WeatherStatusVO.parseFromCursor(cursorWeather);
            NotificationUtils.showWeatherNotification(weatherStatusDetail);
            sendWeatherInfoToWear(weatherStatusDetail);
        }

        cursorWeather.close();
    }

    private void sendWeatherInfoToWear(WeatherStatusVO weatherStatusDetail) {
        Log.d(SunshineApplication.TAG, "sendWeatherInfoToWear");
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(AppSharedConstants.DATA_PATH);
        putDataMapRequest.getDataMap().putString(AppSharedConstants.MAX_TEMPERATURE,
                weatherStatusDetail.getTemperature().getMaxTemperatureDisplay());
        putDataMapRequest.getDataMap().putString(AppSharedConstants.MIN_TEMPERATURE,
                weatherStatusDetail.getTemperature().getMinTemperatureDisplay());
        putDataMapRequest.getDataMap().putString(AppSharedConstants.LOCALE,
                SettingsUtils.getLocale().getLanguage());
        putDataMapRequest.getDataMap().putInt(AppSharedConstants.WEATHER_ID,
                weatherStatusDetail.getWeather().getId());

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.d(SunshineApplication.TAG, "Failed to send weather information to wearable.");
                        } else {
                            Log.d(SunshineApplication.TAG, "Successfully sent weather information to wearable.");
                        }
                    }
                });
    }

    /**
     * Helper method to get fake account to be used with SyncAdapter.
     *
     * @param context
     * @return a fake account
     */
    public static Account getSyncAccount(Context context) {
        //Get an instance of Android Account Manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        //Create the account type & default account
        final String ACCOUNT_TYPE = context.getString(R.string.sync_account_type);
        final String ACCOUNT_NAME = "dummy_account";

        Account newAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);

        if (!accountManager.addAccountExplicitly(newAccount, null, null)) {
            //maybe the account is already exists. return null;
        }

        return newAccount;
    }

    /**
     * to sync immediately & test our SyncAdapter
     *
     * @param context
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * to schedule the sync adapter periodic execution.
     *
     * @param syncInterval
     * @param flexTime
     */
    public static void configurePeriodicSync(Account account, String authority, int syncInterval, int flexTime) {
        if (account != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //taking advantage of flexible time to do in-exact repeating alarm.
                SyncRequest request = new SyncRequest.Builder()
                        .syncPeriodic(syncInterval, flexTime)
                        .setSyncAdapter(account, authority)
                        .setExtras(new Bundle())
                        .build();
                ContentResolver.requestSync(request);
            } else {
                ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
            }
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        if (newAccount != null) {
            String authority = context.getString(R.string.content_authority);

            //Since we've created an account
            configurePeriodicSync(newAccount, authority, SYNC_INTERVAL, SYNC_FLEXTIME);

            //Without calling setSyncAutomatically, our periodic sync will not be enabled.
            ContentResolver.setSyncAutomatically(newAccount, authority, true);

            //Finally, let's do a sync to get things started.
            syncImmediately(context);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        onAccountCreated(getSyncAccount(context), context);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
