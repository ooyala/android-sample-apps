package com.ooyala.sample.players;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;

/**
 * This activity illustrates how to use Freewheel when all configuration is stored in Ooyala Servers
 *
 * In order for Freewheel to work this simply, you need the following parameters set in your Third Party Module Parameters
 * - fw_android_ad_server
 * - fw_android_player_profile
 *
 * And an Freewheel Ad Spot configured in Backlot with at least the following:
 * - Network ID
 * - Video Asset Network ID
 * - Site Section ID
 *
 */
public class PreconfiguredIMAPlayerActivity extends AbstractHookActivity {
	public final static String getName() {
		return "Preconfigured IMA Player";
	}

	protected OoyalaSkinLayoutController playerLayoutController;

	@Override
	void completePlayerSetup(boolean asked) {
		// Get the SkinLayout from our layout xml
		OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaPlayer);

		// Create the OoyalaPlayer, with some built-in UI disabled
		PlayerDomain domain1 = new PlayerDomain(domain);
		Options options = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true).build();
		player = new OoyalaPlayer(pcode, domain1, options);

		//Create the SkinOptions, and setup React
		SkinOptions skinOptions = new SkinOptions.Builder().build();
		playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
		//Add observer to listen to fullscreen open and close events
		playerLayoutController.addObserver(this);

		player.addObserver(this);

		@SuppressWarnings("unused")
		OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);

		if (player.setEmbedCode(embedCode)) {
//      player.play();
		}
		/** DITA_END:</ph> **/

	}
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_simple_frame_layout);
		completePlayerSetup(asked);
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (playerLayoutController != null) {
			playerLayoutController.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	/** Start DefaultHardwareBackBtnHandler **/
	@Override
	public void invokeDefaultOnBackPressed() {
		super.onBackPressed();
	}
	/** End DefaultHardwareBackBtnHandler **/

	/** Start Activity methods for Skin **/
	@Override
	protected void onPause() {
		super.onPause();
		if (playerLayoutController != null) {
			playerLayoutController.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (playerLayoutController != null) {
			playerLayoutController.onResume( this, this );
		}
	}

	@Override
	public void onBackPressed() {
		if (playerLayoutController != null) {
			playerLayoutController.onBackPressed();
		} else {
			super.onBackPressed();
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (playerLayoutController != null) {
			playerLayoutController.onDestroy();
		}
	}
}







