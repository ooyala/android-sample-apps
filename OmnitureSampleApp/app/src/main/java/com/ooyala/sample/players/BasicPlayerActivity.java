package com.ooyala.sample.players;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.adobeanalyticssdk.OoyalaAdobeAnalyticsManager;
import com.ooyala.android.adobeanalyticssdk.OoyalaAdobeHeartbeatConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

public class BasicPlayerActivity extends AppCompatActivity implements Observer,DefaultHardwareBackBtnHandler {
    private static final String TAG = "BasicPlayerActivity";

    final String PLAYER_DOMAIN = "http://www.ooyala.com/";
    final String HB_TRACKING_SERVER = "[INSERT YOUR TRACKING SERVER HERE]";
    final String HB_PUBLISHER = "[INSERT YOUR PROVIDER HERE]";

    protected OoyalaPlayer player;
    protected String EMBEDCODE;
    protected String PCODE;
    protected OoyalaAdobeAnalyticsManager analyticsManager;
    protected OoyalaSkinLayoutController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_player);

        EMBEDCODE = getIntent().getExtras().getString("embed_code");
        PCODE = getIntent().getExtras().getString("pcode");

        //Initialize the player
        OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.playerLayout);

        // Create the OoyalaPlayer, with some built-in UI disabled
        PlayerDomain domain = new PlayerDomain(PLAYER_DOMAIN);
        Options options = new Options.Builder().setShowPromoImage(false).build();
        player = new OoyalaPlayer(PCODE, domain, options);

        //Create the SkinOptions, and setup React
        SkinOptions skinOptions = new SkinOptions.Builder().build();
        controller = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
        player.addObserver(this);

        OoyalaAdobeHeartbeatConfiguration config = OoyalaAdobeHeartbeatConfiguration.builder()
                .heartbeatTrackingServer(HB_TRACKING_SERVER)
                .heartbeatPublisher(HB_PUBLISHER)
                .build();
        analyticsManager = new OoyalaAdobeAnalyticsManager(player,
                config, getApplicationContext());
        analyticsManager.startCapture();

        if (player.setEmbedCode(EMBEDCODE)) {
            //player.play();
        } else {
            Log.e(TAG, "Player failed when setting embed code.");
        }
    }
    // Observer methods
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        controller.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    /** Start DefaultHardwareBackBtnHandler **/
    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }
/** End DefaultHardwareBackBtnHandler **/

    /** Start Activity methods for Skin **/
    @Override
    protected void onPause() {
        super.onPause();
        if (controller != null) {
            controller.onPause();
        }
        Log.d(TAG, "Player Activity Stopped");
        if (player != null) {
            player.suspend();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null) {
            controller.onResume( this, this );
        }
        Log.d(TAG, "Player Activity Restarted");
        if (player != null) {
            player.resume();
        }
    }

    @Override
    public void onBackPressed() {
        if (controller != null) {
            controller.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        analyticsManager.stopCapture();
        if (controller != null) {
            controller.onDestroy();
        }
    }

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
