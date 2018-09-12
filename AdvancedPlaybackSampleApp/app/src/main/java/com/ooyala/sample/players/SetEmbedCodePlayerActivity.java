package com.ooyala.sample.players;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;
import com.ooyala.android.OoyalaException;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.sample.R;
import com.ooyala.android.ui.AbstractOoyalaPlayerLayoutController.DefaultControlStyle;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.android.util.DebugMode;


import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you can create a player, play a basic video
 * and how to quickly switch between streams by using the setEmbedCode() method.
 */
public class SetEmbedCodePlayerActivity extends Activity implements OnClickListener, Observer, EmbedTokenGenerator {
    private static final String TAG = "SetEmbedCodeApp";
    private OoyalaPlayer player;

    private Button setEmbed;

    private final String APIKEY = "";
    private final String SECRET = "";

    private final String PCODE = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
    private final String EMBEDCODE_1 = "oza2pxODE6Vm_TTHPKJ68gBD8JQ7oTuj";
    private final String EMBEDCODE_2 = "E4bDRwZTE6rMB8oYrzOsuHSPz0XM0dAV";
    private String embedCode = EMBEDCODE_1;

    private final String ACCOUNT_ID = "pbk-373@ooyala.com";
    private final String PLAYERDOMAIN = "http://www.ooyala.com";

    public static String getName() {
        return "Set Embed Code player activity";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "TEST - onCreate");
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(onUncaughtException);
        try {
            setContentView(R.layout.player_set_embed_code_layout);
        } catch (Exception e) {
            DebugMode.logE( TAG, "Caught!", e );
        }

        setEmbed = (Button) findViewById(R.id.setEmbed);
        setEmbed.setOnClickListener(this);

        // optional localization
        // LocalizationSupport.useLocalizedStrings(LocalizationSupport.loadLocalizedStrings("ja_JP"));
        PlayerDomain domain = null;
        try {
            domain = new PlayerDomain(PLAYERDOMAIN);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            DebugMode.logE(TAG, "Caught!", e);
        }

        final FCCTVRatingConfiguration tvRatingConfiguration = new FCCTVRatingConfiguration.Builder().setDurationSeconds(5).build();

        player = new OoyalaPlayer(PCODE, domain, this, new Options.Builder().setTVRatingConfiguration( tvRatingConfiguration ).setBypassPCodeMatching(true).setUseExoPlayer(true).build());
        OptimizedOoyalaPlayerLayoutController layoutController = new OptimizedOoyalaPlayerLayoutController(
                (OoyalaPlayerLayout) findViewById(R.id.player),
                player,
                DefaultControlStyle.AUTO
        );
        player.addObserver(this);
        player.setVolume(.05f);
    }


    private void setEmbedCode() {

        if (player.setEmbedCode(embedCode)) {
            player.play();
            if (embedCode.equals(EMBEDCODE_1)) {
                embedCode = EMBEDCODE_2;
            } else {
                embedCode = EMBEDCODE_1;
            }
        } else {
            Log.d(TAG, "setEmbedCode failed");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player = null;
    }

    private final Thread.UncaughtExceptionHandler onUncaughtException = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e(TAG, "Uncaught exception", ex);
            showErrorDialog(ex);
        }
    };

    private void showErrorDialog(Throwable t) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exception!");
        builder.setMessage(t.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    public void onClick(View arg0) {
        if (player != null && arg0 == setEmbed) {
            setEmbedCode();
        }
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        Log.d(TAG, "Notification Recieved: " + arg1 + " - state: " + player.getState());
        String notificationName = OoyalaNotification.getNameOrUnknown(arg1);
        if (notificationName == OoyalaPlayer.CONTENT_TREE_READY_NOTIFICATION_NAME) {
            Log.d(TAG, "AD - metadata true!");
        } else if (notificationName == OoyalaPlayer.METADATA_READY_NOTIFICATION_NAME) {
        } else if (notificationName == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
        }
    }

    @Override
    public void getTokenForEmbedCodes(List<String> embedCodes,
                                      EmbedTokenGeneratorCallback callback) {
        String embedCodesString = "";
        for (String ec : embedCodes) {
            if (ec.equals("")) {
                embedCodesString += ",";
            }
            embedCodesString += ec;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("account_id", ACCOUNT_ID);

        String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;
        EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

        URL tokenUrl  = urlGen.secureURL("http://player.ooyala.com", uri, params);

        callback.setEmbedToken(tokenUrl.toString());
    }


}

