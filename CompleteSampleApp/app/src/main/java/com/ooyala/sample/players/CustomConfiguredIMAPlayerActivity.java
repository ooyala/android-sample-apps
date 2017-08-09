package com.ooyala.sample.players;


import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
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

			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);

			/** DITA_START:<ph id="ima_custom"> **/
			//OoyalaIMAManager imaManager = new OoyalaIMAManager(player);

			/** DITA_END:</ph> **/

			player.setEmbedCode(embedCode);
		}
	}
}

