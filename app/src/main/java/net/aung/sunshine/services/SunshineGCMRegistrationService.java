package net.aung.sunshine.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import net.aung.sunshine.R;
import net.aung.sunshine.SunshineApplication;
import net.aung.sunshine.utils.GcmUtils;

/**
 * Created by aung on 2/26/16.
 */
public class SunshineGCMRegistrationService extends IntentService {

    public SunshineGCMRegistrationService() {
        super(SunshineGCMRegistrationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (SunshineApplication.TAG) {
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                String gcmRegId = instanceID.getToken(getString(R.string.gcm_default_sender_id),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                Log.i(SunshineApplication.TAG, "GCM REGID : " + gcmRegId);

                //TODO sendRegistrationToBackend();
                GcmUtils.storeRegistrationId(gcmRegId);
            }
        } catch (Exception e) {
            Log.e(SunshineApplication.TAG, e.getMessage());
        }
    }


}
