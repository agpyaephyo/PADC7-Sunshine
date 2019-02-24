package net.aung.sunshine.receivers.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by aung on 2/8/16.
 */
public class PowerDisconnectedReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        /*
        Toast.makeText(context, "Power adapter is being disconnected", Toast.LENGTH_SHORT).show();
        */
    }
}
