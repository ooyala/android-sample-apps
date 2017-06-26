package com.ooyala.sample.players;


import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.SampleAdPlugin;

public class PluginPlayerActivity extends AbstractHookActivity {

	public final static String getName() {
		return "Custom Plugin Sample";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_simple_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			Options options = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			SampleAdPlugin plugin = new SampleAdPlugin(this, player);
			player.registerPlugin(plugin);
			if (player.setEmbedCode(embedCode)) {
				//Uncomment for Auto-Play
				//player.play();
			} else {
				Log.d(this.getClass().getName(), "Something Went Wrong!");
			}
		}
	}
}