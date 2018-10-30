package com.ooyala.sample.players;

import android.os.Bundle;

import com.npaw.youbora.plugins.PluginOoyala;
import com.npaw.youbora.youboralib.utils.YBLog;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.youbora.YouboraConfigManager;

import java.util.Map;

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
public class NPAWPreconfiguredFreewheelPlayerActivity extends NPAWFWAbstractHookActivity {

	protected OptimizedOoyalaPlayerLayoutController playerLayoutController;
	private PluginOoyala youboraPluginOoyala;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.player_simple_frame_layout_npaw_fw);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(final boolean asked) {
		if (asked) {
			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			player.addObserver(this);

			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);

			@SuppressWarnings("unused")
            OoyalaFreewheelManager fwManager = new OoyalaFreewheelManager(this, playerLayoutController);

			player.setEmbedCode(embedCode);
			YBLog.setDebugLevel(YBLog.YBLogLevelHTTPRequests);
			Map<String, Object> youboraOptions =      YouboraConfigManager.getYouboraConfig(getApplicationContext());
			youboraPluginOoyala = new PluginOoyala(youboraOptions);
			youboraPluginOoyala.startMonitoring(player);
		}
	}
}
