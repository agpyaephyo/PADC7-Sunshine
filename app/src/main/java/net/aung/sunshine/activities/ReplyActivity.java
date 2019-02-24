package net.aung.sunshine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.aung.sunshine.R;
import net.aung.sunshine.utils.NotificationUtils;

public class ReplyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        CharSequence message = getVoiceMessage(getIntent());
        Toast.makeText(this, "Voice Message : " + message, Toast.LENGTH_SHORT).show();
    }

    private CharSequence getVoiceMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(NotificationUtils.EXTRA_VOICE_REPLY);
        }

        return null;
    }
}
