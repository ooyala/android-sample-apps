package com.ooyala.omnituresampleapp;

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

import java.util.Observable;
import java.util.Observer;

public class BasicPlayerActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "BasicPlayerActivity";

    final String PCODE = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
    final String EMBED_CODE = "h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx";
    final String PLAYER_DOMAIN = "http://www.ooyala.com/";
    final String HB_TRACKING_SERVER = "ovppartners.hb.omtrdc.net";
    final String HB_PUBLISHER = "ooyalatester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_player);

        OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.playerLayout);
        OoyalaPlayer player = new OoyalaPlayer(PCODE, new PlayerDomain(PLAYER_DOMAIN));
        new OoyalaPlayerLayoutController(playerLayout, player);
        player.addObserver(this);

        OoyalaAdobeHeartbeatConfiguration config = OoyalaAdobeHeartbeatConfiguration.builder()
                .heartbeatTrackingServer(HB_TRACKING_SERVER)
                .heartbeatPublisher(HB_PUBLISHER)
                .build();
        OoyalaAdobeAnalyticsManager analyticsManager = new OoyalaAdobeAnalyticsManager(player, config, getApplicationContext());
        analyticsManager.startCapture();

        if (player.setEmbedCode(EMBED_CODE)) {
            player.play();
        } else {
            Log.e(TAG, "Player failed when setting embed code.");
        }
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

            Log.d(TAG, "Player notification received: " + notification + ", player state: " + player.getState());
        }
    }
}
