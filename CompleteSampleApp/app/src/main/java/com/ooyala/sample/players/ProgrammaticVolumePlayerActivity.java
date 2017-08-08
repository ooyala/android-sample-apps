package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can change the volume of the OoyalaPlayer programmatically.
 * Here you can see the volume being set onCreate, and changed every tick of TIME_CHANGED
 *
 * Over 10 seconds, volume should slowly increase from muted to full volume
 *
 * Please read the APIDocs for OoyalaPlayer.setVolume() for more information
 *
 */
public class ProgrammaticVolumePlayerActivity extends AbstractHookActivity {

	public final static String getName() {
		return "Programmatic Volume";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getName());
		setContentView(R.layout.player_simple_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			//Initialize the player
			Options opts = new Options.Builder().setUseExoPlayer(true).build();
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), opts);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			// You can set the volume anytime after the OoyalaPlayer is instantiated
			player.setVolume(0.0f);

			if (player.setEmbedCode(embedCode)) {
				//player.play();
			}
		}
	}
}