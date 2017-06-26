package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAConfiguration;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;


/**
 * This activity illustrates how to override IMA parameters in application code
 *
 * Supported methods:
 * imaManager.setAdUrlOverride(String)
 * imaManager.setAdTagParameters(Map<String, String>)
 */
public class CustomConfiguredIMAPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Custom Configured IMA Player";
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
			//Initialize the player
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			ooyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			/** DITA_START:<ph id="ima_custom"> **/
			OoyalaIMAConfiguration imaConfig = new OoyalaIMAConfiguration.Builder().setLocaleOverride("fr").build();
			OoyalaIMAManager imaManager = new OoyalaIMAManager(player, imaConfig);

			// This ad tag returns a midroll video
			imaManager.setAdUrlOverride("http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/7521029/pb_test_mid&ciu_szs=640x480&impl=s&cmsid=949&vid=FjbGRjbzp0DV_5-NtXBVo5Rgp3Sj0R5C&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=[timestamp]");
			// imaManager.setAdTagParameters(null);
			/** DITA_END:</ph> **/

			if (player.setEmbedCode(embedCode)) {
				//Uncomment for Auto-Play
				//player.play();
			}
		}
	}
}