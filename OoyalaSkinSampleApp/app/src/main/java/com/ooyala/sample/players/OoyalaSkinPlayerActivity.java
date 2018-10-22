package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.Environment;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.sample.R;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.utils.CustomPlayerInfo;

import org.json.JSONObject;

/**
 * This activity illustrates how you can play basic playback video using the Skin SDK
 * you can also play Ooyala and VAST advertisements programmatically
 * through the SDK
 *
 */
public class OoyalaSkinPlayerActivity extends AbstractHookActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_skin_simple_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			// Get the SkinLayout from our layout xml
			skinLayout = (OoyalaSkinLayout) findViewById(R.id.ooyalaSkin);
			// Create the OoyalaPlayer, with some built-in UI disabled
			PlayerDomain playerDomain = new PlayerDomain(domain);
			player = new OoyalaPlayer(pcode, playerDomain, getOptions());
			if(isStaging) {
				Log.i(TAG, "Environment Set to Staging:");
				OoyalaPlayer.setEnvironment(Environment.EnvironmentType.STAGING, Environment.PROTOCOL_HTTPS);
			} else {
				OoyalaPlayer.setEnvironment(Environment.EnvironmentType.PRODUCTION, Environment.PROTOCOL_HTTPS);
			}
			//Create the SkinOptions, and setup React
			JSONObject overrides = createSkinOverrides();
			SkinOptions skinOptions = new SkinOptions.Builder().setSkinOverrides(overrides).build();
			playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
			//Add observer to listen to fullscreen open and close events
			playerLayoutController.addObserver(this);
			player.addObserver(this);

			if (player.setEmbedCode(embedCode)) {
				if(autoPlay) {
					player.play();
				}
			} else {
				Log.e(TAG, "Asset Failure");
			}
		}
	}

	/**
	 * Create skin overrides to show up in the skin.
	 * Default commented. Uncomment to show changes to the start screen.
	 * @return the overrides to apply to the skin.json in the assets folder
	 */
	private JSONObject createSkinOverrides() {
		JSONObject overrides = new JSONObject();
//    JSONObject startScreenOverrides = new JSONObject();
//    JSONObject playIconStyleOverrides = new JSONObject();
//    try {
//      playIconStyleOverrides.put("color", "red");
//      startScreenOverrides.put("playButtonPosition", "bottomLeft");
//      startScreenOverrides.put("playIconStyle", playIconStyleOverrides);
//      overrides.put("startScreen", startScreenOverrides);
//    } catch (Exception e) {
//      Log.e(TAG, "Exception Thrown", e);
//    }
		return overrides;
	}
}