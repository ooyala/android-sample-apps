package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.CustomOverlay;

/**
 * This activity illustrates how you can implement Custom Overlays for the OoyalaPlayer
 *
 * The class CustomOverlay is inserted into the player using setInlineOverlay
 */
public class CustomOverlayPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Custom Overlay";
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
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);

			//Insert the new overlay into the LayoutController
			CustomOverlay overlay = new CustomOverlay(this, playerLayout);
			playerLayoutController.setInlineOverlay(overlay);
			playerLayoutController.setFullscreenOverlay(overlay);
			player.addObserver(this);

			if (player.setEmbedCode(embedCode)) {
				//Uncomment for Auto-Play
				//player.play();
			}
		}
	}
}
