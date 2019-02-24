package net.aung.sunshine.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by aung on 2/26/16.
 */
public class SunshineInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Intent intentToGCMRegistration = new Intent(this, SunshineGCMRegistrationService.class);
        startService(intentToGCMRegistration);
    }
}
