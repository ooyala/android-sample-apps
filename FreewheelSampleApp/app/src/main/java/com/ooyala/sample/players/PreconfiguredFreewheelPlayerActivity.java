package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.Observer;

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
public class PreconfiguredFreewheelPlayerActivity extends AbstractHookActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.player_simple_frame_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(final boolean asked) {
		if (asked) {
			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			player.addObserver(this);

			playerLayout = findViewById(R.id.ooyalaPlayer);
			playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);

			@SuppressWarnings("unused")
			OoyalaFreewheelManager fwManager = new OoyalaFreewheelManager(this, playerLayoutController);

			player.setEmbedCode(embedCode);
		}
	}
}
