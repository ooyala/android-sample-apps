package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.SampleVideoPlayerFactory;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by achaudhari on 9/12/16.
 */
public class SampleVideoPlayerActivity extends Activity implements Observer {
    public final static String getName() {
        return "Custom Video Player Sample";
    }
    final String TAG = this.getClass().toString();
    private String EMBED = "";
    String PCODE = null;
    String DOMAIN = null;

    protected OoyalaPlayer player;
    protected OoyalaPlayerLayoutController playerLayoutController;

    // Write the sdk events text along with events count to log file in sdcard if the log file already exists
    SDCardLogcatOoyalaEventsLogger playbacklog = new SDCardLogcatOoyalaEventsLogger();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_simple_layout);

        EMBED = getIntent().getExtras().getString("embed_code");
        PCODE = getIntent().getExtras().getString("pcode");
        DOMAIN = getIntent().getExtras().getString("domain");

        OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
        player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN));
        playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
        player.getMoviePlayerSelector().registerPlayerFactory(new SampleVideoPlayerFactory(999));
        player.addObserver(this);


        if (player.setEmbedCode(EMBED)) {
            //Uncomment for Auto-Play
            //player.play();
        } else {
            Log.d(this.getClass().getName(), "Something Went Wrong!");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.suspend();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (player != null) {
            player.resume();
        }
    }

    /**
     * Listen to all notifications from the OoyalaPlayer
     */
    @Override
    public void update(Observable arg0, Object argN) {
        final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
        if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
            return;
        }

        // Automation Hook: to write Notifications to a temporary file on the device/emulator
        String text="Notification Received: " + arg1 + " - state: " + player.getState();
        // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
        playbacklog.writeToSdcardLog(text);

        Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
    }

}
