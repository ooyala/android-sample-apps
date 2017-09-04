package com.ooyala.sample.players;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.freewheelsdk.OoyalaFreewheelManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import com.ooyala.sample.R;


public class CuePointsOptionsFreewheelPlayerActivity extends AbstractHookActivity implements
		OnClickListener {

	private Button setButton;
	private ToggleButton cuePointsButton;
	private ToggleButton adsControlsButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		String localeString = getResources().getConfiguration().locale.toString();
		Log.d(TAG, "locale is " + localeString);
		LocalizationSupport.useLocalizedStrings(LocalizationSupport
				.loadLocalizedStrings(localeString));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_toggle_button_layout);
		completePlayerSetup(asked);
	}

	@Override
	void completePlayerSetup(boolean asked) {
		if (asked) {
			setButton = (Button) findViewById(R.id.setButton);
			setButton.setText("Create Video");
			setButton.setOnClickListener(this);

			cuePointsButton = (ToggleButton) findViewById(R.id.toggleButton1);
			cuePointsButton.setTextOn("CuePoints On");
			cuePointsButton.setTextOff("CuePoints Off");
			cuePointsButton.setChecked(true);

			adsControlsButton = (ToggleButton) findViewById(R.id.toggleButton2);
			adsControlsButton.setTextOn("AdsControls On");
			adsControlsButton.setTextOff("AdsControls Off");
			adsControlsButton.setChecked(true);
		}
	}

	@Override
	public void onClick(View v) {
		if (null != player) {
			player.suspend();
			player.removeVideoView();
		}
		OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
		boolean showAdsControls = this.adsControlsButton.isChecked();
		boolean showCuePoints = this.cuePointsButton.isChecked();
		DebugMode.logD(TAG, "showAdsControls: " + showAdsControls
				+ " showCuePoints: " + showCuePoints);
		Options options = new Options.Builder().setShowAdsControls(showAdsControls)
				.setShowCuePoints(showCuePoints).setUseExoPlayer(true).build();

		player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);
		optimizedOoyalaPlayerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout, player);
		player.addObserver(this);

		OoyalaFreewheelManager freewheelManager = new OoyalaFreewheelManager(this,
				optimizedOoyalaPlayerLayoutController);
		Map<String, String> freewheelParameters = new HashMap<String, String>();
		freewheelParameters.put("fw_android_ad_server", "http://g1.v.fwmrm.net/");
		freewheelParameters
				.put("fw_android_player_profile", "90750:ooyala_android");
		freewheelParameters.put("fw_android_site_section_id",
				"ooyala_android_internalapp");
		freewheelParameters.put("fw_android_video_asset_id", EMBED_CODE);

		freewheelManager.overrideFreewheelParameters(freewheelParameters);
		player.setEmbedCode(EMBED_CODE);
	}
}
