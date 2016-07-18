package com.ooyala.sample.players;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.adobeanalyticssdk.OoyalaAdobeAnalyticsManager;
import com.ooyala.android.adobeanalyticssdk.OoyalaAdobeHeartbeatConfiguration;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

public class BasicPlayerActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "BasicPlayerActivity";

    final String PLAYER_DOMAIN = "http://www.ooyala.com/";
    final String HB_TRACKING_SERVER = "[INSERT YOUR TRACKING SERVER HERE]";
    final String HB_PUBLISHER = "[INSERT YOUR PROVIDER HERE]";

    protected OoyalaPlayer player;
    protected String embedCode;
    protected String pcode;
    protected OoyalaAdobeAnalyticsManager analyticsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_player);

        embedCode = getIntent().getExtras().getString("embed_code");
        pcode = getIntent().getExtras().getString("pcode");

        OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.playerLayout);
        player = new OoyalaPlayer(pcode, new PlayerDomain(PLAYER_DOMAIN));
        new OoyalaPlayerLayoutController(playerLayout, player);
        player.addObserver(this);

        OoyalaAdobeHeartbeatConfiguration config = OoyalaAdobeHeartbeatConfiguration.builder()
                .heartbeatTrackingServer(HB_TRACKING_SERVER)
                .heartbeatPublisher(HB_PUBLISHER)
                .build();
        analyticsManager = new OoyalaAdobeAnalyticsManager(player,
                config, getApplicationContext());
        analyticsManager.startCapture();

        if (player.setEmbedCode(embedCode)) {
            player.play();
        } else {
            Log.e(TAG, "Player failed when setting embed code.");
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
    protected void onPause() {
        super.onPause();
        if (null != player) {
            player.suspend();
        }
    }

    @Override
    protected void onDestroy() {
        analyticsManager.stopCapture();

        super.onDestroy();
    }

    // Observer methods

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof OoyalaPlayer) {
            OoyalaPlayer player = (OoyalaPlayer) observable;

            String notification = OoyalaNotification.getNameOrUnknown(data);

            if (notification.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) return;

            if (notification.equals(OoyalaPlayer.ERROR_NOTIFICATION_NAME) && null != player.getError()) {
                Log.e(TAG, "Player error: ", player.getError());
                return;
            }

            Log.d(TAG, "Player notification received: " + notification +
                    ", player state: " + player.getState() +
                    ", playhead: " + player.getPlayheadTime());
        }
    }
}
