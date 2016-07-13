package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can set PreventVideoViewSharing such that third party
 * applications cannot screen-capture video
 */
public class PreventVideoViewSharingPlayerActivity extends Activity implements Observer {
    final String TAG = this.getClass().toString();

    String EMBED = null;
    String PCODE = null;
    String DOMAIN = null;

    protected OoyalaPlayerLayoutController playerLayoutController;
    protected OoyalaPlayer player;
    SDCardLogcatOoyalaEventsLogger playbacklog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getExtras().getString("selection_name"));
        setContentView(R.layout.player_simple_layout);
        EMBED = getIntent().getExtras().getString("embed_code");
        PCODE = getIntent().getExtras().getString("pcode");
        DOMAIN = getIntent().getExtras().getString("domain");

        // Initialize playBackLog : Write the sdk events text along with events count to log file in sdcard if the log file already exists
        playbacklog = new SDCardLogcatOoyalaEventsLogger();

        Options options = new Options.Builder().setPreventVideoViewSharing(true).build();

        //Initialize the player
        OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
        player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
        playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
        player.addObserver(this);

        if (player.setEmbedCode(EMBED)) {
            player.play();
        } else {
            Log.e(TAG, "Asset Failure");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "Player Activity Stopped");
        if (player != null) {
            player.suspend();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "Player Activity Restarted");
        if (player != null) {
            player.resume();
        }
    }

    /**
     * Listen to all notifications from the OoyalaPlayer
     */
    @Override
    public void update(Observable arg0, Object argN) {
        if (arg0 != player) {
            return;
        }

        final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
        if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
            return;
        }

        if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
            final String msg = "Error Event Received";
            if (player != null && player.getError() != null) {
                Log.e(TAG, msg, player.getError());
            } else {
                Log.e(TAG, msg);
            }
            return;
        }

        // Automation Hook: to write Notifications to a temporary file on the device/emulator
        String text = "Notification Received: " + arg1 + " - state: " + player.getState();
        // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
        playbacklog.writeToSdcardLog(text);

        Log.d(TAG, text);
    }

}
