package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can play basicPlayback Video
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 */
public class BasicPlaybackVideoPlayerActivity extends AbstractHookActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getExtras().getString("selection_name"));
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
			player.addObserver(this);

			if (player.setEmbedCode(embedCode)) {
				//Uncomment for Auto-play
				//player.play();
			}
		}
	}
}