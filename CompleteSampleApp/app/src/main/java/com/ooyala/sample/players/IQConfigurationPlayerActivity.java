package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.analytics.IqConfiguration;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can use IQConfiguration to set various IQ settings
 */
public class IQConfigurationPlayerActivity extends AbstractHookActivity {

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
			// Create an IQConfiguration class
			IqConfiguration iqConfiguration = new IqConfiguration.Builder().setPlayerID("Custom Android Player ID").build();

			// Put the IQConfiguration class into the Options
			Options options = new Options.Builder().setIqConfiguration(iqConfiguration).setUseExoPlayer(true).build();

			//Initialize the player
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);
			player.setEmbedCode(embedCode);
		}
	}
}

