package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
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
	void completePlayerSetup(boolean asked) {
		if (asked) {
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain));
			playerLayout = findViewById(R.id.ooyalaPlayer);

			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			if (player.setEmbedCode(embedCode)) {
				//Uncomment for Auto Play
				//player.play();
			}
			else {
				Log.e(TAG, "Asset Failure");
			}
		}
	}
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_simple_layout);
		completePlayerSetup(asked);
	}
}


