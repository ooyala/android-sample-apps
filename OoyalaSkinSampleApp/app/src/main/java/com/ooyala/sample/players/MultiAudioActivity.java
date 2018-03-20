package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.player.exoplayer.multiaudio.AudioTrack;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Set;

/**
 * This activity illustrates how you can use multi audio methods
 *
 */
public class MultiAudioActivity extends AbstractHookActivity {

  private AudioTrack currentAudioTrack;
  private Set<AudioTrack> audioTracks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_skin_simple_layout);
		completePlayerSetup(asked);
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

			if (!player.setEmbedCode(embedCode)) {
				Log.e(TAG, "Asset Failure");
			}

			player.setDefaultAudioLanguage("eng");
			player.setAudioTrack("Track name");
		}
	}

  @Override
  public void update(Observable arg0, Object argN) {
    super.update(arg0, argN);
	  final String arg1 = OoyalaNotification.getNameOrUnknown(argN);

    // MULTI_AUDIO_ENABLED_NOTIFICATION_NAME is called once on a video start
    if (arg1 == OoyalaPlayer.MULTI_AUDIO_ENABLED_NOTIFICATION_NAME) {
      audioTracks = player.getAvailableAudioTracks();
      if (audioTracks != null) {
				for (AudioTrack track : audioTracks) {
					Log.d("MultiAudio activity", "MultiAudio track language is: " + track.getLanguage());
					if (track.getLanguage().equals("eng")) {
	//            player.setAudioTrack(track);
					break;
					}
				}
      }
      AudioTrack defaultAudioTrack = player.getDefaultAudioTrack();
      String defaultAudioLanguage = player.getDefaultAudioLanguage();
    }

    // AUDIO_TRACK_SELECTED_NOTIFICATION_NAME is called when an audio track was selected
    if (arg1 == OoyalaPlayer.AUDIO_TRACK_SELECTED_NOTIFICATION_NAME) {
      currentAudioTrack = player.getCurrentAudioTrack();
    }
  }


}