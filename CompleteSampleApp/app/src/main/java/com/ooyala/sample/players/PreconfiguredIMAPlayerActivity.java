package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how to use Freewheel when all configuration is stored in Ooyala Servers
 *
 * In order for Freewheel to work this simply, you need the following parameters set in your Third Party Module Parameters
 * - fw_android_ad_server
 * - fw_android_player_profile
 *
 * And an Freewheel Ad Spot configured in Backlot with at least the following:
 * - Network ID
 * - Video Asset Network ID
 * - Site Section ID
 *
 */
public class PreconfiguredIMAPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Preconfigured IMA Player";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getName());
		setContentView(R.layout.player_simple_frame_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			/** DITA_START:<ph id="ima_preconfigured"> **/
			//Initialize the player
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			ooyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			@SuppressWarnings("unused")
			OoyalaIMAManager imaManager = new OoyalaIMAManager(player);

			if (player.setEmbedCode(embedCode)) {
				//Uncomment for Auto-Play
				//player.play();
			}
			/** DITA_END:</ph> **/
		}
	}
}