package com.ooyala.sample.players;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.player.exoplayer.multiaudio.AudioTrack;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.sample.R;

import java.util.List;
import java.util.Observable;

/**
 * This activity illustrates how you can use multi audio methods
 */
public class MultiAudioActivity extends AbstractHookActivity {
  private static final String TAG = MultiAudioActivity.class.getSimpleName();

  private AudioTrack currentAudioTrack;
  private List<AudioTrack> audioTracks;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_skin_multiaudio_layout);
    completePlayerSetup(asked);

    initButtonListener();
  }

  @Override
  void completePlayerSetup(boolean asked) {
    if (asked) {
      // Get the SkinLayout from our layout xml
      skinLayout = findViewById(R.id.ooyalaSkin);

      // Create the OoyalaPlayer, with some built-in UI disabled
      PlayerDomain playerDomain = new PlayerDomain(domain);
      Options options = new Options.Builder()
          .setShowNativeLearnMoreButton(false)
          .setShowPromoImage(false)
          .setUseExoPlayer(true)
          .build();

      player = new OoyalaPlayer(pcode, playerDomain, options);
      playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player);

      //Add observer to listen to fullscreen open and close events
      playerLayoutController.addObserver(this);
      player.addObserver(this);

      OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);

      if (!player.setEmbedCode(embedCode)) {
        Log.e(TAG, "Asset Failure");
      }

      // Show how to set audio settings i.e. default audio language
      //setAudioSettings();
    }
  }

  @Override
  public void update(Observable arg0, Object argN) {
    super.update(arg0, argN);
    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    final Object data = ((OoyalaNotification) argN).getData();

    // MULTI_AUDIO_ENABLED_NOTIFICATION_NAME is called once on a video start.
    if (arg1.equalsIgnoreCase(OoyalaPlayer.MULTI_AUDIO_ENABLED_NOTIFICATION_NAME)) {
      if (data != null && data instanceof Boolean) {
        boolean isMultiAudioEnabled = (Boolean) data;
        Log.d(TAG, isMultiAudioEnabled ? "MultiAudio is enabled" : "MultiAudio is disabled");
      }

      // This method demonstrate how to obtain default audio settings.
      //getDefaultAudioSettings();
    }

    // AUDIO_TRACK_SELECTED_NOTIFICATION_NAME is called when an audio track was selected.
    if (arg1.equalsIgnoreCase(OoyalaPlayer.AUDIO_TRACK_SELECTED_NOTIFICATION_NAME)) {
      currentAudioTrack = player.getCurrentAudioTrack();
    }
  }

  private void initButtonListener() {
    // Set an audio track.
    // Notice: when you set an audio track it doesn't set as a default audio track
    Button setTrackButton = findViewById(R.id.setTrackButton);
    setTrackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // This method shows how to retrieve the list of available audio tracks and
        // set audio with English language.
        setAudioTrack();
      }
    });
  }

  private void setAudioSettings() {
    player.setDefaultAudioLanguage("eng");
  }

  private void setAudioTrack() {
    if (player == null) {
      Log.e(TAG, "Player is null");
      return;
    }
    audioTracks = player.getAvailableAudioTracks();
    if (audioTracks != null) {
      String language = "eng";
      for (AudioTrack track : audioTracks) {
        Log.d(TAG, "MultiAudio track language is: " + track.getLanguage());
        if (TextUtils.equals(track.getLanguage(), language)
          && currentAudioTrack != null && !track.equals(currentAudioTrack)) {
          player.setAudioTrack(track);
          break;
        }
      }
    }
  }

  private void getDefaultAudioSettings() {
    AudioTrack defaultAudioTrack = player.getDefaultAudioTrack();
    String defaultAudioLanguage = player.getDefaultAudioLanguage();
  }
}
