package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.npaw.youbora.plugins.PluginOoyala;
import com.npaw.youbora.youboralib.utils.YBLog;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.youbora.YouboraConfigManager;

import java.util.Map;


/**
 * This activity illustrates Ooyala's Integration with NPAW Youbora Quality of Service tools
 * You can find more information on Ooyala's Support website, or from your CSM
 */
public class NPAWDefaultPlayerActivity extends AbstractHookActivity {
	final String TAG = this.getClass().toString();

	private PluginOoyala pluginOoyala;

	/**
	 * Called when the activity is first created.
	 */
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

			Options playerOptions = new Options.Builder().setUseExoPlayer(true).build();
			player = new OoyalaPlayer(pcode, new PlayerDomain(domain), playerOptions);
			playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
			player.addObserver(this);

			if (player.setEmbedCode(embedCode)) {
				//Uncomment for auto-play
				//player.play();
			} else {
				Log.e(TAG, "Asset Failure");
			}

			//Youbora plugin creation and start monitoring
			YBLog.setDebugLevel(YBLog.YBLogLevelHTTPRequests);

			// Get the Youbora Config map from the helper manager
			Map<String, Object> options = YouboraConfigManager.getYouboraConfig(getApplicationContext());

			// Get title from the example
			((Map<String, Object>) options.get("media")).put("title", getIntent().getExtras().getString("selection_name"));

			pluginOoyala = new PluginOoyala(options);
			pluginOoyala.startMonitoring(player);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "Player Activity Stopped");
		if (null != player) {
			player.suspend();
		}
		if (isFinishing()) {
			pluginOoyala.stopMonitoring();
		} else {
			pluginOoyala.pauseMonitoring();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "Player Activity Restarted");
		if (null != player) {
			player.resume();
		}
		pluginOoyala.resumeMonitoring();
	}
}
