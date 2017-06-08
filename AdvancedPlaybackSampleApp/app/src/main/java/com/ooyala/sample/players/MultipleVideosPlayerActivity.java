package com.ooyala.sample.players;


import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import java.util.ArrayList;

/**
 * This activity illustrates how you can play mutliple Videoes
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 */
public class MultipleVideosPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Multiple Video Playback";
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


			ArrayList<String> list = new ArrayList<>();
			list.add(embedCode);
			list.add("h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx");
			if (player.setEmbedCodes(list)) {
				//Uncomment for Auto-Play
				//player.play();
			} else {
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

