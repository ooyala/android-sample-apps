package com.ooyala.sample.players;

import android.os.Bundle;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;
import com.ooyala.android.configuration.FCCTVRatingConfiguration.Position;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you enable TV Ratings Display.
 * <p/>
 * In order for this to work, you must have the following values in your asset's CustomMetadata:
 * tvrating
 * tvratingsurl (optional)
 * tvsubratings (optional)
 */
public class ServerConfiguredTVRatingsPlayerActivity extends AbstractHookActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_simple_frame_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if(asked){
			OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);

			// Configure FCC TV Ratings
			FCCTVRatingConfiguration.Builder builder = new FCCTVRatingConfiguration.Builder();
			FCCTVRatingConfiguration tvRatingConfiguration = builder.setPosition(Position.TopLeft).setDurationSeconds(5).build();
			Options options = new Options.Builder().setTVRatingConfiguration(tvRatingConfiguration).setUseExoPlayer(true).build();

			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), options);
			ooyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);
			player.setEmbedCode(embedCode);
		}
	}
}
