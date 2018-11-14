package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * This class asks permission for WRITE_EXTERNAL_STORAGE. We need it for automation hooks
 * as we need to write into the SD card and automation will parse this file.
 */
public abstract class AbstractHookActivity extends Activity implements Observer {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    String TAG = this.getClass().toString();

    SDCardLogcatOoyalaEventsLogger log = new SDCardLogcatOoyalaEventsLogger();

    String embedCode;
    String pcode;
    String domain;

    OoyalaPlayer player;

    boolean writePermission = false;
    boolean asked = false;

    // complete player setup after we asked for permission to write into external storage
    abstract void completePlayerSetup(final boolean asked);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            writePermission= true;
            asked = true;
        }

        embedCode = getIntent().getExtras().getString("embed_code");
        pcode = getIntent().getExtras().getString("pcode");
        domain = getIntent().getExtras().getString("domain");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            asked = true;
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                writePermission = true;
            }
            completePlayerSetup(asked);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != player) {
            player.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != player) {
            player.resume();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        final String arg1 = OoyalaNotification.getNameOrUnknown(arg);
        if (arg1.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
            return;
        }

        String text = "Notification Received: " + arg1 + " - state: " + player.getState();
        Log.d(TAG, text);

        if (writePermission) {
            Log.d(TAG, "Writing log to SD card");
            // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
            log.writeToSdcardLog(text);
        }
    }
}
