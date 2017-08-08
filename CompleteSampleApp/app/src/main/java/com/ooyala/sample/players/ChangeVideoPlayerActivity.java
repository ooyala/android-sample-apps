package com.ooyala.sample.players;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;

/**
 * This activity illustrates how you can insert Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 * If you want to play an advertisement immediately, you can set the time of your Ad Spot to
 * the current playhead time
 *
 *
 */
public class ChangeVideoPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Change Video Programatically";
	}

	private String EMBED_TWO = "h4aHB1ZDqV7hbmLEv4xSOx3FdUUuephx";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getName());
		setContentView(R.layout.player_double_button_layout);
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
				//Uncomment for Auto-Play
				//player.play();
			}

			/** DITA_START:<ph id="insert_ad_vast"> **/
			//Setup the left button, which will immediately insert a VAST advertisement
			Button leftButton = (Button) findViewById(R.id.doubleLeftButton);
			leftButton.setText("Play Video 1");
			leftButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					player.setEmbedCode(embedCode);
					player.play();
				}
			});
			/** DITA_END:</ph> **/

			//Setup the right button, which will immediately insert an Ooyala advertisement
			Button rightButton = (Button) findViewById(R.id.doubleRightButton);
			rightButton.setText("Play Video 2");
			rightButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					player.setEmbedCode(EMBED_TWO);
					player.play();
				}
			});
		}
	}
}