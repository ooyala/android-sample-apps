package com.ooyala.sample.players;

import android.os.Bundle;
import android.util.Log;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.imasdk.IMAAdErrorListener;
import com.ooyala.android.imasdk.IMAAdEventListener;
import com.ooyala.android.imasdk.IMAAdsLoadedListener;
import com.ooyala.android.imasdk.IMAContainerUpdatedListener;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.CustomPlayerInfo;

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
public class PreconfiguredIMAPlayerActivity extends AbstractHookActivity
		implements IMAAdErrorListener, IMAAdEventListener, IMAAdsLoadedListener, IMAContainerUpdatedListener {
	final String TAG = this.getClass().toString();
	public final static String getName() {
		return "Preconfigured IMA Player";
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if(asked) {
			// Get the SkinLayout from our layout xml
			skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaSkin);

			// Create the OoyalaPlayer, with some built-in UI disabled
			PlayerDomain playerDomain = new PlayerDomain(domain);
			Options options = null;
			if(selectedFormat.equalsIgnoreCase("default"))
				options = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true).build();
			else
				options = new Options.Builder().setShowNativeLearnMoreButton(false).setShowPromoImage(false).setUseExoPlayer(true).setPlayerInfo(new CustomPlayerInfo(selectedFormat)).build();
			player = new OoyalaPlayer(pcode, playerDomain, options);

			//Create the SkinOptions, and setup React
			SkinOptions skinOptions = new SkinOptions.Builder().build();
			playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);
			//Add observer to listen to fullscreen open and close events
			playerLayoutController.addObserver(this);

			player.addObserver(this);

			@SuppressWarnings("unused")
			OoyalaIMAManager imaManager = new OoyalaIMAManager(player, skinLayout);
			imaManager.setOnAdErrorListener(this);
			imaManager.setOnAdEventListener(this);
			imaManager.setAdsLoadedListener(this);
			imaManager.setContainerUpdatedListener(this);

			if (player.setEmbedCode(embedCode)) {
				if(autoPlay)
					player.play();
			}
		}
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_skin_simple_layout);
		completePlayerSetup(asked);
	}

	@Override
	public void onAdError(AdErrorEvent adErrorEvent) {
		Log.d(TAG, String.format("IMA Ad Manager: error %s.", adErrorEvent.getError().getMessage()));
	}

	@Override
	public void onAdEvent(AdEvent adEvent) {
		Log.d(TAG, String.format("IMA Ad Manager: event %s.", adEvent.getType().name()));
	}

	@Override
	public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
		Log.d(TAG, "IMA Ad Manager: loaded");
	}

	@Override
	public void onAdsContainerUpdated(AdDisplayContainer adDisplayContainer) {
		Log.d(TAG, "IMA Display Container Updated");
	}
}