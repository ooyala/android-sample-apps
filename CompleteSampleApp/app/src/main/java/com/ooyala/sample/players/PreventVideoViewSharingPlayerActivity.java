package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can set PreventVideoViewSharing such that third party
 * applications cannot screen-capture video
 */
public class PreventVideoViewSharingPlayerActivity extends AbstractHookActivity {

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			Options options = new Options.Builder().setPreventVideoViewSharing(true).setUseExoPlayer(true).build();

			//Initialize the player
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);
			player.setEmbedCode(EMBED_CODE);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getExtras().getString("selection_name"));
		setContentView(R.layout.player_simple_layout);
		completePlayerSetup(asked);
	}
}