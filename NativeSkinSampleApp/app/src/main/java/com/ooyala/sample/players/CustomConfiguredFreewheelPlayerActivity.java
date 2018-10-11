package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity illustrates how to use Freewheel while manually configuring Freewheel settings
 *
 * Supported parameters for Freewheel Configuration:
 * - fw_android_mrm_network_id
 * - fw_android_ad_server
 * - fw_android_player_profile
 * - FRMSegment
 * - fw_android_site_section_id
 * - fw_android_video_asset_id
 */
public class CustomConfiguredFreewheelPlayerActivity extends AbstractHookActivity {

	protected OptimizedOoyalaPlayerLayoutController playerLayoutController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fw_player_simple_frame_layout);
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

			/** DITA_START:<ph id="freewheel_custom"> **/
			OoyalaFreewheelManager fwManager = new OoyalaFreewheelManager(this, playerLayoutController);

			Map<String, String> freewheelParameters = new HashMap<String, String>();
			freewheelParameters.put("fw_android_mrm_network_id",  "380912");
			freewheelParameters.put("fw_android_ad_server", "http://g1.v.fwmrm.net/");
			freewheelParameters.put("fw_android_player_profile",  "90750:ooyala_android");
			freewheelParameters.put("FRMSegment",  "channel=TEST;subchannel=TEST;section=TEST;mode=online;player=ooyala;beta=n");
			freewheelParameters.put("fw_android_site_section_id", "ooyala_android_internalapp");
			freewheelParameters.put("fw_android_video_asset_id",  "NqcGg4bzoOmMiV35ZttQDtBX1oNQBnT-");

			fwManager.overrideFreewheelParameters(freewheelParameters);
			/** DITA_END:</ph> **/

			player.setEmbedCode(embedCode);
		}
	}
}
