package com.ooyala.sample.players;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.adtech.ContentMetadata;
import com.ooyala.adtech.RequestSettings;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Video;
import com.ooyala.android.pulseintegration.OoyalaPulseManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.pulse.Pulse;
import com.ooyala.pulse.PulseSession;
import com.ooyala.pulse.PulseVideoAd;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.VideoItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The player activity that is responsible for playing a content video and its associated ads.
 */
public class PulsePlayerActivity extends Activity implements Observer, DefaultHardwareBackBtnHandler {

  public final static String getName() {
    return "Pulse Player";
  }

  final String LOG_TAG = this.getClass().toString();
  final int CLICK_THROUGH_REQUEST = 11234;

  // Your publisher code goes here
  final String PCODE  = "tlM2k6i2-WrXX1DE_b8zfhui_eQN";
  final String DOMAIN = "http://ooyala.com";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  private SDCardLogcatOoyalaEventsLogger playbackLog = new SDCardLogcatOoyalaEventsLogger();
  private OoyalaSkinLayoutController playerSkinLayoutController;
  private OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final VideoItem videoItem = getVideoItem();
    setTitle(getName());

    setContentView(R.layout.player_skin_simple_layout);

    // Get the SkinLayout from our layout xml
    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaSkin);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowPromoImage(false).build();

    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup the LayoutController
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    playerSkinLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);

    player.addObserver(this);

    //Create an instance of OoyalaPulseManager and set a listener.
    final OoyalaPulseManager pulseManager = new OoyalaPulseManager(player);
    pulseManager.setListener(new OoyalaPulseManager.Listener() {
      /*
        Called by the plugin to let us create the Pulse session; the metadata retrieved from Backlot is provided here
      */
      @Override
      public PulseSession createPulseSession(OoyalaPulseManager ooyalaPulseManager, Video video, String pulseHost, ContentMetadata contentMetadata, RequestSettings requestSettings) {
        // Replace some of the Backlot metadata with our own local data
        List<Float> midrollPositions = new ArrayList<>();
        for(float f : videoItem.getMidrollPositions()) {
          midrollPositions.add(f);
        }
        requestSettings.setLinearPlaybackPositions(midrollPositions);
        contentMetadata.setTags(Arrays.asList(videoItem.getTags()));
        contentMetadata.setIdentifier(videoItem.getContentId());
        contentMetadata.setCategory(videoItem.getCategory());

        Pulse.setPulseHost(pulseHost, null, null);
        return Pulse.createSession(contentMetadata, requestSettings);
      }

      @Override
      public void openClickThrough(OoyalaPulseManager ooyalaPulseManager, PulseVideoAd pulseVideoAd) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(pulseVideoAd.getClickthroughURL().toString()));
        startActivity(intent);
        
        // adClickThroughTriggered should be reported when the user has opened the 
        // clickthrough link in a browser. 
        // Note: If there are multiple browsers installed on device, the user will
        // be asked choose a browser or cancel. An accurate implementation should
        // only call adClickThroughTriggered if the browser was actually opened.
        pulseVideoAd.adClickThroughTriggered();
      }
    });

    if (player.setEmbedCode(videoItem.getContentCode())) {
      player.play();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (playerSkinLayoutController != null) {
      playerSkinLayoutController.onPause();
    }
    Log.d(LOG_TAG, "Player Activity Paused");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (playerSkinLayoutController != null) {
      playerSkinLayoutController.onResume( this, this );
    }
    Log.d(LOG_TAG, "Player Activity Resumed");
    if (player != null) {
      player.resume();
    }
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable observable, Object argN) {
    final String argument = OoyalaNotification.getNameOrUnknown(argN);
    if (argument == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text = "Notification Received: " + argument + " - state: " + player.getState();

    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    playbackLog.writeToSdcardLog(text);

    Log.d(LOG_TAG, "Notification Received: " + argument + " - state: " + player.getState());
  }

  /*
    Extras
   */

  /**
   * Create a VideoItem from the bundled information send to this activity.
   * @return The created {@link VideoItem}.
   */
  public VideoItem getVideoItem() {
    VideoItem videoItem = new VideoItem();

    videoItem.setTags(getIntent().getExtras().getStringArray("contentMetadataTags"));
    videoItem.setMidrollPositions(getIntent().getExtras().getFloatArray("midrollPositions"));
    videoItem.setContentTitle(getIntent().getExtras().getString("contentTitle"));
    videoItem.setContentId(getIntent().getExtras().getString("contentId"));
    videoItem.setCategory(getIntent().getExtras().getString("category"));
    videoItem.setContentCode(getIntent().getExtras().getString("embedCode"));

    return videoItem;
  }

  /** Start DefaultHardwareBackBtnHandler **/
  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data){
      if((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == CLICK_THROUGH_REQUEST){
        Log.i("DemoIntegration","Came back from clickthrough");
        onResume();
      }
  }
}
